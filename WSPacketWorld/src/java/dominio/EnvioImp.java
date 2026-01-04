package dominio;

import dto.Respuesta;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Envio;
import pojo.Paquete;
import pojo.Sucursal;
import utilidades.CalculadoraEnvios;

public class EnvioImp {

    public static Respuesta registrar(Envio envio) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        SqlSession conexion = MyBatisUtil.getSession();
        if (conexion != null) {
            try {
                // Registrar el envío (estatus inicial = 1)
                int filasAfectadas = conexion.insert("envio.registrar", envio);
                conexion.commit();

                if (filasAfectadas > 0) {
                    Map<String, Object> historial = new HashMap<>();
                    historial.put("idEnvio", envio.getIdEnvio());
                    historial.put("idEstadoEnvio", 1); 
                    historial.put("comentario", "Envío creado");
                    historial.put("idColaborador", 1); 

                    conexion.insert("envio.registrarHistorial", historial);
                    conexion.commit();

                    respuesta.setError(false);
                    respuesta.setMensaje("Envío registrado correctamente.");
                } else {
                    respuesta.setMensaje("No se pudo registrar el envío.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                respuesta.setMensaje("Error en la base de datos: " + e.getMessage());
            } finally {
                conexion.close();
            }
        } else {
            respuesta.setMensaje("Error de conexión con la base de datos.");
        }
        return respuesta;
    }
    public static Respuesta editar(Envio envio) {
    Respuesta respuesta = new Respuesta();
    SqlSession conexion = MyBatisUtil.getSession();
    if (conexion != null) {
        try {
            int resultado = conexion.update("envio.editar", envio);
            conexion.commit();
            if (resultado > 0) {
                respuesta.setError(false);
                respuesta.setMensaje("Envío actualizado correctamente.");
            } else {
                respuesta.setError(true);
                respuesta.setMensaje("El envío no fue encontrado o no se pudo actualizar.");
            }
        } catch (Exception e) {
            respuesta.setError(true);
            respuesta.setMensaje("Error al actualizar: " + e.getMessage());
        } finally {
            conexion.close();
        }
    } else {
        respuesta.setError(true);
        respuesta.setMensaje("Por el momento no hay conexión a la base de datos.");
    }
    return respuesta;
}

   public static Envio buscarPorGuia(String numeroGuia) {
        Envio envio = null;
        SqlSession conn = MyBatisUtil.getSession();
        
        if (conn != null) {
            try {
                envio = conn.selectOne("envio.buscarPorGuia", numeroGuia);
                
                if (envio != null) {
                    List<Paquete> paquetes = conn.selectList("paquete.obtenerPorEnvio", envio.getIdEnvio());
                    envio.setPaquetes(paquetes);
                }
            } finally {
                conn.close();
            }
        }
        return envio;
    }
    
    
    // se llamará cada vez que se agregue o quite un paquete
    public static void recalcularCosto(int idEnvio) {
        SqlSession conn = MyBatisUtil.getSession();
        if (conn != null) {
            try {
              
                List<Paquete> paquetes = conn.selectList("paquete.obtenerPorEnvio", idEnvio);
                int cantidadPaquetes = (paquetes != null) ? paquetes.size() : 0;
                
               
                Envio envio = conn.selectOne("envio.obtenerPorId", idEnvio); 
                
                if(envio != null){
                     // Obtener CP Origen (Sucursal)
                     Sucursal suc = conn.selectOne("sucursal.obtenerPorId", envio.getIdSucursalOrigen());
                     String cpOrigen = suc.getCodigoPostal();
                     String cpDestino = envio.getCodigoPostalDestino(); 
                     
                     if(cpOrigen != null && cpDestino != null){
                         Double distancia = CalculadoraEnvios.obtenerDistancia(cpOrigen, cpDestino);
                         float nuevoCosto = CalculadoraEnvios.calcularCosto(distancia, cantidadPaquetes);
                         
                         
                         Map<String, Object> params = new HashMap<>();
                         params.put("idEnvio", idEnvio);
                         params.put("costo", nuevoCosto);
                         
                         conn.update("envio.actualizarCosto", params);
                         conn.commit();
                     }
                }
            } catch(Exception e){
                e.printStackTrace();
            } finally {
                conn.close();
            }
        }
    }
    

    public static List<Envio> obtenerTodos() {
        List<Envio> lista = null;
        SqlSession conexion = MyBatisUtil.getSession();
        if (conexion != null) {
            try {
                lista = conexion.selectList("envio.obtenerTodos");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexion.close();
            }
        }
        return lista;
    }
    
    public static List<Envio> obtenerPorConductor(String numeroPersonal) {
        List<Envio> lista = null;
        SqlSession conexion = MyBatisUtil.getSession();
        if (conexion != null) {
            try {
                lista = conexion.selectList("envio.obtenerPorConductor", numeroPersonal);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexion.close();
            }
        }
        return lista;
    }

    public static Respuesta actualizarEstatus(Envio envio) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        SqlSession conexion = MyBatisUtil.getSession();
        if (conexion != null) {
            try {
               int filasAfectadas = conexion.update("envio.actualizarEstatus", envio);
                
                if (filasAfectadas > 0) {
                    Map<String, Object> historial = new HashMap<>();
                    historial.put("idEnvio", envio.getIdEnvio());
                    historial.put("idEstadoEnvio", envio.getIdEstadoActual());
                    historial.put("comentario", "Actualización de estatus");
                    historial.put("idColaborador", 1); 

                    conexion.insert("envio.registrarHistorial", historial);
                }

                conexion.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Estatus actualizado correctamente.");
                } else {
                    respuesta.setMensaje("No se encontró el envío.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                respuesta.setMensaje("Error al actualizar estatus: " + e.getMessage());
            } finally {
                conexion.close();
            }
        } else {
            respuesta.setMensaje("Error de conexión con la base de datos.");
        }
        return respuesta;
    }
    
    public static List<Envio> obtenerPorConductor(int idConductor) {
        List<Envio> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                lista = conexionBD.selectList("envio.obtenerPorConductor", idConductor);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return lista;
    }
}
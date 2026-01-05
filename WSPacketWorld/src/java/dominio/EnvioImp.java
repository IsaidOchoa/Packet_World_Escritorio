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
    
    public static Respuesta actualizarEstatus(
        Envio envio,
        String comentario,
        int idColaborador
    ) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexion = MyBatisUtil.getSession();

        if (conexion == null) {
            respuesta.setError(true);
            respuesta.setMensaje("Error de conexión con la base de datos.");
            return respuesta;
        }

        try {
            // 1. Actualizar estatus principal del envío
            Map<String, Object> params = new HashMap<>();
            params.put("idEnvio", envio.getIdEnvio());
            params.put("idEstadoActual", envio.getIdEstadoActual());

            if (envio.getIdUnidad() != null) {
                params.put("idUnidad", envio.getIdUnidad());
            }

            if (envio.getIdConductor() != null) {
                params.put("idConductor", envio.getIdConductor());
            }

            int filas = conexion.update("envio.actualizarEstatus", params);

            if (filas == 0) {
                conexion.rollback();
                respuesta.setError(true);
                respuesta.setMensaje("No se encontró el envío.");
                return respuesta;
            }

            // 2. Registrar historial
            Map<String, Object> historial = new HashMap<>();
            historial.put("idEnvio", envio.getIdEnvio());
            historial.put("idEstadoEnvio", envio.getIdEstadoActual());
            historial.put("comentario", comentario); // ahora sí
            historial.put("idColaborador", idColaborador);

            conexion.insert("historialEnvio.registrar", historial);

            // 3. Commit
            conexion.commit();
            respuesta.setError(false);
            respuesta.setMensaje("Estatus actualizado correctamente.");

        } catch (Exception e) {
            conexion.rollback();
            respuesta.setError(true);
            respuesta.setMensaje("Error al actualizar estatus: " + e.getMessage());
        } finally {
            conexion.close();
        }

        return respuesta;
    }

    public static Respuesta actualizarEstatusMovil(
        int idEnvio,
        int idEstadoActual,
        String comentario,
        int idColaborador
    ) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexion = MyBatisUtil.getSession();

        if (conexion != null) {
            try {
                System.out.println("[DEBUG] Iniciando actualización envío " + idEnvio);

                Map<String, Object> paramsUpdate = new HashMap<>();
                paramsUpdate.put("idEnvio", idEnvio);
                paramsUpdate.put("idEstadoActual", idEstadoActual);

                int filas = conexion.update("envio.actualizarSoloEstatus", paramsUpdate);
                System.out.println("[DEBUG] Filas actualizadas: " + filas);

                if (filas > 0) {
                    registrarHistorial(
                        conexion,
                        idEnvio,
                        idEstadoActual,
                        comentario,
                        idColaborador
                    );

                    conexion.commit();
                    respuesta.setError(false);
                    respuesta.setMensaje("Estatus actualizado correctamente.");
                    System.out.println("[DEBUG] Commit exitoso");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró el envío.");
                }
            } catch (Exception e) {
                conexion.rollback();
                e.printStackTrace();
                respuesta.setError(true);
                respuesta.setMensaje("Error al actualizar estatus: " + e.getMessage());
            } finally {
                conexion.close();
                System.out.println("[DEBUG] Conexión cerrada");
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Error de conexión con la base de datos.");
        }

        return respuesta;
    }
    
    private static void registrarHistorial(
        SqlSession conexion,
        int idEnvio,
        int idEstadoEnvio,
        String comentario,
        int idColaborador
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("idEnvio", idEnvio);
        params.put("idEstadoEnvio", idEstadoEnvio);
        params.put("comentario", comentario);
        params.put("idColaborador", idColaborador);

        conexion.insert("historialEnvio.registrar", params);
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
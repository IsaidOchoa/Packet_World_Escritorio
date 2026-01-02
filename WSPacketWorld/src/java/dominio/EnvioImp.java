package dominio;

import dto.Respuesta;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Envio;

public class EnvioImp {

    public static Respuesta registrar(Envio envio) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        SqlSession conexion = MyBatisUtil.getSession();
        if (conexion != null) {
            try {
                // 1. Registrar el envío (estatus inicial = 1)
                int filasAfectadas = conexion.insert("envio.registrar", envio);
                conexion.commit();

                if (filasAfectadas > 0) {
                    // 2. Registrar en historial (estatus inicial = 1, colaborador que registra = ?)
                    Map<String, Object> historial = new HashMap<>();
                    historial.put("idEnvio", envio.getIdEnvio());
                    historial.put("idEstadoEnvio", 1); // "Registrado" o similar
                    historial.put("comentario", "Envío creado");
                    historial.put("idColaborador", 1); // TEMPORAL: Reemplazar con usuario logeado

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
        SqlSession conexion = MyBatisUtil.getSession();
        if (conexion != null) {
            try {
                envio = conexion.selectOne("envio.buscarPorGuia", numeroGuia);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexion.close();
            }
        }
        return envio;
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
                // Actualizar envío
                int filasAfectadas = conexion.update("envio.actualizarEstatus", envio);
                
                // Registrar en historial
                if (filasAfectadas > 0) {
                    Map<String, Object> historial = new HashMap<>();
                    historial.put("idEnvio", envio.getIdEnvio());
                    historial.put("idEstadoEnvio", envio.getIdEstadoActual());
                    historial.put("comentario", "Actualización de estatus");
                    historial.put("idColaborador", 1); // TEMPORAL

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
}
package dominio;

import dto.Respuesta;
import java.util.List;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Paquete;

public class PaqueteImp {

    public static Respuesta registrar(Paquete paquete) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);
        
        SqlSession conexion = MyBatisUtil.getSession();

        if (conexion != null) {
            try {
                int filasAfectadas = conexion.insert("paquete.registrar", paquete);
                conexion.commit();
                
                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Paquete registrado correctamente.");
                } else {
                    respuesta.setMensaje("No se pudo registrar el paquete.");
                }
            } catch (Exception e) {
                respuesta.setMensaje("Error BD: " + e.getMessage());
            } finally {
                conexion.close();
            }
        } else {
            respuesta.setMensaje("Error de conexión BD.");
        }
        
        return respuesta;
    }

    public static List<Paquete> obtenerPorEnvio(int idEnvio) {
        List<Paquete> lista = null;
        SqlSession conexion = MyBatisUtil.getSession();
        
        if (conexion != null) {
            try {
                lista = conexion.selectList("paquete.obtenerPorEnvio", idEnvio);
            } catch (Exception e) {
                e.printStackTrace(); 
            } finally {
                conexion.close();
            }
        }
        return lista;
    }
    
    public static Respuesta editar(Paquete paquete) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);
        
        SqlSession conexion = MyBatisUtil.getSession();
        
        if (conexion != null) {
            try {
                int filasAfectadas = conexion.update("paquete.editar", paquete);
                conexion.commit();
                
                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Información del paquete editada correctamente.");
                } else {
                    respuesta.setMensaje("No se pudo editar la información.");
                }
            } catch (Exception e) {
                respuesta.setMensaje("Error al editar: " + e.getMessage());
            } finally {
                conexion.close();
            }
        } else {
            respuesta.setMensaje("Error de conexión con la base de datos.");
        }
        
        return respuesta;
    }
    
    public static Respuesta eliminar(int idPaquete) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexion = MyBatisUtil.getSession();
        
        if (conexion != null) {
            try {
                int filasAfectadas = conexion.delete("paquete.eliminar", idPaquete);
                conexion.commit();
                
                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Paquete eliminado del envío.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró el paquete.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al eliminar: " + e.getMessage());
            } finally {
                conexion.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Error de conexión a la BD.");
        }
        
        return respuesta;
    }
    
    public static Integer contarPorEnvio(int idEnvio) {
        Integer cantidad = null;
        SqlSession conexion = MyBatisUtil.getSession();
        
        if (conexion != null) {
            try {
                cantidad = conexion.selectOne("paquete.contarPorEnvio", idEnvio);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexion.close();
            }
        }
        return cantidad;
    }
    
    public static List<Paquete> obtenerTodos() {
        List<Paquete> lista = null;
        SqlSession conexion = MyBatisUtil.getSession();
        if (conexion != null) {
            try {
                lista = conexion.selectList("paquete.obtenerTodos");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexion.close();
            }
        }
        return lista;
    }
    
    public static Paquete obtenerPorId(int idPaquete) {
        Paquete paquete = null;
        SqlSession conexion = MyBatisUtil.getSession();

        if (conexion != null) {
            try {
                paquete = conexion.selectOne("paquete.obtenerPorId", idPaquete);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexion.close();
            }
        }
        return paquete;
    }
}
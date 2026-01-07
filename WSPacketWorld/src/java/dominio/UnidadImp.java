package dominio;

import dto.Respuesta;
import java.util.HashMap;
import java.util.List;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Unidad;

public class UnidadImp {

    public static List<Unidad> obtenerUnidades() {
        List<Unidad> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                lista = conexionBD.selectList("unidad.obtenerUnidades");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return lista;
    }

   public static Respuesta registrar(Unidad unidad) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        
        if (conexionBD != null) {
            try {
              
                if (unidad.getVin() != null && unidad.getVin().length() >= 4) {
                    String anioStr = String.valueOf(unidad.getAnio());
                    String vinParcial = unidad.getVin().substring(0, 4).toUpperCase();
                    unidad.setNii(anioStr + vinParcial);
                }                
                int filasAfectadas = conexionBD.insert("unidad.registrar", unidad);
                conexionBD.commit();
                
                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Unidad registrada correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se pudo registrar la unidad.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al registrar: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Sin conexión a la BD.");
        }
        return respuesta;
    }

   public static Respuesta editar(Unidad unidad) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        
        if (conexionBD != null) {
            try {
               
                if (unidad.getVin() != null && unidad.getVin().length() >= 4) {
                    String anioStr = String.valueOf(unidad.getAnio());
                    String vinParcial = unidad.getVin().substring(0, 4).toUpperCase();
                    unidad.setNii(anioStr + vinParcial);
                }
                int filasAfectadas = conexionBD.update("unidad.editar", unidad);
                conexionBD.commit();
                
                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Unidad actualizada correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró la unidad.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al editar: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Sin conexión a la BD.");
        }
        return respuesta;
    }

    public static Respuesta eliminar(int idUnidad) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                int filasAfectadas = conexionBD.delete("unidad.eliminar", idUnidad);
                conexionBD.commit();
                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Unidad eliminada correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró la unidad.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error: La unidad podría tener envíos asignados.");
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Sin conexión a la BD.");
        }
        return respuesta;
    }
    public static Respuesta asignarConductor(int idUnidad, Integer idColaborador) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                if (idColaborador != null && idColaborador > 0) {
                    conexionBD.update("unidad.desvincularConductor", idColaborador);
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("idUnidad", idUnidad);
                    params.put("idColaborador", idColaborador);
                    int filas = conexionBD.update("unidad.asignarConductor", params);
                    
                    if (filas > 0) {
                        conexionBD.commit(); 
                        respuesta.setError(false);
                        respuesta.setMensaje("Conductor movido y asignado correctamente.");
                    } else {
                        conexionBD.rollback(); 
                        respuesta.setError(true);
                        respuesta.setMensaje("No se pudo completar la asignación. Se cancelaron los cambios.");
                    }
                } else {
                    int filas = conexionBD.update("unidad.liberarUnidad", idUnidad);
                    conexionBD.commit(); 
                    respuesta.setError(false);
                    respuesta.setMensaje("Unidad liberada.");
                }
            } catch (Exception e) {
                conexionBD.rollback(); 
                respuesta.setError(true);
                respuesta.setMensaje("Error crítico: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Sin conexión a BD");
        }
        return respuesta;
    }
    
    public static Unidad buscarPorColaborador(Integer idColaborador) {
        Unidad unidad = null;
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                unidad = conexionBD.selectOne("unidad.buscarPorColaborador", idColaborador);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return unidad;
    }
    
    public static Respuesta validarVinDuplicado(String vin, Integer idExcluir) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                if (vin != null && !vin.isEmpty()) {
                    Unidad existente = conexionBD.selectOne("unidad.buscarPorVin", vin);
                    if (existente != null && 
                        (idExcluir == null || !existente.getIdUnidad().equals(idExcluir))) {
                        respuesta.setError(true);
                        respuesta.setMensaje("Ya existe una unidad con este VIN.");
                        return respuesta;
                    }
                }
                respuesta.setError(false);
                respuesta.setMensaje("Validación exitosa.");
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error en la validación: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Error de conexión a la base de datos.");
        }
        return respuesta;
    }
}

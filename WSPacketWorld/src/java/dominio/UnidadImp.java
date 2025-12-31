/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominio;

import dto.Respuesta;
import java.util.List;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Unidad;

/**
 *
 * @author pepeg
 */

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
                // -----------------------------------
                
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
                // -------------------------

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
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominio;

import dto.Respuesta;
import java.util.Base64;
import java.util.List;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Colaborador;

/**
 *
 * @author pepeg
 */
public class ColaboradorImp {
 
    public static List<Colaborador> obtenerColaboradores() {
        List<Colaborador> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                lista = conexionBD.selectList("colaborador.obtenerColaboradores");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return lista;
    }
    
    
    
    public static List<Colaborador> buscarColaborador(String filtro) {
        List<Colaborador> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                // Agregamos los % para el LIKE de SQL
                lista = conexionBD.selectList("colaborador.buscarColaborador", "%" + filtro + "%");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return lista;
    }

    public static Respuesta registrar(Colaborador colaborador) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                // Convertir Base64 a Bytes si viene foto
                if(colaborador.getFotoBase64() != null && !colaborador.getFotoBase64().isEmpty()){
                     byte[] fotoBytes = Base64.getDecoder().decode(colaborador.getFotoBase64());
                     colaborador.setFoto(fotoBytes);
                }
                
                int filasAfectadas = conexionBD.insert("colaborador.registrar", colaborador);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Colaborador registrado correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se pudo registrar el colaborador.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al registrar: " + e.getMessage()); // Maneja error de CURP/Correo duplicado
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Error de conexión a la base de datos.");
        }
        return respuesta;
    }

    public static Respuesta editar(Colaborador colaborador) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                // Convertir Base64 a Bytes si cambió la foto
                if(colaborador.getFotoBase64() != null && !colaborador.getFotoBase64().isEmpty()){
                     byte[] fotoBytes = Base64.getDecoder().decode(colaborador.getFotoBase64());
                     colaborador.setFoto(fotoBytes);
                }

                int filasAfectadas = conexionBD.update("colaborador.editar", colaborador);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Colaborador actualizado correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró el colaborador para actualizar.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al actualizar: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Error de conexión a la base de datos.");
        }
        return respuesta;
    }

    public static Respuesta eliminar(int idColaborador) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                int filasAfectadas = conexionBD.delete("colaborador.eliminar", idColaborador);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Colaborador eliminado correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró el colaborador a eliminar.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al eliminar (puede tener envíos asociados): " + e.getMessage());
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


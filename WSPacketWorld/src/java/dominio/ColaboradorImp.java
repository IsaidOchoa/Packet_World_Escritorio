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

    // =============== PERFIL ===============
    public static Respuesta editarPerfil(Colaborador colaborador) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                int filasAfectadas = conexionBD.update("colaborador.editarPerfil", colaborador);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Perfil actualizado correctamente.");
                } else {
                    respuesta.setMensaje("No se encontró el colaborador.");
                }
            } catch (Exception e) {
                respuesta.setMensaje("Error al actualizar el perfil: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setMensaje("Error de conexión a la base de datos.");
        }
        return respuesta;
    }

    // =============== FOTO ===============
    public static Respuesta actualizarFoto(Colaborador colaborador) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                // Convertir Base64 a bytes
                if (colaborador.getFotoBase64() != null && !colaborador.getFotoBase64().isEmpty()) {
                    byte[] fotoBytes = Base64.getDecoder().decode(colaborador.getFotoBase64());
                    colaborador.setFoto(fotoBytes);
                }

                int filasAfectadas = conexionBD.update("colaborador.actualizarFoto", colaborador);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Foto actualizada correctamente.");
                } else {
                    respuesta.setMensaje("No se encontró el colaborador.");
                }
            } catch (Exception e) {
                respuesta.setMensaje("Error al actualizar la foto: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setMensaje("Error de conexión a la base de datos.");
        }
        return respuesta;
    }

    // =============== CONTRASEÑA ===============
    public static Respuesta cambiarPassword(Integer idColaborador, String passwordActual, String passwordNueva) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                // 1. Obtener el colaborador para verificar password actual
                Colaborador actual = conexionBD.selectOne("colaborador.obtenerPorId", idColaborador);
                if (actual == null) {
                    respuesta.setMensaje("Colaborador no encontrado.");
                    return respuesta;
                }

                // 2. Verificar que la password actual sea correcta
                if (!actual.getPassword().equals(passwordActual)) {
                    respuesta.setMensaje("La contraseña actual es incorrecta.");
                    return respuesta;
                }

                // 3. Validar complejidad de la nueva contraseña (ej: mínimo 8 caracteres)
                if (passwordNueva == null || passwordNueva.length() < 8) {
                    respuesta.setMensaje("La nueva contraseña debe tener al menos 8 caracteres.");
                    return respuesta;
                }

                // 4. Actualizar
                Colaborador nuevo = new Colaborador();
                nuevo.setIdColaborador(idColaborador);
                nuevo.setPassword(passwordNueva);

                int filasAfectadas = conexionBD.update("colaborador.cambiarPassword", nuevo);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Contraseña actualizada correctamente.");
                } else {
                    respuesta.setMensaje("No se pudo actualizar la contraseña.");
                }
            } catch (Exception e) {
                respuesta.setMensaje("Error al cambiar la contraseña: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
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
    
    // Obtiene el perfil completo del colaborador (incluyendo fotoBase64)
    public static Colaborador obtenerPorIdCompleto(int idColaborador) {
        Colaborador colaborador = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                colaborador = conexionBD.selectOne("colaborador.obtenerPorIdCompleto", idColaborador);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return colaborador;
    }

    // Obtiene solo la foto en Base64 (útil si solo necesitas la imagen)
    public static String obtenerFotoPorId(int idColaborador) {
        String fotoBase64 = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                fotoBase64 = conexionBD.selectOne("colaborador.obtenerFotoPorId", idColaborador);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return fotoBase64;
    }
}


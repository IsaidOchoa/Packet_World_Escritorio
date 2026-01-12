package dominio;

import dto.Respuesta;
import dto.ValidacionDuplicadoColaborador;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Colaborador;

public class ColaboradorImp {
    
    public static Colaborador loginMovil(String numeroPersonal, String password) {
        SqlSession session = MyBatisUtil.getSession();
        if (session != null) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("numeroPersonal", numeroPersonal);
                params.put("password", password);
                return session.selectOne("colaborador.loginMovil", params);
            } finally {
                session.close();
            }
        }
        return null;
    }
 
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
                lista = conexionBD.selectList("colaborador.buscarColaborador", "%" + filtro + "%");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return lista;
    }

    public static Colaborador buscarPorNoPersonal(String numeroPersonal) {
        Colaborador colaborador = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                colaborador = conexionBD.selectOne("colaborador.buscarPorNoPersonal", numeroPersonal);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        }
        return colaborador;
    }
    
    public static Respuesta validarDuplicadosColaborador(ValidacionDuplicadoColaborador datos) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                StringBuilder errores = new StringBuilder();

                if (datos.getNumeroPersonal() != null && !datos.getNumeroPersonal().isEmpty()) {
                    Colaborador porNumero = conexionBD.selectOne("colaborador.buscarPorNoPersonal", datos.getNumeroPersonal());
                    if (porNumero != null && 
                        (datos.getIdColaboradorExcluir() == null || 
                         !porNumero.getIdColaborador().equals(datos.getIdColaboradorExcluir()))) {
                        errores.append("Ya existe un colaborador con este número de personal.\n");
                    }
                }

                if (datos.getCurp() != null && !datos.getCurp().isEmpty()) {
                    Colaborador porCurp = conexionBD.selectOne("colaborador.buscarPorCurp", datos.getCurp());
                    if (porCurp != null && 
                        (datos.getIdColaboradorExcluir() == null || 
                         !porCurp.getIdColaborador().equals(datos.getIdColaboradorExcluir()))) {
                        errores.append("Ya existe un colaborador con esta CURP.\n");
                    }
                }

                if (datos.getCorreo() != null && !datos.getCorreo().isEmpty()) {
                    Colaborador porCorreo = conexionBD.selectOne("colaborador.buscarPorCorreo", datos.getCorreo());
                    if (porCorreo != null && 
                        (datos.getIdColaboradorExcluir() == null || 
                         !porCorreo.getIdColaborador().equals(datos.getIdColaboradorExcluir()))) {
                        errores.append("Ya existe un colaborador con este correo.\n");
                    }
                }

                if (datos.getNumeroLicencia() != null && !datos.getNumeroLicencia().isEmpty()) {
                    Colaborador porLicencia = conexionBD.selectOne("colaborador.buscarPorLicencia", datos.getNumeroLicencia());
                    if (porLicencia != null && 
                        (datos.getIdColaboradorExcluir() == null || 
                         !porLicencia.getIdColaborador().equals(datos.getIdColaboradorExcluir()))) {
                        errores.append("Ya existe un colaborador con esta licencia.\n");
                    }
                }

                if (errores.length() > 0) {
                    respuesta.setError(true);
                    respuesta.setMensaje(errores.toString().trim());
                } else {
                    respuesta.setError(false);
                    respuesta.setMensaje("Validación exitosa.");
                }

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

    public static Respuesta registrar(Colaborador colaborador) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
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
                if (e instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    respuesta.setError(true);
                    respuesta.setMensaje("Ya existe un colaborador con ese número de personal.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("Error al registrar: " + e.getMessage());
                }
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Error de conexión a la base de datos.");
        }
        return respuesta;
    }

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

    public static Respuesta actualizarFoto(Colaborador colaborador) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
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

    public static Respuesta cambiarPassword(Integer idColaborador, String passwordActual, String passwordNueva) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                Colaborador actual = conexionBD.selectOne("colaborador.obtenerPorId", idColaborador);
                if (actual == null) {
                    respuesta.setMensaje("Colaborador no encontrado.");
                    return respuesta;
                }

                if (!actual.getPassword().equals(passwordActual)) {
                    respuesta.setMensaje("La contraseña actual es incorrecta.");
                    return respuesta;
                }

                if (passwordNueva == null || passwordNueva.length() < 8) {
                    respuesta.setMensaje("La nueva contraseña debe tener al menos 8 caracteres.");
                    return respuesta;
                }

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

    public static Respuesta eliminar(int idColaboradorAEliminar, int idColaboradorSesion) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                if (idColaboradorAEliminar == idColaboradorSesion) {
                    respuesta.setError(true);
                    respuesta.setMensaje("UPS! No puedes eliminarte a ti mismo.");
                    return respuesta;
                }

                Integer unidadesAsignadas = conexionBD.selectOne("colaborador.tieneUnidadesAsignadas", idColaboradorAEliminar);
                if (unidadesAsignadas != null && unidadesAsignadas > 0) {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se puede eliminar: El colaborador tiene " + unidadesAsignadas + " unidad asignada. Primero desasígnelas.");
                    return respuesta;
                }

                Integer enviosActivos = conexionBD.selectOne("colaborador.tieneEnviosActivos", idColaboradorAEliminar);
                if (enviosActivos != null && enviosActivos > 0) {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se puede eliminar: El colaborador tiene " + enviosActivos + " envío(s) activo(s) asignado(s).");
                    return respuesta;
                }
                
                conexionBD.delete("colaborador.eliminarHistorialPorColaborador", idColaboradorAEliminar);

                int filasAfectadas = conexionBD.delete("colaborador.eliminar", idColaboradorAEliminar);
                conexionBD.commit();

                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Colaborador y su historial eliminados correctamente.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró el colaborador a eliminar.");
                }
            } catch (Exception e) {
                conexionBD.rollback();
                respuesta.setError(true);
                respuesta.setMensaje("Error al eliminar: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Error de conexión a la base de datos.");
        }
        return respuesta;
    }
    
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
    
    public static List<Colaborador> obtenerConductoresPorSucursal(int idSucursal) {
        SqlSession session = MyBatisUtil.getSession();
        if (session != null) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("idSucursal", idSucursal);
                params.put("idRolConductor", 3);
                return session.selectList("colaborador.obtenerConductoresPorSucursal", params);
            } finally {
                session.close();
            }
        }
        return new ArrayList<>();
    }
}
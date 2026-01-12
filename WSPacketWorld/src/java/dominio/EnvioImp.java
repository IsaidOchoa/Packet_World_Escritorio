package dominio;

import dto.Respuesta;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Envio;
import pojo.HistorialEnvio;
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
    
    public static List<HistorialEnvio> obtenerHistorialPorGuia(String numeroGuia) {
        Envio envio = buscarPorGuia(numeroGuia);
        if (envio == null) {
            return null;
        }
        SqlSession conn = MyBatisUtil.getSession();
        List<HistorialEnvio> historial = null;
        if (conn != null) {
            try {
                historial = conn.selectList("historialEnvio.obtenerPorEnvio", envio.getIdEnvio());
            } finally {
                conn.close();
            }
        }

        return historial;
    }

    public static String cotizarEnvio(Envio envio) {
        try {
            if (envio.getIdSucursalOrigen() <= 0) return "Sucursal de origen no válida.";
            if (envio.getCodigoPostalDestino() == null || envio.getCodigoPostalDestino().isEmpty()) 
                return "Falta el Código Postal destino.";

            Sucursal sucursal = SucursalImp.obtenerSucursal(envio.getIdSucursalOrigen());

            if (sucursal == null) return "La sucursal de origen no existe.";
            String cpOrigen = sucursal.getCodigoPostal();

            if (cpOrigen == null || cpOrigen.isEmpty()) 
                return "La sucursal origen no tiene configurado un Código Postal.";

            Double distancia = CalculadoraEnvios.obtenerDistancia(cpOrigen, envio.getCodigoPostalDestino());

            if (distancia == null) {
                return "No se puede procesar el envío: La API de cálculo de distancia no reconoce los códigos postales ingresados.\n\n" +
                       "Códigos postales no compatibles:\n" +
                       "• Origen: " + cpOrigen + "\n" +
                       "• Destino: " + envio.getCodigoPostalDestino() + "\n\n" +
                       "Para registrar envíos, usa códigos postales compatibles con la API:\n" +
                       "• CP Origen de prueba: 91020\n" +
                       "• CP Destino de prueba: 01089, 11000, 44100, 72000\n\n" +
                       "El cálculo de costo es obligatorio para procesar envíos.";
            }

            // Calcular SOLO el costo base (sin paquetes)
            float costoBase = CalculadoraEnvios.calcularCosto(distancia, 0);
            envio.setCostoBase(costoBase);
            envio.setCosto(costoBase);
            envio.setCantidadPaquetes(0);
            
            return null; 

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al cotizar envío: " + e.getMessage();
        }
    }

    public static List<Envio> obtenerTodos() {
        System.out.println("=== BACKEND: Iniciando obtenerTodos() ===");
        List<Envio> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                lista = conexionBD.selectList("envio.obtenerTodos");
                System.out.println("=== BACKEND: Número de envíos encontrados: " + 
                    (lista != null ? lista.size() : "null"));
            } catch (Exception e) {
                System.err.println("=== BACKEND: ERROR en obtenerTodos():");
                e.printStackTrace();
            } finally {
                conexionBD.close();
            }
        } else {
            System.err.println("=== BACKEND: ERROR: No se pudo obtener la sesión de MyBatis ===");
        }
        return lista != null ? lista : new ArrayList<>();
    }
    
    public static Envio obtenerPorId(int idEnvio) {
        Envio envio = null;
        SqlSession conn = MyBatisUtil.getSession();
        if (conn != null) {
            try {
                envio = conn.selectOne("envio.obtenerPorId", idEnvio);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.close();
            }
        }
        return envio;
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
            historial.put("comentario", comentario);
            historial.put("idColaborador", idColaborador);

            conexion.insert("historialEnvio.registrar", historial);

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
    
    // Metodo para agregar paquete y actualizar costo
    public static Respuesta agregarPaquete(Paquete paquete) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexion = MyBatisUtil.getSession();

        if (conexion != null) {
            try {
                // 1. Guardar el paquete
                conexion.insert("paquete.registrar", paquete);

                // 2. Actualizar cantidad de paquetes y recalcular costo
                actualizarCantidadYCosto(conexion, paquete.getIdEnvio());

                conexion.commit();
                respuesta.setError(false);
                respuesta.setMensaje("Paquete agregado correctamente.");

            } catch (Exception e) {
                conexion.rollback();
                respuesta.setError(true);
                respuesta.setMensaje("Error al agregar paquete: " + e.getMessage());
            } finally {
                conexion.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Error de conexión a la base de datos.");
        }
        return respuesta;
    }
    
    // Método para actualizar costo desde otros controladores
    public static void actualizarCantidadYCosto(int idEnvio) {
        SqlSession conexion = MyBatisUtil.getSession();
        if (conexion != null) {
            try {
                actualizarCantidadYCosto(conexion, idEnvio);
                conexion.commit();
            } catch (Exception e) {
                e.printStackTrace();
                conexion.rollback();
            } finally {
                conexion.close();
            }
        }
    }

    // Metodo para actualizar cantidad y costo
    private static void actualizarCantidadYCosto(SqlSession conexion, int idEnvio) {
        Envio envio = conexion.selectOne("envio.obtenerPorId", idEnvio);
        if (envio != null) {
            Integer cantidad = conexion.selectOne("paquete.contarPorEnvio", idEnvio);
            if (cantidad == null) cantidad = 0;

            float costoExtra = 0.00f;
            if (cantidad >= 2) {
                if (cantidad == 2) costoExtra = 50.00f;
                else if (cantidad == 3) costoExtra = 80.00f;
                else if (cantidad == 4) costoExtra = 110.00f;
                else costoExtra = 150.00f;
            }

            float costoTotal = (float)(envio.getCostoBase() + costoExtra);

            actualizarCostoYCantidad(conexion, idEnvio, costoTotal, cantidad, envio.getCostoBase());
        }
    }
    // Metodo para actualizar SOLO costo y cantidad
    private static void actualizarCostoYCantidad(SqlSession conexion, int idEnvio, float costoTotal, int cantidadPaquetes, double costoBase) {
        Map<String, Object> params = new HashMap<>();
        params.put("idEnvio", idEnvio);
        params.put("costo", costoTotal);
        params.put("costoBase", costoBase);
        params.put("cantidadPaquetes", cantidadPaquetes);

        conexion.update("envio.actualizarCostoYCantidad", params);
    }
}
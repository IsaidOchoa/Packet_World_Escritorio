package dominio;

import dto.Respuesta;
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

    
    
    public static void recalcularCosto(int idEnvio) {
    SqlSession conn = MyBatisUtil.getSession();
    if (conn != null) {
        try {
            Envio envio = conn.selectOne("envio.obtenerPorId", idEnvio);
            if (envio != null) {
        Sucursal suc = conn.selectOne("sucursal.obtenerPorId", envio.getIdSucursalOrigen());
        String cpOrigen = (suc != null) ? suc.getCodigoPostal() : null;
        String cpDestino = envio.getCodigoPostalDestino();
        
        List<Paquete> paquetes = conn.selectList("paquete.obtenerPorEnvio", idEnvio);
        int cantidad = (paquetes != null) ? paquetes.size() : 0;

        // VERIFICACIÓN IMPORTANTE
        if (cpOrigen != null && cpDestino != null && !cpOrigen.isEmpty() && !cpDestino.isEmpty()) {
            Double distancia = CalculadoraEnvios.obtenerDistancia(cpOrigen, cpDestino);
            
            if (distancia == null) {
                System.err.println(">> ADVERTENCIA: Distancia NULL. Usando 0.0 para cobrar paquetes.");
                distancia = 0.0; 
            }

            float nuevoCosto = CalculadoraEnvios.calcularCosto(distancia, cantidad);
            envio.setCosto(nuevoCosto);
            conn.update("envio.editar", envio); 
            conn.commit();
            System.out.println(">> Costo actualizado a: $" + nuevoCosto);

        } else {
            // AGREGA ESTO: Si entra aquí, es la razón por la que no actualiza el costo
            System.err.println(">> ERROR LOGICO: No se puede cotizar. Faltan códigos postales.");
            System.err.println(">> Origen: " + cpOrigen + " | Destino: " + cpDestino);
        }
    }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }

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
                //MENSAJE ESTRICO Y CLARO
                return "No se puede procesar el envío: La API de cálculo de distancia no reconoce los códigos postales ingresados.\n\n" +
                       "Códigos postales no compatibles:\n" +
                       "• Origen: " + cpOrigen + "\n" +
                       "• Destino: " + envio.getCodigoPostalDestino() + "\n\n" +
                       "Para registrar envíos, usa códigos postales compatibles con la API:\n" +
                       "• CP Origen de prueba: 91020\n" +
                       "• CP Destino de prueba: 01089, 11000, 44100, 72000\n\n" +
                       "El cálculo de costo es obligatorio para procesar envíos.";
            }

            int numPaquetes = (envio.getPaquetes() != null) ? envio.getPaquetes().size() : 0;
            float costoTotal = CalculadoraEnvios.calcularCosto(distancia, numPaquetes);
            envio.setCosto(costoTotal);

            return null; 

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al cotizar envío: " + e.getMessage();
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
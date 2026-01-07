package ws;

import com.google.gson.Gson;
import dominio.ColaboradorImp;
import dominio.EnvioImp;
import dto.Respuesta;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pojo.Colaborador;
import pojo.Envio;
import pojo.HistorialEnvio;

@Path("envio")
public class EnvioWS {

    @POST
    @Path("registrar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) {
        Gson gson = new Gson();
        try {
            Envio envio = gson.fromJson(json, Envio.class);
            
            // Validaciones básicas de entrada
            if (envio.getIdCliente() <= 0 || envio.getIdSucursalOrigen() <= 0) {
                return new Respuesta(true, "Faltan datos obligatorios (Cliente o Sucursal).");
            }

            // 1. Delegar el cálculo del costo a la Capa de Negocio (EnvioImp)
            String errorCotizacion = EnvioImp.cotizarEnvio(envio);
            
            if (errorCotizacion != null) {
                // Si hubo error en el cálculo (API caída, CPs mal), lo devolvemos
                return new Respuesta(true, errorCotizacion);
            }

            // 2. Si cotizó bien, procedemos a guardar
            return EnvioImp.registrar(envio);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new Respuesta(true, "Error al procesar registro: " + e.getMessage());
        }
    }

    @PUT
    @Path("editar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta editar(String json) {
        Gson gson = new Gson();
        try {
            Envio envio = gson.fromJson(json, Envio.class);
            
            if (envio.getIdEnvio() != null && envio.getIdEnvio() > 0) {
                
                // 1. Recalcular el costo antes de actualizar (por si cambiaron la dirección)
                String errorCotizacion = EnvioImp.cotizarEnvio(envio);
                
                if (errorCotizacion != null) {
                    return new Respuesta(true, errorCotizacion);
                }

                // 2. Actualizar en BD con el nuevo costo
                return EnvioImp.editar(envio);
            }
            return new Respuesta(true, "Se requiere el ID del envío para editar.");
        } catch (Exception e) {
            return new Respuesta(true, "Error en el servidor: " + e.getMessage());
        }
    }

    // --- RESTO DE MÉTODOS (Getters, Busquedas, etc.) ---
    
    @GET
    @Path("buscar/{numeroGuia}")
    @Produces(MediaType.APPLICATION_JSON)
    public Envio buscarPorGuia(@PathParam("numeroGuia") String numeroGuia) {
        Envio envio = EnvioImp.buscarPorGuia(numeroGuia);
        if (envio == null) throw new NotFoundException("Envío no encontrado.");
        return envio;
    }
    
    @GET
    @Path("historial/{numeroGuia}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HistorialEnvio> obtenerHistorialPorGuia(@PathParam("numeroGuia") String numeroGuia) {
        return EnvioImp.obtenerHistorialPorGuia(numeroGuia);
    }

    @GET
    @Path("todos")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Envio> obtenerTodos() {
        return EnvioImp.obtenerTodos();
    }
    
    @GET
    @Path("{idEnvio}") // La URL será: .../api/envio/15
    @Produces(MediaType.APPLICATION_JSON)
    public Envio obtenerPorId(@PathParam("idEnvio") Integer idEnvio) {
        // Llama a tu implementación de base de datos
        return EnvioImp.obtenerPorId(idEnvio);
    }

    @PUT
    @Path("actualizar-estatus")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta actualizarEstatus(String json) {
        Gson gson = new Gson();
        try {
            Envio envio = gson.fromJson(json, Envio.class);
            if (envio.getIdEnvio() != null && envio.getIdEstadoActual() != null) {
                int idColaborador = 1; // TODO: Obtener de token/sesión
                return EnvioImp.actualizarEstatus(envio, null, idColaborador);
            }
            return new Respuesta(true, "Datos inválidos.");
        } catch (Exception e) {
            return new Respuesta(true, "Error: " + e.getMessage());
        }
    }
    
    @GET
    @Path("conductor/{numeroPersonal}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Envio> obtenerPorConductor(@PathParam("numeroPersonal") String numeroPersonal) {
        Colaborador colaborador = ColaboradorImp.buscarPorNoPersonal(numeroPersonal);
        if (colaborador == null) return new ArrayList<>();
        return EnvioImp.obtenerPorConductor(colaborador.getIdColaborador());
    }
    
    @PUT
    @Path("actualizar-estatus-movil")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta actualizarEstatusMovil(String json) {
        Gson gson = new Gson();
        try {
            Map<String, Object> datos = gson.fromJson(json, Map.class);
            Double idEnvioD = (Double) datos.get("idEnvio");
            Double idEstadoActualD = (Double) datos.get("idEstadoActual");
            String comentario = (String) datos.get("comentario");
            Double idColaboradorD = (Double) datos.get("idColaborador");

            if (idEnvioD == null || idEstadoActualD == null || idColaboradorD == null) {
                return new Respuesta(true, "Faltan datos obligatorios.");
            }
            return EnvioImp.actualizarEstatusMovil(idEnvioD.intValue(), idEstadoActualD.intValue(), comentario, idColaboradorD.intValue());
        } catch (Exception e) {
            return new Respuesta(true, "Error: " + e.getMessage());
        }
    }
}
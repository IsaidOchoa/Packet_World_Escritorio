package ws;

import com.google.gson.Gson;
import dominio.ColaboradorImp;
import dominio.EnvioImp;
import dominio.SucursalImp; 
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
import pojo.Sucursal;
import utilidades.CalculadoraEnvios;

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
            if (envio.getIdCliente() <= 0 || envio.getIdSucursalOrigen() <= 0 ||
                envio.getCalleDestino() == null || envio.getCodigoPostalDestino() == null) {
                return new Respuesta(true, "Faltan datos obligatorios (Cliente, Sucursal o Destino).");
            }
            try {
                Sucursal sucursalOrigen = SucursalImp.obtenerSucursal(envio.getIdSucursalOrigen());
                String cpOrigen = (sucursalOrigen != null) ? sucursalOrigen.getCodigoPostal() : null;
                String cpDestino = envio.getCodigoPostalDestino();

                if (cpOrigen != null && cpDestino != null) {
                    Double distancia = CalculadoraEnvios.obtenerDistancia(cpOrigen, cpDestino);
                    if (distancia == null) {
                        System.out.println("⚠ La API de distancia falló. Usando distancia por defecto.");
                        distancia = 50.0; 
                    }
                    int numPaquetes = (envio.getPaquetes() != null) ? envio.getPaquetes().size() : 0;
                    float costoTotal = CalculadoraEnvios.calcularCosto(distancia, numPaquetes);
                    envio.setCosto(costoTotal);
                    System.out.println(" Costo calculado: $" + costoTotal);
                } else {
                    System.out.println(" No se encontraron los CP (Origen: " + cpOrigen + ", Destino: " + cpDestino + ")");
                    envio.setCosto(150.0); 
                }
            } catch (Exception ex) {
                System.out.println(" Error en cálculo: " + ex.getMessage());
                envio.setCosto(150.0); 
            }
            return EnvioImp.registrar(envio);
        } catch (Exception e) {
            return new Respuesta(true, "Error al registrar el envío: " + e.getMessage());
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
            return EnvioImp.editar(envio);
        }
        return new Respuesta(true, "Se requiere el ID del envío para editar.");
    } catch (Exception e) {
        return new Respuesta(true, "Error en el servidor: " + e.getMessage());
    }
}
    

    
    @GET
    @Path("buscar/{numeroGuia}")
    @Produces(MediaType.APPLICATION_JSON)
    public Envio buscarPorGuia(@PathParam("numeroGuia") String numeroGuia) {
        Envio envio = EnvioImp.buscarPorGuia(numeroGuia);
        if (envio == null) {
            throw new NotFoundException("Envío no encontrado.");
        }
        return envio;
    }

    @GET
    @Path("todos")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Envio> obtenerTodos() {
        return EnvioImp.obtenerTodos();
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

                int idColaborador = 1; // <-- desde sesión, token o hardcode temporal
                String comentario = null; // opcional en escritorio

                return EnvioImp.actualizarEstatus(
                    envio,
                    comentario,
                    idColaborador
                );
            }

            return new Respuesta(true, "Datos inválidos para actualizar estatus.");

        } catch (Exception e) {
            return new Respuesta(true, "Error al actualizar: " + e.getMessage());
        }
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

            return EnvioImp.actualizarEstatusMovil(
                idEnvioD.intValue(),
                idEstadoActualD.intValue(),
                comentario,
                idColaboradorD.intValue()
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new Respuesta(true, "Error al procesar la actualización: " + e.getMessage());
        }
    }
    
    @GET
    @Path("conductor/{numeroPersonal}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Envio> obtenerPorConductor(@PathParam("numeroPersonal") String numeroPersonal) {
        System.out.println("[DEBUG] Buscando envíos para conductor con numeroPersonal: " + numeroPersonal);

        // 1. Buscar colaborador por numeroPersonal
        Colaborador colaborador = ColaboradorImp.buscarPorNoPersonal(numeroPersonal);
        if (colaborador == null) {
            System.out.println("[ERROR] No se encontró colaborador con numeroPersonal: " + numeroPersonal);
            return new ArrayList<>();
        }

        int idConductor = colaborador.getIdColaborador();
        System.out.println("Colaborador encontrado: id=" + idConductor + ", nombre=" + colaborador.getNombre());

        // 2. Obtener envíos por idConductor
        List<Envio> envios = EnvioImp.obtenerPorConductor(idConductor);
        System.out.println("?Se encontraron " + (envios != null ? envios.size() : 0) + " envíos.");

        if (envios != null && !envios.isEmpty()) {
            for (Envio e : envios) {
                System.out.println("Envío ID: " + e.getIdEnvio() + ", Guía: " + e.getNumeroGuia() + ", Estatus: " + e.getEstatus());
            }
        } else {
            System.out.println("No hay envíos asignados a este conductor.");
        }

        return envios;
    }
}
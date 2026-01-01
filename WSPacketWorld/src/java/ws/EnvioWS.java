package ws;

import com.google.gson.Gson;
import dominio.EnvioImp;
import dto.Respuesta;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pojo.Envio;

@Path("envio")
public class EnvioWS {

    @POST
    @Path("registrar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) {
        Gson gson = new Gson();
        Envio envio = gson.fromJson(json, Envio.class);

        // Validar campos obligatorios
        if (envio.getNumeroGuia() == null || envio.getNumeroGuia().trim().isEmpty() ||
            envio.getIdCliente() <= 0 || envio.getIdSucursalOrigen() <= 0 ||
            envio.getIdColoniaDestino() <= 0 || envio.getCalleDestino() == null ||
            envio.getNumeroDestino() == null || envio.getNombreDestinatario() == null) {
            return new Respuesta(true, "Faltan datos obligatorios para registrar el envío.");
        }

        return EnvioImp.registrar(envio);
    }

    @GET
    @Path("buscar/{numeroGuia}")
    @Produces(MediaType.APPLICATION_JSON)
    public Envio buscarPorGuia(@PathParam("numeroGuia") String numeroGuia) {
        Envio envio = EnvioImp.buscarPorGuia(numeroGuia);
        if (envio == null) {
            throw new NotFoundException("Envío con número de guía '" + numeroGuia + "' no encontrado.");
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
        Envio envio = gson.fromJson(json, Envio.class);

        if (envio.getIdEnvio() == null || envio.getIdEnvio() <= 0 ||
            envio.getIdEstadoActual() == null || envio.getIdEstadoActual() <= 0) {
            return new Respuesta(true, "Se requiere el ID del envío y un estatus válido.");
        }

        return EnvioImp.actualizarEstatus(envio);
    }
}
package ws;

import com.google.gson.Gson;
import dominio.EnvioImp; // <--- IMPORTANTE: Para recalcular el costo
import dominio.PaqueteImp;
import dto.Respuesta;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pojo.Paquete;

@Path("paquete")
public class PaqueteWS {

    @POST
    @Path("registrar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrarPaquete(String json) {
    Gson gson = new Gson();
    try {
        Paquete paquete = gson.fromJson(json, Paquete.class);
        
        // La llamada interna ya recalcula el costo con el código corregido arriba
        Respuesta resp = PaqueteImp.registrar(paquete);
        
        return resp;
    } catch (Exception e) {
        return new Respuesta(true, "Error JSON: " + e.getMessage());
    }
    }

    @GET
    @Path("envio/{idEnvio}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Paquete> obtenerPorEnvio(@PathParam("idEnvio") int idEnvio) {
        if(idEnvio > 0){
             return PaqueteImp.obtenerPorEnvio(idEnvio);
        }
        return null; 
    }
    
    @PUT
    @Path("editar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta editarPaquete(String json) {
        Gson gson = new Gson();
        try {
            Paquete paquete = gson.fromJson(json, Paquete.class);
            
            if (paquete.getIdPaquete() == null || paquete.getIdPaquete() <= 0) {
                return new Respuesta(true, "Se requiere el ID del paquete para editarlo.");
            }
            return PaqueteImp.editar(paquete);
            
        } catch (Exception e) {
            return new Respuesta(true, "Error al editar paquete: " + e.getMessage());
        }
    }
    
    @DELETE
    @Path("eliminar/{idPaquete}")
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("idPaquete") int id) { 
        try {
            if(id > 0){
                Respuesta resp = PaqueteImp.eliminar(id);
                
                return resp;
            }
            return new Respuesta(true, "ID de paquete no válido.");
        } catch (Exception e) {
            return new Respuesta(true, "Error al eliminar paquete: " + e.getMessage());
        }
    }
}
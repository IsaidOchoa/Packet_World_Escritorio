package ws;

import com.google.gson.Gson;
import dominio.ClienteImp;
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
import pojo.Cliente;

@Path("cliente")
public class ClienteWS {

    @Path("obtener-todos")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Cliente> obtenerTodos() { 
        return ClienteImp.obtenerClientes(); 
    }

    @Path("registrar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) { 
        Gson gson = new Gson();
        Respuesta resp = new Respuesta();
        try {
            Cliente c = gson.fromJson(json, Cliente.class);
            
            // Validación de datos mínimos
            if(c.getNombre() != null && !c.getNombre().isEmpty() && 
               c.getTelefono() != null && !c.getTelefono().isEmpty()){
                 return ClienteImp.registrar(c);
            } else {
                resp.setError(true);
                resp.setMensaje("El nombre y teléfono son obligatorios.");
            }
        } catch (Exception e) {
            resp.setError(true);
            resp.setMensaje("Error al registrar cliente: " + e.getMessage());
        }
        return resp;
    }
    
    @Path("editar")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta editar(String json) { 
        Gson gson = new Gson();
        Respuesta resp = new Respuesta();
        try {
            Cliente c = gson.fromJson(json, Cliente.class);
            if(c.getIdCliente() > 0){
                 return ClienteImp.editar(c);
            } else {
                resp.setError(true);
                resp.setMensaje("ID inválido para editar.");
            }
        } catch (Exception e) {
            resp.setError(true);
            resp.setMensaje("Error al editar cliente: " + e.getMessage());
        }
        return resp;
    }
    
    @Path("eliminar/{idCliente}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("idCliente") int idCliente) { 
        return ClienteImp.eliminar(idCliente); 
    }
    
    @Path("buscar/{filtro}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Cliente> buscar(@PathParam("filtro") String filtro) { 
        return ClienteImp.buscar(filtro); 
    }
}
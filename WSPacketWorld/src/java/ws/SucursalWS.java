package ws;

import com.google.gson.Gson;
import dominio.SucursalImp;
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
import pojo.Sucursal;

@Path("sucursal")
public class SucursalWS {

    @GET
    @Path("Obtener-todas")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sucursal> getAll() {
        return SucursalImp.obtenerTodas();
    }
    
    @POST
    @Path("registrar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) {
        Gson gson = new Gson();
        Respuesta resp = new Respuesta();
        try {
            Sucursal sucursal = gson.fromJson(json, Sucursal.class);
            
            // Validaciones
            if (sucursal.getNombre() == null || sucursal.getNombre().isEmpty()) {
                 return new Respuesta(true, "El nombre es obligatorio.");
            }
            if (sucursal.getIdColonia() == null || sucursal.getIdColonia() <= 0) {
                 return new Respuesta(true, "Debes seleccionar una colonia.");
            }
            if (sucursal.getCalle() == null || sucursal.getCalle().isEmpty()) {
                 return new Respuesta(true, "La calle es obligatoria.");
            }
            
            return SucursalImp.registrar(sucursal);
            
        } catch (Exception e) {
            resp.setError(true);
            resp.setMensaje("Error en el servidor al registrar sucursal.");
        }
        return resp;
    }
    
    @PUT
    @Path("editar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta editar(String json) {
        Gson gson = new Gson();
        Respuesta resp = new Respuesta();
        try {
            Sucursal sucursal = gson.fromJson(json, Sucursal.class);
            if (sucursal.getIdSucursal() != null && sucursal.getIdSucursal() > 0) {
                 return SucursalImp.editar(sucursal);
            } else {
                 resp.setError(true);
                 resp.setMensaje("ID inválido para editar.");
            }
        } catch (Exception e) {
            resp.setError(true);
            resp.setMensaje("Error en el servidor al editar sucursal.");
        }
        return resp;
    }
    
    @DELETE
    @Path("eliminar/{idSucursal}")
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("idSucursal") int idSucursal) {
        if (idSucursal <= 0) {
             return new Respuesta(true, "ID inválido.");
        }
        return SucursalImp.eliminar(idSucursal);
    }
}
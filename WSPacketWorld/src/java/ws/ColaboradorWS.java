package ws;

import com.google.gson.Gson;
import dominio.ColaboradorImp;
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
import pojo.Colaborador;

@Path("colaborador")
public class ColaboradorWS {

    @Path("obtener-todos")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Colaborador> obtenerTodos() {
        return ColaboradorImp.obtenerColaboradores();
    }

    @Path("registrar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) {
        Gson gson = new Gson();
        Respuesta resp = new Respuesta();
        try {
            Colaborador c = gson.fromJson(json, Colaborador.class);
            
            // Validaciones obligatorias
            if (c.getNombre() != null && !c.getNombre().isEmpty() &&
                c.getNumeroPersonal() != null && !c.getNumeroPersonal().isEmpty() &&
                c.getPassword() != null && !c.getPassword().isEmpty() &&
                c.getIdRol() > 0) {
                
                return ColaboradorImp.registrar(c);
            } else {
                resp.setError(true);
                resp.setMensaje("Faltan datos (Nombre, No. Personal, Contraseña o Rol).");
            }
        } catch (Exception e) {
            resp.setError(true);
            resp.setMensaje("Error al procesar el registro: " + e.getMessage());
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
            Colaborador c = gson.fromJson(json, Colaborador.class);
            
            if (c.getIdColaborador() > 0) {
                return ColaboradorImp.editar(c);
            } else {
                resp.setError(true);
                resp.setMensaje("Se requiere un ID válido para editar.");
            }
        } catch (Exception e) {
            resp.setError(true);
            resp.setMensaje("Error al procesar la edición: " + e.getMessage());
        }
        return resp;
    }

    @Path("eliminar/{idColaborador}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("idColaborador") Integer idColaborador) {
        Respuesta resp = new Respuesta();
        try {
            if (idColaborador != null && idColaborador > 0) {
                return ColaboradorImp.eliminar(idColaborador);
            }
            resp.setError(true);
            resp.setMensaje("ID inválido.");
        } catch (Exception e) {
            resp.setError(true);
            resp.setMensaje("Error al eliminar: " + e.getMessage());
        }
        return resp;
    }
    
    // Método buscar se queda igual porque es GET simple
    @Path("buscar/{filtro}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Colaborador> buscar(@PathParam("filtro") String filtro) {
        return ColaboradorImp.buscarColaborador(filtro);
    }
}
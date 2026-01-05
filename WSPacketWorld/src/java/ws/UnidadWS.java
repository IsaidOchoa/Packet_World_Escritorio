/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import com.google.gson.Gson; 
import dominio.UnidadImp;
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
import pojo.Unidad;

/**
 *
 * @author pepeg
 */
@Path("unidad")
public class UnidadWS {

    @Path("obtener-todas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Unidad> obtenerUnidades() {
        return UnidadImp.obtenerUnidades();
    }

    @Path("registrar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) {
        Gson gson = new Gson();
        Respuesta resp = new Respuesta();

        try {
            Unidad unidad = gson.fromJson(json, Unidad.class);

            if (unidad != null && 
                unidad.getVin() != null && !unidad.getVin().isEmpty() &&
                unidad.getMarca() != null && !unidad.getMarca().isEmpty() &&
                unidad.getModelo() != null && !unidad.getModelo().isEmpty() &&
                unidad.getIdSucursal() > 0) {
                
                return UnidadImp.registrar(unidad);
                
            } else {
                resp.setError(true);
                resp.setMensaje("Faltan datos obligatorios (Marca, Modelo, VIN o Sucursal)");
            }

        } catch (Exception e) {
            resp.setError(true);
            resp.setMensaje("Error al registrar la unidad: " + e.getMessage());
            e.printStackTrace();
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
            Unidad unidad = gson.fromJson(json, Unidad.class);
            
            if (unidad != null && unidad.getIdUnidad() > 0) {
                return UnidadImp.editar(unidad);
            } else {
                resp.setError(true);
                resp.setMensaje("Se requiere un ID válido para editar la unidad.");
            }
            
        } catch (Exception e) {
            resp.setError(true);
            resp.setMensaje("Error al editar la unidad: " + e.getMessage());
        }
        
        return resp;
    }

    @Path("eliminar/{idUnidad}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("idUnidad") Integer idUnidad) {
        Respuesta resp = new Respuesta();
        try {
            if (idUnidad != null && idUnidad > 0) {
                return UnidadImp.eliminar(idUnidad);
            }
            resp.setError(true);
            resp.setMensaje("El ID de la unidad no es válido.");
            
        } catch (Exception e) {
            resp.setError(true);
            resp.setMensaje("Error al eliminar la unidad: " + e.getMessage());
        }
        return resp;
    }
    
}
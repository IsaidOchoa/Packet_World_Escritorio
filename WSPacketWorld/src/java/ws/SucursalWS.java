/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author pepeg
 */


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
        Sucursal sucursal = gson.fromJson(json, Sucursal.class);
        
        // Validar campos obligatorios (Nombre y Dirección completa)
        if (sucursal.getNombre() == null || sucursal.getNombre().isEmpty()) {
             return new Respuesta(true, "El nombre de la sucursal es obligatorio.");
        }
        if (sucursal.getIdColonia() == null || sucursal.getIdColonia() <= 0) {
             return new Respuesta(true, "Debes seleccionar una colonia.");
        }
        if (sucursal.getCalle() == null || sucursal.getCalle().isEmpty()) {
             return new Respuesta(true, "La calle es obligatoria.");
        }
        
        return SucursalImp.registrar(sucursal);
    }
    
    
    @PUT
    @Path("editar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta editar(String json) {
        Gson gson = new Gson();
        Sucursal sucursal = gson.fromJson(json, Sucursal.class);
        
        // Validar ID para editar
        if (sucursal.getIdSucursal() == null) {
             return new Respuesta(true, "Se requiere el ID de la sucursal para editarla.");
        }
        
        return SucursalImp.editar(sucursal);
    }
    
    
    @DELETE
    @Path("eliminar/{idSucursal}")
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("idSucursal") int idSucursal) {
        if (idSucursal <= 0) {
             return new Respuesta(true, "El ID de la sucursal no es válido.");
        }
        return SucursalImp.eliminar(idSucursal);
    }
}


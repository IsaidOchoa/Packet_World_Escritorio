/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import com.google.gson.Gson;
import dominio.PaqueteImp;
import dto.Respuesta;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pojo.Paquete;

/**
 *
 * @author pepeg
 */

  // URL:/PacketWorld/
@Path("paquete")
public class PaqueteWS {

  
    @POST
    @Path("registrar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrarPaquete(String json) {
        Gson gson = new Gson();
        Paquete paquete = gson.fromJson(json, Paquete.class);
        
        // 1. Validaciones básicas antes de ir a la BD
        if (paquete.getIdEnvio() == null || paquete.getDescripcion() == null) {
            // Usamos el constructor de tu clase Respuesta(error, mensaje)
            return new Respuesta(true, "Faltan datos obligatorios (idEnvio, descripcion)");
        }
        
        // 2. Llamar a la implementación
        return PaqueteImp.registrar(paquete); 
    }


    @GET
    @Path("envio/{idEnvio}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Paquete> obtenerPorEnvio(@PathParam("idEnvio") int idEnvio) {
        return PaqueteImp.obtenerPorEnvio(idEnvio);
    }
    
    // URL: .../PacketWorld/paquete/editar
    @PUT
    @Path("editar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta editarPaquete(String json) {
        Gson gson = new Gson();
        Paquete paquete = gson.fromJson(json, Paquete.class);
        
        // Validar que venga el ID del paquete a editar
        if (paquete.getIdPaquete() == null) {
            return new Respuesta(true, "Se requiere el ID del paquete para editarlo.");
        }

        return PaqueteImp.editar(paquete);
    }
}
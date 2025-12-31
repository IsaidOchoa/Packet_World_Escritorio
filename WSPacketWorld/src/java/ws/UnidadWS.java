/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import dominio.UnidadImp;
import dto.Respuesta;
import java.util.List;
import javax.ws.rs.BadRequestException;
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
    public Respuesta registrar(Unidad unidad) {
        if (unidad != null && unidad.getVin() != null && !unidad.getVin().isEmpty()) {
            return UnidadImp.registrar(unidad);
        }
        throw new BadRequestException("Datos de unidad incompletos.");
    }

    @Path("editar")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta editar(Unidad unidad) {
        if (unidad != null && unidad.getIdUnidad() > 0) {
            return UnidadImp.editar(unidad);
        }
        throw new BadRequestException("ID de unidad requerido para editar.");
    }

    @Path("eliminar/{idUnidad}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("idUnidad") Integer idUnidad) {
        if (idUnidad != null && idUnidad > 0) {
            return UnidadImp.eliminar(idUnidad);
        }
        throw new BadRequestException("ID inválido.");
    }
   
}

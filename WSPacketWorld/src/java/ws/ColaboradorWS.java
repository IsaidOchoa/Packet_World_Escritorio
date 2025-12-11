/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import dominio.ColaboradorImp;
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
import pojo.Colaborador;

/**
 *
 * @author pepeg
 */

@Path("colaborador")
public class ColaboradorWS {

    @Path("obtener-todos")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Colaborador> obtenerTodos() {
        return ColaboradorImp.obtenerColaboradores();
    }
    
    @Path("buscar/{filtro}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Colaborador> buscar(@PathParam("filtro") String filtro) {
        return ColaboradorImp.buscarColaborador(filtro);
    }

    @Path("registrar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrar(Colaborador colaborador) {
        if (colaborador != null && colaborador.getNumeroPersonal() != null) {
            return ColaboradorImp.registrar(colaborador);
        }
        throw new BadRequestException("Datos del colaborador inválidos.");
    }

    @Path("editar")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta editar(Colaborador colaborador) {
        // Validamos que venga el ID para poder editar
        if (colaborador != null && colaborador.getIdColaborador() > 0) {
            return ColaboradorImp.editar(colaborador);
        }
        throw new BadRequestException("ID de colaborador necesario para editar.");
    }

    @Path("eliminar/{idColaborador}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("idColaborador") Integer idColaborador) {
        if (idColaborador != null && idColaborador > 0) {
            return ColaboradorImp.eliminar(idColaborador);
        }
        throw new BadRequestException("ID inválido.");
    }
}


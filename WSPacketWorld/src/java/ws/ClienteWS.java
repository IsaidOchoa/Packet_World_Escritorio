/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import dominio.ClienteImp;
import dto.Respuesta;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pojo.Cliente;

/**
 *
 * @author pepeg
 */

@Path("cliente")
public class ClienteWS {
    @Path("obtener-todos")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
        public List<Cliente> obtenerTodos() { return ClienteImp.obtenerClientes(); }

    @Path("registrar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrar(Cliente c) { return ClienteImp.registrar(c); }
}


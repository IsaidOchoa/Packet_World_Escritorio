/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import dominio.AutenticacionImp;
import dto.RSAutenticacionColaborador;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author pepeg
 */

@Path("autenticacion")
public class AutenticacionWS {

    @Path("login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public RSAutenticacionColaborador autenticarColaborador(@FormParam("noPersonal") String noPersonal, 
                                        @FormParam("password") String password){
        
        if(noPersonal != null && !noPersonal.isEmpty() && password != null && !password.isEmpty()){
             return AutenticacionImp.autenticacionColaborador(noPersonal, password);
        }
        
        throw new BadRequestException();
    }
}


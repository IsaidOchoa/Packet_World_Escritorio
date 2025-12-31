/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import dominio.CatalogoImp;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pojo.EstadoEnvio;
import pojo.Rol;
import pojo.TipoUnidad;

/**
 *
 * @author pepeg
 */

@Path("catalogo")
public class CatalogoWS {
    
    @Path("roles")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Rol> obtenerRoles() {
        return CatalogoImp.obtenerRoles();
    }
    
    @Path("tipos-unidad")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TipoUnidad> obtenerTiposUnidad() {
        return CatalogoImp.obtenerTiposUnidad();
    }
    
    @Path("estatus-envio")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<EstadoEnvio> obtenerEstatusEnvio() {
        return CatalogoImp.obtenerEstatusEnvio();
    }
}


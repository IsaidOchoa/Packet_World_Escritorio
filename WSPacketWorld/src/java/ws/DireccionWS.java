/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import dominio.DireccionImp;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pojo.Colonia;
import pojo.Estado;
import pojo.Municipio;

/**
 *
 * @author pepeg
 */

@Path("direccion")
public class DireccionWS {
    
    @Path("estados")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Estado> obtenerEstados() {
        return DireccionImp.obtenerEstados();
    }
    
    @Path("municipios/{idEstado}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Municipio> obtenerMunicipios(@PathParam("idEstado") Integer idEstado) {
        if (idEstado != null && idEstado > 0) {
            return DireccionImp.obtenerMunicipios(idEstado);
        }
        return null;
    }
    
    @Path("colonias/{idMunicipio}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Colonia> obtenerColonias(@PathParam("idMunicipio") Integer idMunicipio) {
        if (idMunicipio != null && idMunicipio > 0) {
            return DireccionImp.obtenerColonias(idMunicipio);
        }
        return null;
    }
    
    @Path("cp/{codigoPostal}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Colonia> buscarPorCP(@PathParam("codigoPostal") String codigoPostal) {
        if (codigoPostal != null && !codigoPostal.isEmpty()) {
            return DireccionImp.buscarPorCP(codigoPostal);
        }
        return null;
    }
    
    @Path("municipio/{idMunicipio}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Municipio obtenerMunicipio(@PathParam("idMunicipio") Integer idMunicipio) {
        if (idMunicipio != null && idMunicipio > 0) {
            return DireccionImp.obtenerMunicipio(idMunicipio);
        }
        return null;
    }
}
    


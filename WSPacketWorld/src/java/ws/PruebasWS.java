/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;



/**
 *
 * @author pepeg
 */
@Path("prueba")
public class PruebasWS {
    @Context
    private UriInfo context;
    
    /**
     * Creates a new instance of PruebasWS
     */
    public PruebasWS() {
    }

    
    
    @Path("probar-conexion")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public boolean probarConexion(){
        SqlSession conexionBD = MyBatisUtil.getSession();
        return (conexionBD!=null);
    }
            
    
}
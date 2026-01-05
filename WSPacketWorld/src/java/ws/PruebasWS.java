/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Colaborador;



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
            
    @Path("test-conexion")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String probarConexion2() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- DIAGNOSTICO MYBATIS ---\n");
        
        SqlSession sess = null;
        try {
            // Intentamos leer tu XML y abrir sesión
            sess = MyBatisUtil.getSession();
            
            if (sess != null) {
                sb.append("[OK] Conexion Exitosa con MyBatis.\n");
                sb.append("     (Se uso tu archivo mybatis-config.xml)\n\n");
                
                // Probamos la consulta real
                try {
                    // Asegúrate que en el XML el namespace sea 'colaborador' y el id 'obtenerColaboradores'
                    List<Colaborador> lista = sess.selectList("colaborador.obtenerColaboradores");
                    sb.append("[OK] Consulta SQL ejecutada.\n");
                    sb.append("    -> Registros encontrados: ").append(lista.size()).append("\n");
                    
                    if (!lista.isEmpty()) {
                        sb.append("    -> Dato: ").append(lista.get(0).getNombre());
                    } else {
                        sb.append("    -> La lista esta vacia. (Verifica si insertaste los datos de prueba)");
                    }
                } catch (Exception sqlE) {
                    sb.append("[ERROR] Fallo el SELECT: ").append(sqlE.getMessage()).append("\n");
                    sqlE.printStackTrace();
                }
            } else {
                sb.append("[ERROR] MyBatis Session es NULL.\n");
                sb.append("Revisa que 'mybatis-config.xml' este en la carpeta correcta (Source Packages).");
            }
        } catch (Exception e) {
            sb.append("[ERROR] Error grave al abrir MyBatis: ").append(e.getMessage());
            e.printStackTrace();
        } finally {
            if (sess != null) sess.close();
        }
        
        return sb.toString();
    }
    
    @GET
    @Path("probar-calculadora/{origen}/{destino}")
    @Produces(MediaType.APPLICATION_JSON)
    public String probarCalculadora(@PathParam("origen") String origen,@PathParam("destino") String destino) {
        
        System.out.println("--- PRUEBA MANUAL DE CALCULADORA ---");
        System.out.println("Origen recibido: " + origen);
        System.out.println("Destino recibido: " + destino);
        
        try {
            Double distancia = utilidades.CalculadoraEnvios.obtenerDistancia(origen, destino);
            
            if (distancia != null) {
                return "{\"mensaje\":\"ÉXITO\", \"distancia\":" + distancia + "}";
            } else {
                return "{\"mensaje\":\"FALLÓ: La calculadora devolvió NULL.\"}";
            }
        } catch (Exception e) {
            return "{\"mensaje\":\"ERROR DE EXCEPCIÓN: " + e.getMessage() + "\"}";
        }
    }
}
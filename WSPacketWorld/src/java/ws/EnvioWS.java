package ws;

import com.google.gson.Gson;
import dominio.EnvioImp;
import dominio.SucursalImp; 
import dto.Respuesta;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pojo.Envio;
import pojo.Sucursal;
import utilidades.CalculadoraEnvios;

@Path("envio")
public class EnvioWS {

    @POST
    @Path("registrar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) {
        Gson gson = new Gson();
        try {
            Envio envio = gson.fromJson(json, Envio.class);

            
            if (envio.getIdCliente() <= 0 || envio.getIdSucursalOrigen() <= 0 ||
                envio.getCalleDestino() == null || envio.getCodigoPostalDestino() == null) {
                return new Respuesta(true, "Faltan datos (Cliente, Sucursal Origen o Dirección Destino).");
            }

            
            try {
               
                Sucursal sucursalOrigen = SucursalImp.obtenerSucursal(envio.getIdSucursalOrigen());
                
                String cpOrigen = (sucursalOrigen != null) ? sucursalOrigen.getCodigoPostal() : null;
                String cpDestino = envio.getCodigoPostalDestino();

                if (cpOrigen != null && cpDestino != null) {
                    
                    Double distancia = CalculadoraEnvios.obtenerDistancia(cpOrigen, cpDestino);
                    
                    
                    int numPaquetes = (envio.getPaquetes() != null) ? envio.getPaquetes().size() : 0;
                    
                    float costoTotal = CalculadoraEnvios.calcularCosto(distancia, numPaquetes);
                    
                    
                    envio.setCosto(costoTotal);
                }
            } catch (Exception ex) {
                System.out.println("No se pudo calcular el costo automáticamente: " + ex.getMessage());
            }

            // 3. Guardar en BD
            return EnvioImp.registrar(envio);
            
        } catch (Exception e) {
            return new Respuesta(true, "Error al registrar el envío: " + e.getMessage());
        }
    }

    
    @GET
    @Path("buscar/{numeroGuia}")
    @Produces(MediaType.APPLICATION_JSON)
    public Envio buscarPorGuia(@PathParam("numeroGuia") String numeroGuia) {
        Envio envio = EnvioImp.buscarPorGuia(numeroGuia);
        if (envio == null) {
            throw new NotFoundException("Envío no encontrado.");
        }
        return envio;
    }

    @GET
    @Path("todos")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Envio> obtenerTodos() {
        return EnvioImp.obtenerTodos();
    }

    @PUT
    @Path("actualizar-estatus")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta actualizarEstatus(String json) {
        Gson gson = new Gson();
        try {
            Envio envio = gson.fromJson(json, Envio.class);
            if (envio.getIdEnvio() > 0 && envio.getIdEstadoActual() > 0) {
                return EnvioImp.actualizarEstatus(envio);
            }
            return new Respuesta(true, "Datos inválidos para actualizar estatus.");
        } catch (Exception e) {
            return new Respuesta(true, "Error al actualizar: " + e.getMessage());
        }
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteescritoriopw.dominio;

import clienteescritoriopw.conexion.ConexionAPI;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.RespuestaHTTP;
import clienteescritoriopw.pojo.Sucursal;
import clienteescritoriopw.utilidad.Constantes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pepeg
 */
public class SucursalImp {
    
    public static List<Sucursal> obtenerSucursales() {
        List<Sucursal> lista = new ArrayList<>();
        String url = Constantes.URL_WS + "sucursal/obtener-todas";
        
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type tipoLista = new TypeToken<ArrayList<Sucursal>>(){}.getType();
                lista = gson.fromJson(respuesta.getContenido(), tipoLista);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
    
    public static Respuesta registrar(Sucursal sucursal) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "sucursal/registrar";
        Gson gson = new Gson();
        String parametros = gson.toJson(sucursal);
        
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "POST", parametros, "application/json");
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error en la petición: " + respuesta.getCodigo());
        }
        return msj;
    }
    
    public static Respuesta editar(Sucursal sucursal) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "sucursal/editar";
        Gson gson = new Gson();
        String parametros = gson.toJson(sucursal);
        
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "PUT", parametros, "application/json");
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al editar: " + respuesta.getCodigo());
        }
        return msj;
    }
    
    public static Respuesta eliminar(int idSucursal) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "sucursal/eliminar/" + idSucursal;
        
        RespuestaHTTP respuesta = ConexionAPI.peticionSinBody(url, "DELETE");
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al eliminar: " + respuesta.getCodigo());
        }
        return msj;
    }
}


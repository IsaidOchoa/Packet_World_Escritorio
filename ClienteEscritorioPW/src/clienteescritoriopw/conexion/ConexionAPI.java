/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteescritoriopw.conexion;

import clienteescritoriopw.pojo.RespuestaHTTP;
import clienteescritoriopw.utilidad.Constantes;
import clienteescritoriopw.utilidad.Utilidades;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConexionAPI {
    
    public static RespuestaHTTP peticionGET(String URL){
        RespuestaHTTP respuesta = new RespuestaHTTP();
        try {
            URL urlWS = new URL(URL);
            HttpURLConnection conexionHTTP = (HttpURLConnection) urlWS.openConnection();
            conexionHTTP.setRequestMethod("GET");
            
            int codigo = conexionHTTP.getResponseCode();
            respuesta.setCodigo(codigo);
            
            if (codigo == HttpURLConnection.HTTP_OK) {
                respuesta.setContenido(Utilidades.streamToString(conexionHTTP.getInputStream()));
            } else {
                respuesta.setContenido(Utilidades.streamToString(conexionHTTP.getErrorStream()));
            }
            
        } catch (MalformedURLException e) {
            respuesta.setCodigo(Constantes.ERROR_MALFORMED_URL);
            respuesta.setContenido(e.getMessage());
        } catch (IOException ex) {
            respuesta.setCodigo(Constantes.ERROR_PETICION);
            respuesta.setContenido(ex.getMessage());
        }
        return respuesta;
    }
      
    public static RespuestaHTTP peticionBody(String URL, String metodoHTTP, String parametros, String contentType) {
        RespuestaHTTP respuesta = new RespuestaHTTP();
        try {
            URL urlWS = new URL(URL);
            HttpURLConnection conexionHTTP = (HttpURLConnection) urlWS.openConnection();
            conexionHTTP.setRequestMethod(metodoHTTP);
            
            // CORRECCIÓN IMPORTANTE: 
            // Usamos la variable 'contentType' que recibimos, no "application/json" fijo.
            // Esto permite que el Login mande formularios y el Registro mande JSON.
            conexionHTTP.setRequestProperty("Content-Type", contentType);
            
            conexionHTTP.setDoOutput(true);
            OutputStream os = conexionHTTP.getOutputStream();
            os.write(parametros.getBytes("UTF-8"));
            os.flush();
            os.close();
            
            int codigo = conexionHTTP.getResponseCode();
            respuesta.setCodigo(codigo);
            
            if (codigo == HttpURLConnection.HTTP_OK) {
                respuesta.setContenido(Utilidades.streamToString(conexionHTTP.getInputStream()));
            } else {
                respuesta.setContenido(Utilidades.streamToString(conexionHTTP.getErrorStream()));
            }
            
        } catch (MalformedURLException e) {
            respuesta.setCodigo(Constantes.ERROR_MALFORMED_URL);
            respuesta.setContenido(e.getMessage());
        } catch (IOException ex) {
            respuesta.setCodigo(Constantes.ERROR_PETICION);
            respuesta.setContenido(ex.getMessage());
        }
        return respuesta;
    }
    
    // El método peticionSinBody queda igual que antes (con el getErrorStream)
    public static RespuestaHTTP peticionSinBody(String URL, String metodoHTTP){
        RespuestaHTTP respuesta = new RespuestaHTTP();
        try {
            URL urlWS = new URL(URL);
            HttpURLConnection conexionHTTP = (HttpURLConnection) urlWS.openConnection();
            conexionHTTP.setRequestMethod(metodoHTTP);
            
            int codigo = conexionHTTP.getResponseCode();
            respuesta.setCodigo(codigo);
            
            if (codigo == HttpURLConnection.HTTP_OK) {
                respuesta.setContenido(Utilidades.streamToString(conexionHTTP.getInputStream()));
            } else {
                respuesta.setContenido(Utilidades.streamToString(conexionHTTP.getErrorStream()));
            }
            
        } catch (MalformedURLException e) {
            respuesta.setCodigo(Constantes.ERROR_MALFORMED_URL);
            respuesta.setContenido(e.getMessage());
        } catch (IOException ex) {
            respuesta.setCodigo(Constantes.ERROR_PETICION);
            respuesta.setContenido(ex.getMessage());
        }
        return respuesta;
    }
}
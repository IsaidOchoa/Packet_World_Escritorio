package clienteescritoriopw.dominio;

import clienteescritoriopw.conexion.ConexionAPI;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Envio;
import clienteescritoriopw.pojo.RespuestaHTTP;
import clienteescritoriopw.utilidad.Constantes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class EnvioImp {

    
    public static Respuesta registrar(Envio envio) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "envio/registrar";
        
        Gson gson = new Gson();
        String parametros = gson.toJson(envio);
        
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "POST", parametros, "application/json");
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al conectar con el servidor: " + respuesta.getContenido());
        }
        return msj;
    }
    
    public static Respuesta editar(Envio envio) {
    Respuesta msj = new Respuesta();
    String url = Constantes.URL_WS + "envio/editar";
    Gson gson = new Gson();
    String parametros = gson.toJson(envio);
    
    RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "PUT", parametros, "application/json"); 
    if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
        msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
    } else {
        msj.setError(true);
        msj.setMensaje("Error al editar envío: " + respuesta.getContenido());
    }
    return msj;
}

    public static List<Envio> obtenerTodos() {
        String url = Constantes.URL_WS + "envio/todos";
        System.out.println("URL de obtención de todos los envíos: " + url);

        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        System.out.println("Código de respuesta HTTP: " + respuesta.getCodigo());

        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            String contenido = respuesta.getContenido();
            System.out.println("Contenido JSON recibido (primeros 500 chars): " + 
                (contenido.length() > 500 ? contenido.substring(0, 500) + "..." : contenido));

            Gson gson = new Gson();
            try {
                Type tipoLista = new TypeToken<ArrayList<Envio>>(){}.getType();
                List<Envio> resultado = gson.fromJson(contenido, tipoLista);
                System.out.println("Parseo JSON exitoso. Número de envíos: " + 
                    (resultado != null ? resultado.size() : "null"));
                return resultado;
            } catch (Exception e) {
                System.err.println("ERROR al parsear el JSON:");
                e.printStackTrace();
                System.err.println("JSON problemático: " + contenido);
            }
        } else {
            System.err.println("Error HTTP: " + respuesta.getCodigo());
            System.err.println("Contenido de error: " + respuesta.getContenido());
        }
        return new ArrayList<>();
    }

   
    public static Envio buscarPorGuia(String numeroGuia) {
        Envio envio = null;
        String url = Constantes.URL_WS + "envio/buscar/" + numeroGuia;
        
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            envio = gson.fromJson(respuesta.getContenido(), Envio.class);
        }
        return envio;
    }

   
    public static Respuesta actualizarEstatus(int idEnvio, int idEstado, int idConductor, int idUnidad) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "envio/actualizar-estatus";
        
        Envio envio = new Envio();
        envio.setIdEnvio(idEnvio);
        envio.setIdEstadoActual(idEstado);
        if(idConductor > 0) envio.setIdConductor(idConductor);
        if(idUnidad > 0) envio.setIdUnidad(idUnidad);
        
        Gson gson = new Gson();
        String parametros = gson.toJson(envio);
        
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "PUT", parametros, "application/json");
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error: " + respuesta.getContenido());
        }
        return msj;
    }
    public static Envio obtenerPorId(int idEnvio) {
        Envio envio = null;
        // Asegúrate de que la URL coincida con tu @Path del WS
        String url = Constantes.URL_WS + "envio/" + idEnvio;
        try {
            RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
            if (respuesta.getCodigo()== 200) {
                Gson gson = new Gson();
                envio = gson.fromJson(respuesta.getContenido(), Envio.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return envio;
    }
}
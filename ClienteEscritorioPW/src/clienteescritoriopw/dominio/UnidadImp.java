package clienteescritoriopw.dominio;

import clienteescritoriopw.conexion.ConexionAPI;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.RespuestaHTTP;
import clienteescritoriopw.pojo.Unidad;
import clienteescritoriopw.utilidad.Constantes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class UnidadImp {
    
    public static List<Unidad> obtenerUnidades() {
        List<Unidad> lista = new ArrayList<>();
        String url = Constantes.URL_WS + "unidad/obtener-todas"; 
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<ArrayList<Unidad>>(){}.getType();
            lista = gson.fromJson(respuesta.getContenido(), tipoLista);
        }
        return lista;
    }
    
    public static Respuesta registrar(Unidad unidad) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "unidad/registrar";
        Gson gson = new Gson();
        String parametros = gson.toJson(unidad);
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "POST", parametros, "application/json");
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error: " + respuesta.getContenido());
        }
        return msj;
    }
    
    public static Respuesta editar(Unidad unidad) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "unidad/editar";
        Gson gson = new Gson();
        String parametros = gson.toJson(unidad);
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "PUT", parametros, "application/json");
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error: " + respuesta.getContenido());
        }
        return msj;
    }
    
    public static Respuesta eliminar(int idUnidad) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "unidad/eliminar/" + idUnidad;
        RespuestaHTTP respuesta = ConexionAPI.peticionSinBody(url, "DELETE");
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error: " + respuesta.getContenido());
        }
        return msj;
    }
    public static Respuesta asignarConductor(int idUnidad, Integer idColaborador) {
        Respuesta respuesta = new Respuesta();
        String url = Constantes.URL_WS + "unidad/asignar";
        
        String parametros = "idUnidad=" + idUnidad;
        
        if(idColaborador != null && idColaborador > 0){
            parametros += "&idColaborador=" + idColaborador;
        } else {
            parametros += "&idColaborador=0"; 
        }
        RespuestaHTTP res = ConexionAPI.peticionBody(url, "PUT", parametros, "application/x-www-form-urlencoded");
        
        if (res.getCodigo() == 200) {
            Gson gson = new Gson();
            respuesta = gson.fromJson(res.getContenido(), Respuesta.class);
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Error de conexión al asignar.");
        }
        return respuesta;
    }
    
    public static boolean existeVinDuplicado(String vin, Integer idExcluir) {
        try {
            String url = Constantes.URL_WS + "unidad/validar-vin-duplicado?vin=" + 
                         java.net.URLEncoder.encode(vin, "UTF-8");
            if (idExcluir != null) {
                url += "&idExcluir=" + idExcluir;
            }

            RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
            if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                Respuesta resp = gson.fromJson(respuesta.getContenido(), Respuesta.class);
                return resp.isError(); // true si hay duplicado
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
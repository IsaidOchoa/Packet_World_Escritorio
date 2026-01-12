package clienteescritoriopw.dominio;

import clienteescritoriopw.conexion.ConexionAPI;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Envio;
import clienteescritoriopw.pojo.Paquete;
import clienteescritoriopw.pojo.RespuestaHTTP;
import clienteescritoriopw.utilidad.Constantes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class PaqueteImp {

    public static Respuesta registrar(Paquete paquete) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "paquete/registrar";
        Gson gson = new Gson();
        String parametros = gson.toJson(paquete);
        
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "POST", parametros, "application/json");
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al registrar paquete: " + respuesta.getContenido());
        }
        return msj;
    }
    
    public static List<Paquete> obtenerTodos() {
        List<Paquete> lista = new ArrayList<>();
        String url = Constantes.URL_WS + "paquete/todos";

        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);

        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type tipoLista = new TypeToken<ArrayList<Paquete>>(){}.getType();
                lista = gson.fromJson(respuesta.getContenido(), tipoLista);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
    
    //NUEVO MÉTODO PARA EL FLUJO DE COSTO ACTUALIZADO
    public static Respuesta agregarPaquete(Paquete paquete) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "envio/agregar-paquete";
        Gson gson = new Gson();
        String parametros = gson.toJson(paquete);
        
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "POST", parametros, "application/json");
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al agregar paquete: " + respuesta.getContenido());
        }
        return msj;
    }

    public static List<Paquete> obtenerPaquetesPorEnvio(int idEnvio) {
        List<Paquete> lista = new ArrayList<>();
        String url = Constantes.URL_WS + "paquete/envio/" + idEnvio;
        
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type tipoLista = new TypeToken<ArrayList<Paquete>>(){}.getType();
                lista = gson.fromJson(respuesta.getContenido(), tipoLista);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
    
    public static Respuesta eliminar(int idPaquete) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "paquete/eliminar/" + idPaquete;

        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "DELETE", "", "application/json");

        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = new Gson().fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al eliminar paquete: " + respuesta.getContenido());
        }
        return msj;
    }
    
    public static Respuesta editar(Paquete paquete) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "paquete/editar";
        Gson gson = new Gson();
        String parametros = gson.toJson(paquete);

        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "PUT", parametros, "application/json");

        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al actualizar el paquete: " + respuesta.getCodigo());
        }
        return msj;
    }
}
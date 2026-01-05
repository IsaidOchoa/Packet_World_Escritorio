package clienteescritoriopw.dominio;

import clienteescritoriopw.conexion.ConexionAPI;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Cliente;
import clienteescritoriopw.pojo.RespuestaHTTP;
import clienteescritoriopw.utilidad.Constantes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;


public class ClienteImp {

    public static List<Cliente> obtenerClientes() {
        List<Cliente> lista = new ArrayList<>();
        String url = Constantes.URL_WS + "cliente/obtener-todos";
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type tipoLista = new TypeToken<ArrayList<Cliente>>(){}.getType();
                lista = gson.fromJson(respuesta.getContenido(), tipoLista);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
    
    public static Respuesta registrar(Cliente cliente) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "cliente/registrar";
        Gson gson = new Gson();
        String parametros = gson.toJson(cliente);
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "POST", parametros, "application/json");
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al registrar: " + respuesta.getContenido());
        }
        return msj;
    }
    
    public static Respuesta editar(Cliente cliente) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "cliente/editar";
        Gson gson = new Gson();
        String parametros = gson.toJson(cliente);
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "PUT", parametros, "application/json");
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al editar: " + respuesta.getContenido());
        }
        return msj;
    }
    
    public static Respuesta eliminar(int idCliente) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "cliente/eliminar/" + idCliente;
        RespuestaHTTP respuesta = ConexionAPI.peticionSinBody(url, "DELETE");
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al eliminar: " + respuesta.getContenido());
        }
        return msj;
    }
}
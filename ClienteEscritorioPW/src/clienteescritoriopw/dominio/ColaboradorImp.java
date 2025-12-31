package clienteescritoriopw.dominio;

import clienteescritoriopw.conexion.ConexionAPI;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Colaborador;
import clienteescritoriopw.pojo.RespuestaHTTP;
import clienteescritoriopw.utilidad.Constantes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorImp {

    public static List<Colaborador> obtenerColaboradores() {
        List<Colaborador> lista = new ArrayList<>();
        String url = Constantes.URL_WS + "colaborador/obtener-todos";
        
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type tipoLista = new TypeToken<ArrayList<Colaborador>>(){}.getType();
                lista = gson.fromJson(respuesta.getContenido(), tipoLista);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
    
    public static Respuesta registrar(Colaborador colaborador) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "colaborador/registrar";
        Gson gson = new Gson();
        String parametros = gson.toJson(colaborador);
        
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "POST", parametros, "application/json");
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error en la petición: " + respuesta.getCodigo());
        }
        return msj;
    }
    
    public static Respuesta editar(Colaborador colaborador) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "colaborador/editar";
        Gson gson = new Gson();
        String parametros = gson.toJson(colaborador);
        
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "PUT", parametros, "application/json");
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al editar: " + respuesta.getCodigo());
        }
        return msj;
    }
    
    public static Respuesta eliminar(int idColaborador) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "colaborador/eliminar/" + idColaborador;
        
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

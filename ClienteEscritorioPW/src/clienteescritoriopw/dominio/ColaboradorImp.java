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

    //version de Isaid
    public static List<Colaborador> obtenerColaboradores() {
        String url = Constantes.URL_WS + "colaborador/obtener-todos";
        System.out.println("URL COLABORADORES: " + url);

        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        System.out.println("Código colaboradores: " + respuesta.getCodigo());
        System.out.println("Contenido colaboradores: " + respuesta.getContenido());

        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type tipoLista = new TypeToken<ArrayList<Colaborador>>(){}.getType();
                List<Colaborador> resultado = gson.fromJson(respuesta.getContenido(), tipoLista);
                System.out.println("Número de colaboradores: " + (resultado != null ? resultado.size() : "null"));
                return resultado;
            } catch (Exception e) {
                System.out.println("ERROR al parsear colaboradores: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
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

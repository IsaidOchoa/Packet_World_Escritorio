package clienteescritoriopw.dominio;

import clienteescritoriopw.conexion.ConexionAPI;
import clienteescritoriopw.pojo.Colonia;
import clienteescritoriopw.pojo.Estado;
import clienteescritoriopw.pojo.Municipio;
import clienteescritoriopw.pojo.RespuestaHTTP;
import clienteescritoriopw.utilidad.Constantes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class DireccionImp {

    public static List<Estado> obtenerEstados() {
        List<Estado> lista = new ArrayList<>();
        String url = Constantes.URL_WS + "direccion/estados"; 
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<ArrayList<Estado>>(){}.getType();
            lista = gson.fromJson(respuesta.getContenido(), tipoLista);
        }
        return lista;
    }

    public static List<Municipio> obtenerMunicipios(int idEstado) {
        List<Municipio> lista = new ArrayList<>();
        String url = Constantes.URL_WS + "direccion/municipios/" + idEstado; 
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<ArrayList<Municipio>>(){}.getType();
            lista = gson.fromJson(respuesta.getContenido(), tipoLista);
        }
        return lista;
    }

    public static List<Colonia> obtenerColoniasPorMunicipio(int idMunicipio) {
        List<Colonia> lista = new ArrayList<>();
        String url = Constantes.URL_WS + "direccion/colonias/" + idMunicipio; 
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<ArrayList<Colonia>>(){}.getType();
            lista = gson.fromJson(respuesta.getContenido(), tipoLista);
        }
        return lista;
    }
    
    public static List<Colonia> buscarPorCP(String cp) {
        List<Colonia> lista = new ArrayList<>();
        String url = Constantes.URL_WS + "direccion/cp/" + cp; 
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<ArrayList<Colonia>>(){}.getType();
            lista = gson.fromJson(respuesta.getContenido(), tipoLista);
        }
        return lista;
    }
    
    // Obtener un Municipio específico (Para la búsqueda automática)
    public static Municipio obtenerMunicipio(int idMunicipio) {
        Municipio municipio = null;
        String url = Constantes.URL_WS + "direccion/municipio/" + idMunicipio;
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            municipio = gson.fromJson(respuesta.getContenido(), Municipio.class);
        }
        return municipio;
    }
}
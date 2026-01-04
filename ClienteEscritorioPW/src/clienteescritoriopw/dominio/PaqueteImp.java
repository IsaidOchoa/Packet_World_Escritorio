package clienteescritoriopw.dominio;

import clienteescritoriopw.conexion.ConexionAPI;
import clienteescritoriopw.dto.Respuesta;
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
}
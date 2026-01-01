package clienteescritoriopw.dominio;

import clienteescritoriopw.conexion.ConexionAPI;
import clienteescritoriopw.pojo.Colonia;
import clienteescritoriopw.pojo.Estado;
import clienteescritoriopw.pojo.Municipio;
import clienteescritoriopw.pojo.RespuestaHTTP;
import clienteescritoriopw.pojo.Rol;
import clienteescritoriopw.pojo.Sucursal;
import clienteescritoriopw.pojo.TipoUnidad;
import clienteescritoriopw.utilidad.Constantes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class CatalogoImp {
  
    
    public static List<Rol> obtenerRoles() {
        List<Rol> lista = new ArrayList<>();
        String url = Constantes.URL_WS + "catalogo/roles";
        
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type tipoLista = new TypeToken<ArrayList<Rol>>(){}.getType();
                lista = gson.fromJson(respuesta.getContenido(), tipoLista);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
    public static List<TipoUnidad> obtenerTiposUnidad() {
        List<TipoUnidad> lista = new ArrayList<>();
        // Esta URL debe coincidir con tu CatalogoWS del servidor (@Path("tipos-unidad"))
        String url = Constantes.URL_WS + "catalogo/tipos-unidad";
        
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type tipoLista = new TypeToken<ArrayList<TipoUnidad>>(){}.getType();
                lista = gson.fromJson(respuesta.getContenido(), tipoLista);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
}
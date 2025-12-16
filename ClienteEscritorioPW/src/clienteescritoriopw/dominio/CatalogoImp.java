/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteescritoriopw.dominio;

import clienteescritoriopw.conexion.ConexionAPI;
import clienteescritoriopw.pojo.RespuestaHTTP;
import clienteescritoriopw.pojo.Rol;
import clienteescritoriopw.pojo.Sucursal;
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
        // Ruta basada en tu CatalogoWS: @Path("roles")
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
    
    public static List<Sucursal> obtenerSucursales() {
        List<Sucursal> lista = new ArrayList<>();
        // Ruta basada en tu SucursalWS: @Path("Obtener-todas")
        String url = Constantes.URL_WS + "sucursal/Obtener-todas";
        
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type tipoLista = new TypeToken<ArrayList<Sucursal>>(){}.getType();
                lista = gson.fromJson(respuesta.getContenido(), tipoLista);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
}


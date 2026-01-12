package clienteescritoriopw.dominio;

import clienteescritoriopw.conexion.ConexionAPI;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Colaborador;
import clienteescritoriopw.pojo.RespuestaHTTP;
import clienteescritoriopw.pojo.ValidacionDuplicadoColaborador;
import clienteescritoriopw.utilidad.Constantes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColaboradorImp {

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

    // PERFIL (sin foto, sin password)
    public static Respuesta editarPerfil(Colaborador colaborador) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "colaborador/editar-perfil";
        Gson gson = new Gson();
        String parametros = gson.toJson(colaborador);
        
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "PUT", parametros, "application/json");
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al actualizar perfil: " + respuesta.getCodigo());
        }
        return msj;
    }

    // FOTO
    public static Respuesta editarFoto(Colaborador colaborador) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "colaborador/editar-foto";
        Gson gson = new Gson();
        String parametros = gson.toJson(colaborador);
        
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "PUT", parametros, "application/json");
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al actualizar foto: " + respuesta.getCodigo());
        }
        return msj;
    }

    // CONTRASEÑA
    public static Respuesta editarPassword(Integer idColaborador, String passwordActual, String passwordNueva) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "colaborador/editar-password";
        Gson gson = new Gson();
        
        Map<String, Object> datos = new HashMap<>();
        datos.put("idColaborador", idColaborador);
        datos.put("passwordActual", passwordActual);
        datos.put("passwordNueva", passwordNueva);
        
        String parametros = gson.toJson(datos);
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "PUT", parametros, "application/json");
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al cambiar contraseña: " + respuesta.getCodigo());
        }
        return msj;
    }
    
    public static List<Colaborador> obtenerConductoresPorSucursal(int idSucursal) {
        String url = Constantes.URL_WS + "colaborador/por-sucursal/" + idSucursal + "/conductores";
        RespuestaHTTP respuesta = ConexionAPI.peticionGET(url);
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                Type tipoLista = new TypeToken<ArrayList<Colaborador>>(){}.getType();
                return gson.fromJson(respuesta.getContenido(), tipoLista);
            } catch (Exception e) {}
        }
        return new ArrayList<>();
    }

    public static Respuesta eliminar(int idColaboradorAEliminar, int idColaboradorSesion) {
        Respuesta msj = new Respuesta();
        String url = Constantes.URL_WS + "colaborador/eliminar/" + idColaboradorAEliminar;

        // Crear un JSON con el ID del admin
        Map<String, Object> datos = new HashMap<>();
        datos.put("idSesion", idColaboradorSesion);
        Gson gson = new Gson();
        String json = gson.toJson(datos);

        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "DELETE", json, "application/json");

        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            msj = gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            msj.setError(true);
            msj.setMensaje("Error al eliminar: " + respuesta.getCodigo());
        }
        return msj;
    }
    
    public static Respuesta validarDuplicadosColaborador(ValidacionDuplicadoColaborador datos) {
        String url = Constantes.URL_WS + "colaborador/validar-duplicados";
        Gson gson = new Gson();
        String json = gson.toJson(datos);
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(url, "POST", json, "application/json");

        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            return gson.fromJson(respuesta.getContenido(), Respuesta.class);
        } else {
            Respuesta error = new Respuesta();
            error.setError(true);
            error.setMensaje("Error de conexión al validar duplicados.");
            return error;
        }
    }
}
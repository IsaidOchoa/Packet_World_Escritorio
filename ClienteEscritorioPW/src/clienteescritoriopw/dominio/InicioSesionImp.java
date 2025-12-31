package clienteescritoriopw.dominio;

import clienteescritoriopw.conexion.ConexionAPI;
import clienteescritoriopw.dto.RSAutenticacionColaborador;
import clienteescritoriopw.pojo.RespuestaHTTP;
import clienteescritoriopw.utilidad.Constantes;
import com.google.gson.Gson;
import java.net.HttpURLConnection;

public class InicioSesionImp {
    
    
    public static RSAutenticacionColaborador validarLogin(String noPersonal, String password) {
        RSAutenticacionColaborador respuestaLogin = new RSAutenticacionColaborador();
        
        String url = Constantes.URL_WS + "autenticacion/login";
        String parametros = "noPersonal=" + noPersonal + "&password=" + password;
        
      
        RespuestaHTTP respuesta = ConexionAPI.peticionBody(
                url, 
                Constantes.METODO_POST, 
                parametros, 
                Constantes.APPLICATION_FORM
        );
        
        if (respuesta.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            try {
                respuestaLogin = gson.fromJson(respuesta.getContenido(), RSAutenticacionColaborador.class);
            } catch (Exception e) {
                respuestaLogin.setError(true);
                respuestaLogin.setMensaje("Error al procesar la respuesta del servidor.");
            }
        } else {
            respuestaLogin.setError(true);
            respuestaLogin.setMensaje("Error en la petición. Código: " + respuesta.getCodigo());
        }
        return respuestaLogin;
    }
}

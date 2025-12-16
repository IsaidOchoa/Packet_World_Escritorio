/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteescritoriopw.utilidad;

/**
 *
 * @author pepeg
 */

public class Constantes {
    
    public static final String URL_WS = "http://localhost:8080/WSPacketWorld/PacketWorld/";
    
    // Códigos de Error 
    public static final int ERROR_MALFORMED_URL = 1001; 
    public static final int ERROR_PETICION = 1002;
    
    // Mensajes de Error
    public static final String MSJ_ERROR_URL = "Lo sentimos, la URL de solicitud no es válida.";
    public static final String MSJ_ERROR_PETICION = "Lo sentimos, tu solicitud no pudo ser procesada.";
    public static final String MSJ_ERROR_CONEXION = "Lo sentimos, tenemos problemas de conexión con el servidor.";
    
    // Llaves para JSON 
    public static final String KEY_ERROR = "error";
    public static final String KEY_MENSAJE = "mensaje";
    public static final String KEY_LISTA="lista_valores";
    public static final String KEY_DATOS = "objeto";
    
    // Métodos HTTP
    public static final String METODO_GET = "GET";
    public static final String METODO_POST = "POST";
    public static final String METODO_PUT = "PUT";
    public static final String METODO_DELETE = "DELETE";
    
    // Content Types
    public static final String APPLICATION_JSON = "application/json" ;
    public static final String APPLICATION_FORM = "application/x-www-form-urlencoded" ;
}


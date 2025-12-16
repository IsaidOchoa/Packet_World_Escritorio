/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteescritoriopw.utilidad;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;

/**
 *
 * @author pepeg
 */
public class Utilidades {
    
public static String streamToString(InputStream input) throws IOException{
    BufferedReader in = new BufferedReader(new InputStreamReader(input));
    String inputLine;
    StringBuffer respuestaEntrada = new StringBuffer();
    while( (inputLine = in.readLine()) != null){
       respuestaEntrada.append(inputLine);
    }
    in.close();
    return respuestaEntrada.toString();
}
  public static void mostrarAlertaSimple(String titulo,String contenido, Alert.AlertType tipo){
  Alert alerta = new Alert(tipo);
  alerta.setTitle(titulo);
  alerta.setContentText(contenido);
  alerta.setHeaderText(null);
  alerta.showAndWait();
  }
  
  public static boolean mostrarAlertaConfirmacion(String titulo, String contenido){
      Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
      alerta.setTitle(titulo);
      alerta.setContentText(contenido);
      alerta.setHeaderText(contenido);
      Optional<ButtonType> btnSeleccion = alerta.showAndWait();
      return (btnSeleccion.get() == ButtonType.OK);
  }
  
  public static Image decodificarImagen(String base64) {
        try {
            String base64Limpio = base64.replaceAll("\\n", "").replaceAll("\\r", "");
            
            byte[] imageBytes = Base64.getDecoder().decode(base64Limpio);
            
            return new Image(new ByteArrayInputStream(imageBytes));
        } catch (Exception e) {
            System.err.println("Error al decodificar la imagen: " + e.getMessage());
            return null; // Si falla, retorna null y no muestra nada
        }
    }
}

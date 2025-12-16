/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteescritoriopw.utilidad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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
}

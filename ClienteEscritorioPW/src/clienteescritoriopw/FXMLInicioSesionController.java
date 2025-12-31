package clienteescritoriopw;

import clienteescritoriopw.dominio.InicioSesionImp;
import clienteescritoriopw.dto.RSAutenticacionColaborador;
import clienteescritoriopw.pojo.Colaborador;
import clienteescritoriopw.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLInicioSesionController implements Initializable {

    @FXML
    private TextField tfNoPersonal;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Label lblError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void clicIngresar(ActionEvent event) {

        String noPersonal = tfNoPersonal.getText().trim();
        String password = pfPassword.getText().trim();

        // A) Validar Vacíos
        if(noPersonal.isEmpty() || password.isEmpty()){
            Utilidades.mostrarAlertaSimple(
                "Campos requeridos", 
                "El número de personal y la contraseña son obligatorios.", 
                Alert.AlertType.WARNING
            );
            return; 
        }
        
        // B) Validar Longitud
        if (noPersonal.length() > 6) {
            Utilidades.mostrarAlertaSimple(
                "Error de validación", 
                "El número de personal es demasiado largo (máx 6 caracteres).", 
                Alert.AlertType.WARNING
            );
            return; 
        }

        // C) Validar Formato Seguro (Regex)
        // Solo permite Mayúsculas (A-Z), Números (0-9) y Guiones (-)
        if (!noPersonal.matches("^[A-Z0-9-]+$")) {
            Utilidades.mostrarAlertaSimple(
                "Formato inválido", 
                "El usuario contiene caracteres no permitidos. Verifica que no tengas espacios o símbolos.", 
                Alert.AlertType.WARNING
            );
            return; 
        }
        verificarCredenciales(noPersonal, password);
    }
      
    
    
    private void verificarCredenciales(String noPersonal, String password){
        // Llamamos a la implementación (Lógica de negocio)
        RSAutenticacionColaborador respuesta = InicioSesionImp.validarLogin(noPersonal, password);
        
        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Credenciales verificadas", 
                    respuesta.getMensaje(), 
                    Alert.AlertType.INFORMATION);
            
            irPantallaPrincipal(respuesta.getColaborador());
        } else {
            Utilidades.mostrarAlertaSimple("Error de acceso", 
                    respuesta.getMensaje(), 
                    Alert.AlertType.ERROR);
        }
    }
    
    private void irPantallaPrincipal(Colaborador colaborador){
       
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("FXMLPrincipal.fxml"));
            Parent vista = cargador.load();
            FXMLPrincipalController controladorPrincipal = cargador.getController();
            controladorPrincipal.cargarInformacion(colaborador);
            
            Scene escenaPrincipal = new Scene(vista);
            Stage stPrincipal = (Stage) tfNoPersonal.getScene().getWindow();
            
            stPrincipal.setScene(escenaPrincipal);
            stPrincipal.setTitle("Packet World - Principal");
            stPrincipal.centerOnScreen();
            stPrincipal.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo cargar la ventana principal", Alert.AlertType.ERROR);
        }
    }
}
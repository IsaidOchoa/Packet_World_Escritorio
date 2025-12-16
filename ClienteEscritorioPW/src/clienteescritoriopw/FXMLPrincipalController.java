/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteescritoriopw;

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
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author pepeg
 */
public class FXMLPrincipalController implements Initializable {

    @FXML
    private Label lblNombreUsuario;
    @FXML
    private Label lblRol;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    public void cargarInformacion(Colaborador colaborador){
    lblNombreUsuario.setText(colaborador.getNombre() + " " + colaborador.getApellidoPaterno());
    lblRol.setText("Rol: " + colaborador.getRol());
}
    

    @FXML
    private void clicCerrarSesion(ActionEvent event) {
        boolean confirmar = Utilidades.mostrarAlertaConfirmacion("Cerrar Sesión", "¿Estás seguro de que deseas salir del sistema?");
              
        if(confirmar){
            try {
                Stage escenarioPrincipal = (Stage) lblNombreUsuario.getScene().getWindow();
                
               
                Parent vista = FXMLLoader.load(getClass().getResource("/clienteescritoriopw/FXMLInicioSesion.fxml")); 
                
                Scene escenaLogin = new Scene(vista);
                
                escenarioPrincipal.setScene(escenaLogin);
                escenarioPrincipal.setTitle("Iniciar Sesión");
                escenarioPrincipal.centerOnScreen();
                escenarioPrincipal.show();
            } catch (IOException ex) {
                
                ex.printStackTrace();
            }
        }
    }
    }



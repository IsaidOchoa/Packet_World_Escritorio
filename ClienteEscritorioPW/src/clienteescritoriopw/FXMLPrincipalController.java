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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author pepeg
 */
public class FXMLPrincipalController implements Initializable {

   @FXML private Label lblNombreUsuario;
   @FXML private Label lblRol;

    @FXML
    private Label lbColaborador;

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
   @FXML
    private void clicModuloColaboradores(ActionEvent event) {
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clienteescritoriopw/FXMLColaborador.fxml"));
            
            Parent root = loader.load();
            
            Stage escenario = new Stage();
            escenario.setScene(new Scene(root));
            escenario.setTitle("Gestión de Colaboradores");
            escenario.initModality(Modality.APPLICATION_MODAL); 
            
            // ---LIMITAMOS EL TAMAÑO MÍNIMO ---
            escenario.setMinWidth(900);  
            escenario.setMinHeight(600); 
            
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error de Carga", 
                                          "No se pudo abrir la ventana de Colaboradores. Verifique la ruta del FXML.", 
                                          Alert.AlertType.ERROR);
        }
    }
    
    
    
    @FXML
    private void clicModuloSucursales(ActionEvent event) {
        try {
            // Asegúrate de que la ruta sea correcta
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clienteescritoriopw/FXMLSucursal.fxml"));
            Parent root = loader.load();
            
            Stage escenario = new Stage();
            escenario.setScene(new Scene(root));
            escenario.setTitle("Gestión de Sucursales");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error de Carga", 
                                          "No se pudo abrir la ventana de Sucursales.", 
                                          Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicModuloClientes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clienteescritoriopw/FXMLCliente.fxml"));
            Parent root = loader.load();
            
            Stage escenario = new Stage();
            escenario.setScene(new Scene(root));
            escenario.setTitle("Gestión de Clientes");
            escenario.initModality(Modality.APPLICATION_MODAL);
            
            escenario.setMinWidth(900);
            escenario.setMinHeight(600);
            
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir la ventana de Clientes.", Alert.AlertType.ERROR);
        }
    }
    

    @FXML
    private void clicModuloEnvios(ActionEvent event) {
    }

    @FXML
    private void clicModuloUnidades(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clienteescritoriopw/FXMLUnidad.fxml"));
            Parent root = loader.load();
            
            Stage escenario = new Stage();
            escenario.setScene(new Scene(root));
            escenario.setTitle("Gestión de Unidades");
            escenario.initModality(Modality.APPLICATION_MODAL);
            
            escenario.setMinWidth(900);
            escenario.setMinHeight(600);
            
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir la ventana de Unidades.", Alert.AlertType.ERROR);
        }
    }
}



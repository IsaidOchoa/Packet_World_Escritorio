/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteescritoriopw;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author pepeg
 */
public class FXMLPaqueteController implements Initializable {

    @FXML
    private TextField tfBusqueda;
    @FXML
    private TableView<?> tvClientes;
    @FXML
    private TableColumn<?, ?> colNombre;
    @FXML
    private TableColumn<?, ?> colPaterno;
    @FXML
    private TableColumn<?, ?> colMaterno;
    @FXML
    private TableColumn<?, ?> colTelefono;
    @FXML
    private TableColumn<?, ?> colCorreo;
    @FXML
    private TableColumn<?, ?> colDireccion;
    @FXML
    private TableColumn<?, ?> colColonia;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void clicRegresar(ActionEvent event) {
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
    }

    @FXML
    private void clicNuevo(ActionEvent event) {
    }

    @FXML
    private void clicEditar(ActionEvent event) {
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
    }
    
}

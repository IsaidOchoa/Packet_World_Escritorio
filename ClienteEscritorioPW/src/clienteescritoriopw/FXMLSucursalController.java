/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteescritoriopw;

import clienteescritoriopw.dominio.SucursalImp;
import clienteescritoriopw.pojo.Sucursal;
import clienteescritoriopw.utilidad.Utilidades;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author pepeg
 */
public class FXMLSucursalController implements Initializable {

    @FXML
    private TableView<Sucursal> tvSucursales;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colCalle;
    @FXML
    private TableColumn colNumero;
    @FXML
    private TableColumn colColonia;
    @FXML
    private TableColumn colCodigoPostal;
    @FXML
    private TableColumn colMunicipio;
    @FXML
    private TableColumn colEstado;
    @FXML
    private TextField tfBusqueda;
    
    private ObservableList<Sucursal> listaSucursales;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarDatosTabla();
    }    
    
    private void configurarTabla(){
        // Nota: Asegúrate que tu POJO Sucursal tenga los Getters para estos atributos:
        // getNombre, getCalle, getNumero, getColonia, getCodigoPostal, getMunicipio, getEstado
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colCalle.setCellValueFactory(new PropertyValueFactory("calle"));
        colNumero.setCellValueFactory(new PropertyValueFactory("numero"));
        colColonia.setCellValueFactory(new PropertyValueFactory("colonia"));
        colCodigoPostal.setCellValueFactory(new PropertyValueFactory("codigoPostal"));
        colMunicipio.setCellValueFactory(new PropertyValueFactory("municipio"));
        colEstado.setCellValueFactory(new PropertyValueFactory("estado"));
    }
    
    public void cargarDatosTabla(){
        listaSucursales = FXCollections.observableArrayList();
        List<Sucursal> respuestaWS = SucursalImp.obtenerSucursales();
        
        if(respuestaWS != null && !respuestaWS.isEmpty()){
            listaSucursales.addAll(respuestaWS);
            tvSucursales.setItems(listaSucursales);
        }else{
            Utilidades.mostrarAlertaSimple("Sin Resultados", "No se encontraron sucursales.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        // Implementación de la búsqueda aquí
        cargarDatosTabla();
    }

    @FXML
    private void clicNuevo(ActionEvent event) {
        // Implementación para abrir el formulario de registro
        System.out.println("Clic en Nuevo Sucursal");
    }

    @FXML
    private void clicEditar(ActionEvent event) {
        Sucursal seleccionado = tvSucursales.getSelectionModel().getSelectedItem();
        if(seleccionado != null){
            // Implementación para abrir el formulario de edición
            System.out.println("Editando a: " + seleccionado.getNombre());
        }else{
            Utilidades.mostrarAlertaSimple("Selección requerida", "Selecciona una sucursal para editar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        Sucursal seleccionado = tvSucursales.getSelectionModel().getSelectedItem();
        if(seleccionado != null){
            boolean confirmar = Utilidades.mostrarAlertaConfirmacion(
                    "Eliminar Sucursal", 
                    "¿Estás seguro de eliminar la sucursal " + seleccionado.getNombre() + "?"
            );
            
            if(confirmar){
                clienteescritoriopw.dto.Respuesta respuesta = SucursalImp.eliminar(seleccionado.getIdSucursal());
                if(!respuesta.isError()){
                    Utilidades.mostrarAlertaSimple("Éxito", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
                    cargarDatosTabla(); 
                }else{
                    Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
                }
            }
        }else{
            Utilidades.mostrarAlertaSimple("Selección requerida", "Selecciona una sucursal para eliminar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        Stage escenario = (Stage) tfBusqueda.getScene().getWindow();
        escenario.close();
    }
}
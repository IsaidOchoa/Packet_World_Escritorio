/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteescritoriopw;

import clienteescritoriopw.dominio.ColaboradorImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Colaborador;
import clienteescritoriopw.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author pepeg
 */
public class FXMLColaboradorController implements Initializable {

    @FXML
    private TableView<Colaborador> tvColaboradores;
    @FXML
    private TableColumn colNoPersonal;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colPaterno;
    @FXML
    private TableColumn colMaterno;
    @FXML
    private TableColumn colRol;
    @FXML
    private TableColumn colSucursal;
    @FXML
    private TableColumn colCorreo;
    @FXML
    private TableColumn colCurp;
    @FXML
    private TableColumn colLicencia;
    @FXML
    private TextField tfBusqueda;
    
    // Lista observable para la tabla
    private ObservableList<Colaborador> listaColaboradores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarDatosTabla();
    }    
    
    private void configurarTabla(){
        colNoPersonal.setCellValueFactory(new PropertyValueFactory("numeroPersonal"));
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colPaterno.setCellValueFactory(new PropertyValueFactory("apellidoPaterno"));
        colMaterno.setCellValueFactory(new PropertyValueFactory("apellidoMaterno"));
        colCurp.setCellValueFactory(new PropertyValueFactory("curp")); 
        colLicencia.setCellValueFactory(new PropertyValueFactory("numeroLicencia")); 
        colRol.setCellValueFactory(new PropertyValueFactory("rol"));
        colSucursal.setCellValueFactory(new PropertyValueFactory("nombreSucursal"));
        colCorreo.setCellValueFactory(new PropertyValueFactory("correo"));
    }
    
    private void cargarDatosTabla(){
        listaColaboradores = FXCollections.observableArrayList();
        List<Colaborador> respuestaWS = ColaboradorImp.obtenerColaboradores();
        
        if(respuestaWS != null && !respuestaWS.isEmpty()){
            listaColaboradores.addAll(respuestaWS);
            tvColaboradores.setItems(listaColaboradores);
        }else{
            Utilidades.mostrarAlertaSimple("Sin Resultados", "No se encontraron colaboradores.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        cargarDatosTabla();
    }

   

    @FXML
    private void clicNuevo(ActionEvent event) {
        abrirFormulario(null);
    }

    @FXML
    private void clicEditar(ActionEvent event) {
        Colaborador seleccionado = tvColaboradores.getSelectionModel().getSelectedItem();
        if(seleccionado != null){
            abrirFormulario(seleccionado);
        }else{
            Utilidades.mostrarAlertaSimple("Selección requerida", "Selecciona un colaborador para editar.", Alert.AlertType.WARNING);
        }
    }
    
    // Método auxiliar para abrir la ventana modal
    private void abrirFormulario(Colaborador colaborador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clienteescritoriopw/FXMLFormularioColaborador.fxml"));
                Parent root = loader.load();
            
            FXMLFormularioColaboradorController controlador = loader.getController();
            
            // Si hay un colaborador, inicializamos el modo edición
            if (colaborador != null) {
                controlador.inicializarEdicion(colaborador);
            }
            
            Stage escenario = new Stage();
            escenario.setScene(new Scene(root));
            escenario.setTitle((colaborador == null) ? "Registrar Colaborador" : "Editar Colaborador");
            escenario.initModality(Modality.APPLICATION_MODAL);
            
            // Esperar a que se cierre el modal para recargar la tabla
            escenario.showAndWait(); 
            cargarDatosTabla(); 

        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error de Carga", 
                                          "No se pudo abrir la ventana del formulario.", 
                                          Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        Colaborador seleccionado = tvColaboradores.getSelectionModel().getSelectedItem();
        if(seleccionado != null){
            boolean confirmar = Utilidades.mostrarAlertaConfirmacion(
                    "Eliminar Colaborador", 
                    "¿Estás seguro de eliminar a " + seleccionado.getNombre() + "?"
            );
            
            if(confirmar){
                Respuesta respuesta = ColaboradorImp.eliminar(seleccionado.getIdColaborador());
                if(!respuesta.isError()){
                    Utilidades.mostrarAlertaSimple("Éxito", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
                    cargarDatosTabla(); // Recargamos la tabla
                }else{
                    Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
                }
            }
        }else{
            Utilidades.mostrarAlertaSimple("Selección requerida", "Selecciona un colaborador para eliminar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        Stage escenario = (Stage) tfBusqueda.getScene().getWindow();
        escenario.close(); // O regresar al menú principal
    }
}
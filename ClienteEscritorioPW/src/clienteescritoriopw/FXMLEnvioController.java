package clienteescritoriopw;

import clienteescritoriopw.dominio.EnvioImp;
import clienteescritoriopw.dto.Respuesta; // Importante para manejar la respuesta de eliminar
import clienteescritoriopw.pojo.Colaborador;
import clienteescritoriopw.pojo.Envio;
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
import javafx.scene.control.ButtonType; // Importante para confirmaciones
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLEnvioController implements Initializable {

    @FXML
    private TableView<Envio> tvEnvios;
    @FXML
    private TableColumn colGuia;
    @FXML
    private TableColumn colCliente;
    @FXML
    private TableColumn colOrigen;
    @FXML
    private TableColumn colDestino;
    @FXML
    private TableColumn colEstatus;
    @FXML
    private TableColumn colCosto;
    @FXML
    private TextField tfBusqueda;
    @FXML
    private TableColumn<Envio, String> colUnidad;

    private ObservableList<Envio> listaEnvios;
    private Colaborador colaboradorSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionTabla();
        tfBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarEnvios(newValue);
        });
    }

    public void inicializarColaborador(Colaborador colaborador) {
        this.colaboradorSesion = colaborador;
    }

    private void configurarTabla() {
        colGuia.setCellValueFactory(new PropertyValueFactory("numeroGuia"));
        colCliente.setCellValueFactory(new PropertyValueFactory("nombreCliente")); 
        colOrigen.setCellValueFactory(new PropertyValueFactory("nombreSucursalOrigen"));
        colDestino.setCellValueFactory(new PropertyValueFactory("nombreColonia")); 
        colEstatus.setCellValueFactory(new PropertyValueFactory("estatus"));
        colCosto.setCellValueFactory(new PropertyValueFactory("costo"));
     
        colUnidad.setCellValueFactory(new PropertyValueFactory("infoUnidad"));
    
    }

    private void cargarInformacionTabla() {
        listaEnvios = FXCollections.observableArrayList();
        List<Envio> enviosBD = EnvioImp.obtenerTodos(); 
        if (enviosBD != null) {
            listaEnvios.addAll(enviosBD);
            tvEnvios.setItems(listaEnvios);
        } else {
            Utilidades.mostrarAlertaSimple("Error", "No se pudo conectar con el servidor.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void btnRegistrarEnvio(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clienteescritoriopw/FXMLFormularioEnvio.fxml"));
            Parent root = loader.load();
            
            FXMLFormularioEnvioController controller = loader.getController();
            controller.inicializarColaborador(colaboradorSesion);
            
            Stage escenario = new Stage();
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.setScene(new Scene(root));
            escenario.setTitle("Registrar Nuevo Envío");
            escenario.showAndWait();
            
            cargarInformacionTabla();
            
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir el formulario.", Alert.AlertType.ERROR);
        }
    }

    
    @FXML
    private void btnEditarEnvio(ActionEvent event) {
        Envio envioSeleccionado = tvEnvios.getSelectionModel().getSelectedItem();

        if (envioSeleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/clienteescritoriopw/FXMLFormularioEnvio.fxml"));
                Parent root = loader.load();

                FXMLFormularioEnvioController controller = loader.getController();
                controller.inicializarColaborador(colaboradorSesion);
                controller.inicializarEnvioEdicion(envioSeleccionado);

                Stage escenario = new Stage();
                escenario.initModality(Modality.APPLICATION_MODAL);
                escenario.setScene(new Scene(root));
                escenario.setTitle("Editar Envío");
                escenario.showAndWait();
                cargarInformacionTabla();
            } catch (IOException ex) {
                ex.printStackTrace();
                Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir el formulario de edición.", Alert.AlertType.ERROR);
            }
        } else {
            Utilidades.mostrarAlertaSimple("Selección Requerida", "Debes seleccionar un envío para editar.", Alert.AlertType.WARNING);
        }
    }

    /*
    @FXML
    private void btnEliminarEnvio(ActionEvent event) {
        Envio envioSeleccionado = tvEnvios.getSelectionModel().getSelectedItem();

        if (envioSeleccionado != null) {
           
            boolean confirmar = Utilidades.mostrarAlertaConfirmacion("Eliminar Envío", 
                    "¿Estás seguro de eliminar el envío " + envioSeleccionado.getNumeroGuia() + "?\n\n"
                    + "Esta acción eliminará también todos los paquetes asociados.");

            if (confirmar) {
                // Llamada al backend para eliminar
                Respuesta respuesta = EnvioImp.eliminar(envioSeleccionado.getIdEnvio());
                
                if (!respuesta.isError()) {
                    Utilidades.mostrarAlertaSimple("Éxito", "Envío eliminado correctamente.", Alert.AlertType.INFORMATION);
                    cargarInformacionTabla();
                } else {
                    Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
                }
            }
        } else {
            Utilidades.mostrarAlertaSimple("Selección Requerida", "Debes seleccionar un envío para eliminar.", Alert.AlertType.WARNING);
        }
    }*/

    @FXML
    private void btnVerDetalles(ActionEvent event) {
        Envio envioSeleccionado = tvEnvios.getSelectionModel().getSelectedItem();
        
        if (envioSeleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/clienteescritoriopw/FXMLDetalleEnvio.fxml"));
                Parent root = loader.load();
                
                FXMLDetalleEnvioController controller = loader.getController();
                controller.inicializarEnvio(envioSeleccionado);
                
                Stage escenario = new Stage();
                escenario.setScene(new Scene(root));
                escenario.setTitle("Detalle del Envío y Paquetes");
                escenario.initModality(Modality.APPLICATION_MODAL);
                escenario.showAndWait();
                
                cargarInformacionTabla(); 
                
            } catch (IOException ex) {
                ex.printStackTrace();
                Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir el detalle del envío.", Alert.AlertType.ERROR);
            }
        } else {
            Utilidades.mostrarAlertaSimple("Selección Requerida", "Debes seleccionar un envío de la lista para ver sus detalles.", Alert.AlertType.WARNING);
        }
    }
    
    @FXML
    private void btnBuscar(ActionEvent event){
        filtrarEnvios(tfBusqueda.getText());
    }
    
    private void filtrarEnvios(String texto){
        if(texto == null || texto.isEmpty()){
            tvEnvios.setItems(listaEnvios);
        }else{
            ObservableList<Envio> filtrados = FXCollections.observableArrayList();
            for(Envio e : listaEnvios){
                if(e.getNumeroGuia().toLowerCase().contains(texto.toLowerCase()) || 
                   e.getNombreCliente().toLowerCase().contains(texto.toLowerCase())){
                    filtrados.add(e);
                }
            }
            tvEnvios.setItems(filtrados);
        }
    }
}
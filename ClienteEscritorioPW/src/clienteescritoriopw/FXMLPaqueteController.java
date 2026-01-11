package clienteescritoriopw;

import clienteescritoriopw.dominio.PaqueteImp;
import clienteescritoriopw.pojo.Paquete;
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

public class FXMLPaqueteController implements Initializable {

    @FXML private TextField tfBusqueda;
    @FXML private TableView<Paquete> tvPaquetes;
    
    // TODAS las columnas que aparecen en el FXML
    @FXML private TableColumn<Paquete, Integer> colIdEnvio;
    @FXML private TableColumn<Paquete, Integer> colIdPaquete;
    @FXML private TableColumn<Paquete, String> colDescripcion;
    @FXML private TableColumn<Paquete, Float> colPeso;
    @FXML private TableColumn<Paquete, Float> colAlto;
    @FXML private TableColumn<Paquete, Float> colAncho;
    @FXML private TableColumn<Paquete, Float> colProfundidad;

    private ObservableList<Paquete> listaPaquetes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarPaquetes();
    }     

    private void configurarTabla() {
        listaPaquetes = FXCollections.observableArrayList();
        
        // Configurar todas las columnas
        if (colIdEnvio != null) colIdEnvio.setCellValueFactory(new PropertyValueFactory<>("idEnvio"));
        if (colIdPaquete != null) colIdPaquete.setCellValueFactory(new PropertyValueFactory<>("idPaquete"));
        if (colDescripcion != null) colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        if (colPeso != null) colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        if (colAlto != null) colAlto.setCellValueFactory(new PropertyValueFactory<>("alto"));
        if (colAncho != null) colAncho.setCellValueFactory(new PropertyValueFactory<>("ancho"));
        if (colProfundidad != null) colProfundidad.setCellValueFactory(new PropertyValueFactory<>("profundidad"));
        
        if (tvPaquetes != null) {
            tvPaquetes.setItems(listaPaquetes);
        }
    }

    private void cargarPaquetes() {
        listaPaquetes.clear();
        List<Paquete> paquetes = PaqueteImp.obtenerTodos(); // Necesitarás implementar este método
        if (paquetes != null) {
            listaPaquetes.addAll(paquetes);
        }
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        ((Stage) tfBusqueda.getScene().getWindow()).close();
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        // Implementar búsqueda si es necesario
        String busqueda = tfBusqueda.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            cargarPaquetes();
        } else {
            // Filtrar paquetes por descripción
            ObservableList<Paquete> filtrados = FXCollections.observableArrayList();
            for (Paquete p : listaPaquetes) {
                if (p.getDescripcion().toLowerCase().contains(busqueda)) {
                    filtrados.add(p);
                }
            }
            tvPaquetes.setItems(filtrados);
        }
    }

    @FXML
    private void clicNuevo(ActionEvent event) {
        // Abrir formulario para nuevo paquete
        Utilidades.mostrarAlertaSimple("Información", "Esta funcionalidad se maneja desde el módulo de Envíos.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void clicEditar(ActionEvent event) {
        Utilidades.mostrarAlertaSimple("Información", "La edición de paquetes se realiza desde el detalle del envío.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        Utilidades.mostrarAlertaSimple("Información", "La eliminación de paquetes se realiza desde el detalle del envío.", Alert.AlertType.INFORMATION);
    }
}
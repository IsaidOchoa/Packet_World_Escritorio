package clienteescritoriopw;


import clienteescritoriopw.dominio.ClienteImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Cliente;
import clienteescritoriopw.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
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

public class FXMLClienteController implements Initializable {

    @FXML private TableView<Cliente> tvClientes;
    @FXML private TableColumn<Cliente, String> colNombre;
    
    @FXML private TableColumn<Cliente, String> colPaterno;
    @FXML private TableColumn<Cliente, String> colMaterno;
    
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private TableColumn<Cliente, String> colColonia;
    @FXML private TextField tfBusqueda;
    
    private ObservableList<Cliente> listaClientes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarDatosTabla();
    }    
    
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colPaterno.setCellValueFactory(new PropertyValueFactory("apellidoPaterno"));
        colMaterno.setCellValueFactory(new PropertyValueFactory("apellidoMaterno"));        
        colTelefono.setCellValueFactory(new PropertyValueFactory("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory("correo"));
        colColonia.setCellValueFactory(new PropertyValueFactory("nombreColonia"));
      
        colDireccion.setCellValueFactory(cellData -> {
            Cliente c = cellData.getValue();
            String calle = (c.getCalle() != null) ? c.getCalle() : "";
            String num = (c.getNumero() != null) ? c.getNumero() : "";
            return new SimpleStringProperty(calle + " #" + num);
        });
    }
    
    private void cargarDatosTabla() {
        listaClientes = FXCollections.observableArrayList();
        List<Cliente> respuestaWS = ClienteImp.obtenerClientes(); 
        
        if (respuestaWS != null) {
            listaClientes.addAll(respuestaWS);
            tvClientes.setItems(listaClientes);
        } else {
            Utilidades.mostrarAlertaSimple("Error", "No se pudo cargar la lista de clientes.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String busqueda = tfBusqueda.getText().trim().toLowerCase();

        if (busqueda.isEmpty()) {
            tvClientes.setItems(listaClientes);
            return;
        }

        ObservableList<Cliente> resultados = FXCollections.observableArrayList();

        for (Cliente c : listaClientes) {
            String nombre = (c.getNombre() != null) ? c.getNombre().toLowerCase() : "";
            String paterno = (c.getApellidoPaterno() != null) ? c.getApellidoPaterno().toLowerCase() : "";
            String materno = (c.getApellidoMaterno() != null) ? c.getApellidoMaterno().toLowerCase() : "";
            String correo = (c.getCorreo() != null) ? c.getCorreo().toLowerCase() : "";
            String tel = (c.getTelefono() != null) ? c.getTelefono().toLowerCase() : "";

            if (nombre.contains(busqueda) || paterno.contains(busqueda) || 
                materno.contains(busqueda) || correo.contains(busqueda) || tel.contains(busqueda)) {
                resultados.add(c);
            }
        }
        tvClientes.setItems(resultados);
    }

    @FXML
    private void clicNuevo(ActionEvent event) {
        abrirFormulario(null);
    }

    @FXML
    private void clicEditar(ActionEvent event) {
        Cliente seleccionado = tvClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            abrirFormulario(seleccionado);
        } else {
            Utilidades.mostrarAlertaSimple("Atención", "Selecciona un cliente para editar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        Cliente seleccionado = tvClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            boolean confirmar = Utilidades.mostrarAlertaConfirmacion("Eliminar Cliente", 
                    "¿Estás seguro de eliminar a " + seleccionado.getNombre() + "?");
            
            if (confirmar) {
                Respuesta respuesta = ClienteImp.eliminar(seleccionado.getIdCliente());
                if (!respuesta.isError()) {
                    Utilidades.mostrarAlertaSimple("Éxito", "Cliente eliminado.", Alert.AlertType.INFORMATION);
                    cargarDatosTabla();
                } else {
                    Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
                }
            }
        } else {
            Utilidades.mostrarAlertaSimple("Atención", "Selecciona un cliente para eliminar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        ((Stage) tfBusqueda.getScene().getWindow()).close();
    }
    
    private void abrirFormulario(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clienteescritoriopw/FXMLFormularioCliente.fxml"));
            Parent root = loader.load();
            
            FXMLFormularioClienteController controlador = loader.getController();
            if(cliente != null) 
            controlador.inicializarEdicion(cliente);
            
            Stage escenario = new Stage();
            escenario.setScene(new Scene(root));
            escenario.setTitle(cliente == null ? "Nuevo Cliente" : "Editar Cliente");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.setMinWidth(720);  // Ancho mínimo
            escenario.setMinHeight(550); // Alto mínimo
            escenario.showAndWait();
            
            cargarDatosTabla();
            
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Aviso", "El formulario de Cliente aún no está creado.", Alert.AlertType.INFORMATION);
        }
    }
}
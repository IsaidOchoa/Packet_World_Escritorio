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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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
    @FXML
    private TableColumn colEstatus;
    
    private ObservableList<Sucursal> listaSucursales;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarDatosTabla();
    }    
    
    private void configurarTabla(){
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colCalle.setCellValueFactory(new PropertyValueFactory("calle"));
        colNumero.setCellValueFactory(new PropertyValueFactory("numero"));
        colCodigoPostal.setCellValueFactory(new PropertyValueFactory("codigoPostal"));
        colColonia.setCellValueFactory(new PropertyValueFactory("nombreColonia")); 
        colMunicipio.setCellValueFactory(new PropertyValueFactory("municipio"));
        colEstado.setCellValueFactory(new PropertyValueFactory("estado"));
        colEstatus.setCellValueFactory(new PropertyValueFactory("estatusTexto"));
    }
    
    
    public void cargarDatosTabla(){
        listaSucursales = FXCollections.observableArrayList();
        List<Sucursal> respuestaWS = SucursalImp.obtenerSucursales();
        
        if(respuestaWS != null && !respuestaWS.isEmpty()){
            listaSucursales.addAll(respuestaWS);
            tvSucursales.setItems(listaSucursales);
        } else {
            Utilidades.mostrarAlertaSimple("Sin Resultados", "No se encontraron sucursales.", Alert.AlertType.INFORMATION);
        }
    }
    

    @FXML
    private void clicBuscar(ActionEvent event) {
        String busqueda = tfBusqueda.getText().trim().toLowerCase();

        // 1. Si no hay texto, restaurar la lista completa
        if (busqueda.isEmpty()) {
            tvSucursales.setItems(listaSucursales);
            return;
        }

        ObservableList<Sucursal> resultados = FXCollections.observableArrayList();

        for (Sucursal s : listaSucursales) {
            String nombre = (s.getNombre() != null) ? s.getNombre().toLowerCase() : "";
            String calle = (s.getCalle() != null) ? s.getCalle().toLowerCase() : "";
            String colonia = (s.getNombreColonia() != null) ? s.getNombreColonia().toLowerCase() : ""; // Ojo: usa getNombreColonia()
            String cp = (s.getCodigoPostal() != null) ? s.getCodigoPostal().toLowerCase() : "";
            String municipio = (s.getMunicipio() != null) ? s.getMunicipio().toLowerCase() : "";
            String estado = (s.getEstado() != null) ? s.getEstado().toLowerCase() : "";

            if (nombre.contains(busqueda) || 
                calle.contains(busqueda) || 
                colonia.contains(busqueda) || 
                cp.contains(busqueda) || 
                municipio.contains(busqueda) || 
                estado.contains(busqueda)) {
                
                resultados.add(s);
            }
        }

        tvSucursales.setItems(resultados);
        
        
    }
    

    @FXML
    private void clicNuevo(ActionEvent event) {
        abrirFormulario(null);
    
    }

    @FXML
    private void clicEditar(ActionEvent event) {
        Sucursal seleccionado = tvSucursales.getSelectionModel().getSelectedItem();
        if(seleccionado != null){
            System.out.println("Editando a: " + seleccionado.getNombre());
        }else{
            Utilidades.mostrarAlertaSimple("Selección requerida", "Selecciona una sucursal para editar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        Sucursal seleccionado = tvSucursales.getSelectionModel().getSelectedItem();
        if(seleccionado != null){
            
            // Validar que no esté ya dada de baja
            if(seleccionado.getEstatus() != null && seleccionado.getEstatus() == 0) {
                 Utilidades.mostrarAlertaSimple("Aviso", "La sucursal ya está inactiva.", Alert.AlertType.WARNING);
                 return;
            }

            boolean confirmar = Utilidades.mostrarAlertaConfirmacion(
                    "Dar de Baja Sucursal", 
                    "¿Estás seguro de inhabilitar la sucursal " + seleccionado.getNombre() + "?"
            );
            
            if(confirmar){
                // Llamamos al servicio eliminar.
                // IMPORTANTE: Tu Backend debe tener el UPDATE estatus=0 en el mapper de "eliminar"
                // para que sea una baja lógica y no física.
                clienteescritoriopw.dto.Respuesta respuesta = SucursalImp.eliminar(seleccionado.getIdSucursal());
                
                if(!respuesta.isError()){
                    Utilidades.mostrarAlertaSimple("Éxito", "Sucursal dada de baja correctamente.", Alert.AlertType.INFORMATION);
                    cargarDatosTabla(); 
                }else{
                    Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
                }
            }
        }else{
            Utilidades.mostrarAlertaSimple("Selección requerida", "Selecciona una sucursal para dar de baja.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        Stage escenario = (Stage) tfBusqueda.getScene().getWindow();
        escenario.close();
    }
    
    private void abrirFormulario(Sucursal sucursal) {
        try {
            // Asegúrate de importar javafx.fxml.FXMLLoader, javafx.scene.Parent, etc.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clienteescritoriopw/FXMLFormularioSucursal.fxml"));
            javafx.scene.Parent root = loader.load();
            
            FXMLFormularioSucursalController controlador = loader.getController();
            
            if(sucursal != null){
                controlador.inicializarEdicion(sucursal);
            }
            
            Stage escenario = new Stage();
            escenario.setScene(new javafx.scene.Scene(root));
            escenario.setTitle(sucursal == null ? "Nueva Sucursal" : "Editar Sucursal");
            escenario.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            
            escenario.showAndWait();
            cargarDatosTabla(); // Recargar la tabla al cerrar
            
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo cargar el formulario.", Alert.AlertType.ERROR);
        }
    }
}
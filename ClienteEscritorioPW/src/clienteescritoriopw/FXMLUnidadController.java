package clienteescritoriopw;

import clienteescritoriopw.dominio.UnidadImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Colaborador;
import clienteescritoriopw.pojo.Unidad;
import clienteescritoriopw.utilidad.Permisos;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLUnidadController implements Initializable {

    @FXML private TableView<Unidad> tvUnidades;
    @FXML private TableColumn<Unidad, String> colMarca;
    @FXML private TableColumn<Unidad, String> colModelo;
    @FXML private TableColumn<Unidad, String> colAnio;
    @FXML private TableColumn<Unidad, String> colVin;
    @FXML private TableColumn<Unidad, String> colNoIdentificacion;
    @FXML private TableColumn<Unidad, String> colTipo;
    @FXML private TableColumn<Unidad, String> colSucursal;
    @FXML private TableColumn<Unidad, String> colConductor;
    
    @FXML private TextField tfBusqueda;
    
    @FXML private Button btNuevo;
    @FXML private Button btAsignar;
    @FXML private Button btEditar;
    @FXML private Button btEliminar;

    private ObservableList<Unidad> listaUnidades;
    private Colaborador colaboradorSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarDatosTabla();
    }    

    public void inicializarColaborador(Colaborador colaborador) {
        this.colaboradorSesion = colaborador;
        if (colaborador != null) {
            aplicarPermisos();
        }
    }
    
    private void aplicarPermisos() {
        if (Permisos.esConductor(colaboradorSesion.getIdRol())) {
            btNuevo.setVisible(false);
            btAsignar.setVisible(false);
            btEditar.setVisible(false);
            btEliminar.setVisible(false);
        }
    }

    private void configurarTabla() {
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colVin.setCellValueFactory(new PropertyValueFactory<>("vin"));
        colNoIdentificacion.setCellValueFactory(new PropertyValueFactory<>("nii"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("nombreTipo")); 
        colSucursal.setCellValueFactory(new PropertyValueFactory<>("nombreSucursal"));
        colConductor.setCellValueFactory(new PropertyValueFactory<>("nombreColaborador"));
    }
    
    private void cargarDatosTabla() {  
        List<Unidad> lista = UnidadImp.obtenerUnidades();
        if (lista != null) {
            listaUnidades = FXCollections.observableArrayList(lista);
            tvUnidades.setItems(listaUnidades);
        } else {
            Utilidades.mostrarAlertaSimple("Error", "No se pudo cargar la lista de unidades.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String busqueda = tfBusqueda.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            tvUnidades.setItems(listaUnidades);
            return;
        }

        ObservableList<Unidad> resultados = FXCollections.observableArrayList();
        for (Unidad u : listaUnidades) {
            String marca = (u.getMarca() != null) ? u.getMarca().toLowerCase() : "";
            String vin = (u.getVin() != null) ? u.getVin().toLowerCase() : "";
            String nii = (u.getNii() != null) ? u.getNii().toLowerCase() : "";
            if (marca.contains(busqueda) || vin.contains(busqueda) || nii.contains(busqueda)) {
                resultados.add(u);
            }
        }
        tvUnidades.setItems(resultados);
    }
    

    @FXML
    private void clicNuevo(ActionEvent event) {
        abrirFormulario(null);
    }

    @FXML
    private void clicEditar(ActionEvent event) {
        Unidad seleccionada = tvUnidades.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            abrirFormulario(seleccionada);
        } else {
            Utilidades.mostrarAlertaSimple("Atención", "Selecciona una unidad para editar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicEliminar(ActionEvent event) {
        Unidad seleccionada = tvUnidades.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            boolean confirmar = Utilidades.mostrarAlertaConfirmacion("Eliminar Unidad", 
                    "¿Estás seguro de eliminar la unidad " + seleccionada.getMarca() + " " + seleccionada.getModelo() + "?");
            
            if (confirmar) {
                Respuesta respuesta = UnidadImp.eliminar(seleccionada.getIdUnidad());
                if (!respuesta.isError()) {
                    Utilidades.mostrarAlertaSimple("Éxito", "Unidad eliminada.", Alert.AlertType.INFORMATION);
                    cargarDatosTabla();
                } else {
                    Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
                }
            }
        } else {
            Utilidades.mostrarAlertaSimple("Atención", "Selecciona una unidad para eliminar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        ((Stage) tfBusqueda.getScene().getWindow()).close();
    }
    
    private void abrirFormulario(Unidad unidad) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLFormularioUnidad.fxml")); 
            Parent root = loader.load();
            
            FXMLFormularioUnidadController controlador = loader.getController();
            if (unidad != null) {
                controlador.inicializarEdicion(unidad); 
            }
            
            Stage escenario = new Stage();
            escenario.setScene(new Scene(root));
            escenario.setTitle(unidad == null ? "Nueva Unidad" : "Editar Unidad");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
            cargarDatosTabla();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML
    private void clicAsignar(ActionEvent event) {
        Unidad seleccionada = tvUnidades.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLAsignarConductor.fxml"));
                Parent root = loader.load();
                
                FXMLAsignarConductorController controlador = loader.getController();
                controlador.inicializarDatos(seleccionada);
                
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Asignar Conductor");
                stage.showAndWait();
                
                cargarDatosTabla(); 
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            Utilidades.mostrarAlertaSimple("Atención", "Selecciona una unidad para asignar.", Alert.AlertType.WARNING);
        }
    }
}
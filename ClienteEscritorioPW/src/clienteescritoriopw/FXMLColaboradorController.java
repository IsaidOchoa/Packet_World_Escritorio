package clienteescritoriopw;

import clienteescritoriopw.dominio.ColaboradorImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Colaborador;
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
    
    @FXML private Button btNuevo;
    @FXML private Button btEditar;
    @FXML private Button btEliminar;
    
    private ObservableList<Colaborador> listaColaboradores;
    private Colaborador colaboradorSesion;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
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
    
    public void inicializarColaborador(Colaborador colaborador) {
        this.colaboradorSesion = colaborador;
        if (colaborador != null) {
            aplicarPermisos();
            cargarDatosTabla();
        }
    }

    private void aplicarPermisos() {
        if (Permisos.esEjecutivo(colaboradorSesion.getIdRol())) {
            btEditar.setVisible(false);
            btEliminar.setVisible(false);
        } else if (Permisos.esConductor(colaboradorSesion.getIdRol())) {
            btNuevo.setVisible(false);
            btEditar.setVisible(false);
            btEliminar.setVisible(false);
        }
    }
    private void cargarDatosTabla() {
        listaColaboradores = FXCollections.observableArrayList();
        List<Colaborador> todosLosColaboradores = ColaboradorImp.obtenerColaboradores();

        if (todosLosColaboradores != null) {
            if (colaboradorSesion != null && Permisos.esEjecutivo(colaboradorSesion.getIdRol())) {
                // Filtrar: solo colaboradores de la misma sucursal que el ejecutivo
                for (Colaborador c : todosLosColaboradores) {
                    if (c.getIdSucursal() == colaboradorSesion.getIdSucursal()) {
                        listaColaboradores.add(c);
                    }
                }
            } else {
                listaColaboradores.addAll(todosLosColaboradores);
            }

            tvColaboradores.setItems(listaColaboradores);
        } else {
            Utilidades.mostrarAlertaSimple("Error", "No se pudo cargar la lista de colaboradores.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        String busqueda = tfBusqueda.getText().trim().toLowerCase();

        if (busqueda.isEmpty()) {
            tvColaboradores.setItems(listaColaboradores); 
            return;
        }

        ObservableList<Colaborador> resultados = FXCollections.observableArrayList();

        for (Colaborador c : listaColaboradores) {
            String nombre = (c.getNombre() != null) ? c.getNombre().toLowerCase() : "";
            String paterno = (c.getApellidoPaterno() != null) ? c.getApellidoPaterno().toLowerCase() : "";
            String materno = (c.getApellidoMaterno() != null) ? c.getApellidoMaterno().toLowerCase() : "";
            String nombreCompleto = (nombre + " " + paterno + " " + materno).trim();

            String noPersonal = (c.getNumeroPersonal() != null) ? c.getNumeroPersonal().toLowerCase() : "";
            String rol = (c.getRol() != null) ? c.getRol().toLowerCase() : "";

            if (nombreCompleto.contains(busqueda) || 
                noPersonal.contains(busqueda) || 
                rol.contains(busqueda)) {

                resultados.add(c);
            }
        }

        tvColaboradores.setItems(resultados);
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
    
    private void abrirFormulario(Colaborador colaborador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clienteescritoriopw/FXMLFormularioColaborador.fxml"));
                Parent root = loader.load();
            
            FXMLFormularioColaboradorController controlador = loader.getController();
            
            if (colaborador != null) {
                controlador.inicializarEdicion(colaborador);
            }
            
            Stage escenario = new Stage();
            escenario.setScene(new Scene(root));
            escenario.setTitle((colaborador == null) ? "Registrar Colaborador" : "Editar Colaborador");
            escenario.initModality(Modality.APPLICATION_MODAL);
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
        if (seleccionado != null) {
            if (colaboradorSesion != null && 
                seleccionado.getIdColaborador() == colaboradorSesion.getIdColaborador()) {
                Utilidades.mostrarAlertaSimple(
                    "Acción no permitida", 
                    "No puedes eliminarte a ti mismo.", 
                    Alert.AlertType.WARNING
                );
                return;
            }
            boolean confirmar = Utilidades.mostrarAlertaConfirmacion(
                "Eliminar Colaborador", 
                "¿Estás seguro de eliminar a " + seleccionado.getNombre() + "?"
            );
            
            if (confirmar) {
                // Llama al método que ahora recibe dos IDs
                Respuesta respuesta = ColaboradorImp.eliminar(
                    seleccionado.getIdColaborador(), 
                    colaboradorSesion.getIdColaborador()
                );
                
                if (!respuesta.isError()) {
                    Utilidades.mostrarAlertaSimple("Éxito", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
                    cargarDatosTabla();
                } else {
                    Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
                }
            }
        } else {
            Utilidades.mostrarAlertaSimple("Selección requerida", "Selecciona un colaborador para eliminar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        Stage escenario = (Stage) tfBusqueda.getScene().getWindow();
        escenario.close();
    }
}
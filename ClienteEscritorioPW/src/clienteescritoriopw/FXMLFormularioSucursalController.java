package clienteescritoriopw;

import clienteescritoriopw.dominio.DireccionImp;
import clienteescritoriopw.dominio.SucursalImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Colonia;
import clienteescritoriopw.pojo.Estado;
import clienteescritoriopw.pojo.Municipio;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLFormularioSucursalController implements Initializable {

    @FXML private Label lblTitulo;  // Corregido: debe coincidir con FXML
    @FXML private TextField tfNombre;
    @FXML private TextField tfCalle;
    @FXML private TextField tfNumero;
    @FXML private TextField tfCP; // Solo lectura
    @FXML private ComboBox<Estado> cbEstado;
    @FXML private ComboBox<Municipio> cbMunicipio;
    @FXML private ComboBox<Colonia> cbColonia;

    // 👇 VARIABLE DECLARADA AQUÍ
    private Sucursal sucursalEdicion;

    private ObservableList<Estado> listaEstados;
    private ObservableList<Municipio> listaMunicipios;
    private ObservableList<Colonia> listaColonias;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarCombos();
        cargarEstados(); // Cargar estados al inicio
        configurarListeners();
    }
    
    private void inicializarCombos() {
        listaEstados = FXCollections.observableArrayList();
        cbEstado.setItems(listaEstados);
        
        listaMunicipios = FXCollections.observableArrayList();
        cbMunicipio.setItems(listaMunicipios);
        cbMunicipio.setDisable(true); // Deshabilitado hasta seleccionar estado
        
        listaColonias = FXCollections.observableArrayList();
        cbColonia.setItems(listaColonias);
        cbColonia.setDisable(true); // Deshabilitado hasta seleccionar municipio
    }

    private void configurarListeners() {
        // Al seleccionar estado → cargar municipios
        cbEstado.setOnAction(e -> {
            Estado estado = cbEstado.getValue();
            if (estado != null) {
                listaMunicipios.clear();
                cbMunicipio.setDisable(false);
                cargarMunicipios(estado.getIdEstado());
            } else {
                listaMunicipios.clear();
                cbMunicipio.setDisable(true);
                listaColonias.clear();
                cbColonia.setDisable(true);
                tfCP.clear();
            }
        });
        
        // Al seleccionar municipio → cargar colonias
        cbMunicipio.setOnAction(e -> {
            Municipio municipio = cbMunicipio.getValue();
            if (municipio != null) {
                listaColonias.clear();
                cbColonia.setDisable(false);
                cargarColonias(municipio.getIdMunicipio());
            } else {
                listaColonias.clear();
                cbColonia.setDisable(true);
                tfCP.clear();
            }
        });
        
        // Al seleccionar colonia → llenar CP
        cbColonia.setOnAction(e -> {
            Colonia colonia = cbColonia.getValue();
            if (colonia != null) {
                tfCP.setText(colonia.getCodigoPostal());
            } else {
                tfCP.clear();
            }
        });
    }
    
    private void cargarEstados() {
        List<Estado> estados = DireccionImp.obtenerEstados();
        if (estados != null) listaEstados.addAll(estados);
    }

    private void cargarMunicipios(int idEstado) {
        List<Municipio> municipios = DireccionImp.obtenerMunicipios(idEstado);
        if (municipios != null) listaMunicipios.addAll(municipios);
    }

    private void cargarColonias(int idMunicipio) {
        List<Colonia> colonias = DireccionImp.obtenerColoniasPorMunicipio(idMunicipio);
        if (colonias != null) listaColonias.addAll(colonias);
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if (tfNombre.getText().isEmpty() || tfCalle.getText().isEmpty() || 
            tfNumero.getText().isEmpty() || cbColonia.getValue() == null) {
            Utilidades.mostrarAlertaSimple("Campos requeridos", "Completa todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        Sucursal s = new Sucursal();
        s.setNombre(tfNombre.getText().trim());
        s.setCalle(tfCalle.getText().trim());
        s.setNumero(tfNumero.getText().trim());
        s.setIdColonia(cbColonia.getValue().getIdColonia());

        if (sucursalEdicion == null) {
            procesar(SucursalImp.registrar(s), "creada");
        } else {
            s.setIdSucursal(sucursalEdicion.getIdSucursal());
            procesar(SucursalImp.editar(s), "actualizada");
        }
    }
    
    private void procesar(Respuesta r, String accion) {
        if (!r.isError()) {
            Utilidades.mostrarAlertaSimple("Éxito", "Sucursal " + accion + " correctamente.", Alert.AlertType.INFORMATION);
            ((Stage) tfNombre.getScene().getWindow()).close();
        } else {
            Utilidades.mostrarAlertaSimple("Error", r.getMensaje(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML 
    private void clicCancelar(ActionEvent event) {
        ((Stage) tfNombre.getScene().getWindow()).close();
    }
    
    public void inicializarEdicion(Sucursal s) {
        this.sucursalEdicion = s;
        lblTitulo.setText("Editar Sucursal");  // Corregido: usa lblTitulo
        
        tfNombre.setText(s.getNombre());
        tfCalle.setText(s.getCalle());
        tfNumero.setText(s.getNumero());
        
        // Cargar la ubicación jerárquica
        if (s.getIdColonia() != null) {
            // Primero cargar estados
            cargarEstados();
            
            // Buscar y seleccionar el estado
            for (Estado e : listaEstados) {
                if (e.getIdEstado().equals(s.getEstado())) {
                    cbEstado.getSelectionModel().select(e);
                    // Forzar carga de municipios
                    cargarMunicipios(e.getIdEstado());
                    break;
                }
            }
            
            // Buscar y seleccionar el municipio
            for (Municipio m : listaMunicipios) {
                if (m.getIdMunicipio().equals(s.getMunicipio())) {
                    cbMunicipio.getSelectionModel().select(m);
                    // Forzar carga de colonias
                    cargarColonias(m.getIdMunicipio());
                    break;
                }
            }
            
            // Buscar y seleccionar la colonia
            for (Colonia c : listaColonias) {
                if (c.getIdColonia().equals(s.getIdColonia())) {
                    cbColonia.getSelectionModel().select(c);
                    break;
                }
            }
        }
    }
}
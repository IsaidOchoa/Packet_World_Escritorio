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
import javafx.util.StringConverter;

public class FXMLFormularioSucursalController implements Initializable {

    @FXML private Label lbTitulo;
    @FXML private TextField tfNombre;
    @FXML private TextField tfCalle;
    @FXML private TextField tfNumero;
    @FXML private TextField tfCP; // Solo para buscar, no se guarda en BD
    @FXML private ComboBox<Estado> cbEstado;
    @FXML private ComboBox<Municipio> cbMunicipio;
    @FXML private ComboBox<Colonia> cbColonia;

    private Sucursal sucursalEdicion;
    private ObservableList<Estado> listaEstados;
    private ObservableList<Municipio> listaMunicipios;
    private ObservableList<Colonia> listaColonias;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarCombos();
        cargarEstados();
        configurarListeners();
    }
    
    private void inicializarCombos(){
        listaEstados = FXCollections.observableArrayList();
        cbEstado.setItems(listaEstados);
        listaMunicipios = FXCollections.observableArrayList();
        cbMunicipio.setItems(listaMunicipios);
        listaColonias = FXCollections.observableArrayList();
        cbColonia.setItems(listaColonias);
        
        configurarCombo(cbEstado);
        configurarCombo(cbMunicipio);
        configurarCombo(cbColonia);
    }
    
    // Configuración para que se vean los nombres en los combos
    private <T> void configurarCombo(ComboBox<T> combo) {
        combo.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T object) {
                if (object == null) return null;
                // Usamos el toString() que definiste en tus POJOs (o casteo si es necesario)
                return object.toString();
            }
            @Override
            public T fromString(String string) { return null; }
        });
    }

    private void configurarListeners() {
        cbEstado.valueProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                cargarMunicipios(nuevo.getIdEstado());
                cbMunicipio.setDisable(false);
            } else {
                listaMunicipios.clear();
                cbMunicipio.setDisable(true);
            }
            listaColonias.clear();
        });

        cbMunicipio.valueProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                // Solo cargamos todas si no estamos buscando por CP
                if(tfCP.getText().isEmpty()){
                    cargarColonias(nuevo.getIdMunicipio());
                }
                cbColonia.setDisable(false);
            } else {
                listaColonias.clear();
                cbColonia.setDisable(true);
            }
        });
        
        // Al seleccionar colonia, llenamos el CP visualmente
        cbColonia.valueProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                tfCP.setText(String.valueOf(nuevo.getCodigoPostal()));
            }
        });
    }

    private void cargarEstados() {
        List<Estado> lista = DireccionImp.obtenerEstados();
        if(lista != null) listaEstados.addAll(lista);
    }
    
    private void cargarMunicipios(int idEstado){
        listaMunicipios.clear();
        List<Municipio> lista = DireccionImp.obtenerMunicipios(idEstado);
        if(lista != null) listaMunicipios.addAll(lista);
    }
    
    private void cargarColonias(int idMunicipio){
        listaColonias.clear();
        List<Colonia> lista = DireccionImp.obtenerColoniasPorMunicipio(idMunicipio);
        if(lista != null) listaColonias.addAll(lista);
    }

    @FXML
    private void clicBuscarCP(ActionEvent event) {
        String cp = tfCP.getText().trim();
        if(cp.length() != 5){
             Utilidades.mostrarAlertaSimple("CP Incorrecto", "El CP debe tener 5 dígitos.", Alert.AlertType.WARNING);
             return;
        }

        List<Colonia> colonias = DireccionImp.buscarPorCP(cp);
        if(colonias != null && !colonias.isEmpty()){
            Colonia col = colonias.get(0);
            
            // 1. Estado
            if(col.getIdEstado() != null){
                for(Estado e : cbEstado.getItems()){
                    if(e.getIdEstado().equals(col.getIdEstado())){
                        cbEstado.getSelectionModel().select(e);
                        break;
                    }
                }
            }
            //Municipio
             for(Municipio m : cbMunicipio.getItems()){
                if(m.getIdMunicipio().equals(col.getIdMunicipio())){
                    cbMunicipio.getSelectionModel().select(m);
                    break;
                }
            }
            //Colonias (Mostramos solo las del CP)
            cbColonia.getItems().clear();
            cbColonia.getItems().addAll(colonias);
            cbColonia.getSelectionModel().select(0);
        } else {
            Utilidades.mostrarAlertaSimple("Sin resultados", "No se encontraron colonias para el CP: " + cp, Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if(tfNombre.getText().isEmpty() || tfCalle.getText().isEmpty() || 
           tfNumero.getText().isEmpty() || cbColonia.getValue() == null){
            Utilidades.mostrarAlertaSimple("Campos vacíos", "Por favor llena todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        Sucursal s = new Sucursal();
        s.setNombre(tfNombre.getText().trim());
        s.setCalle(tfCalle.getText().trim());
        s.setNumero(tfNumero.getText().trim());
        
        // Guardamos el ID de la colonia seleccionada
        if(cbColonia.getValue() != null)
            s.setIdColonia(cbColonia.getValue().getIdColonia());
        
        if(sucursalEdicion == null){
            procesar(SucursalImp.registrar(s), "registrada");
        } else {
            s.setIdSucursal(sucursalEdicion.getIdSucursal());
            procesar(SucursalImp.editar(s), "actualizada");
        }
    }
    
    private void procesar(Respuesta r, String accion){
        if(!r.isError()){
            Utilidades.mostrarAlertaSimple("Éxito", "Sucursal " + accion + " correctamente.", Alert.AlertType.INFORMATION);
            ((Stage) tfNombre.getScene().getWindow()).close();
        } else {
            Utilidades.mostrarAlertaSimple("Error", r.getMensaje(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML private void clicCancelar(ActionEvent event) {
        ((Stage) tfNombre.getScene().getWindow()).close();
    }
    
    public void inicializarEdicion(Sucursal s){
        this.sucursalEdicion = s;
        lbTitulo.setText("Editar Sucursal");
        
        tfNombre.setText(s.getNombre());
        tfCalle.setText(s.getCalle());
        tfNumero.setText(s.getNumero());
        
        // Usamos el CP para recuperar la ubicación
        if(s.getCodigoPostal() != null && !s.getCodigoPostal().isEmpty()){
            tfCP.setText(s.getCodigoPostal());
            clicBuscarCP(null);
            
            // Refinamos la selección de colonia
            if(s.getIdColonia() != null && s.getIdColonia() > 0){
                 for(Colonia c : cbColonia.getItems()){
                     if(c.getIdColonia().equals(s.getIdColonia())){
                         cbColonia.getSelectionModel().select(c);
                         break;
                     }
                 }
            }
        }
    }
}
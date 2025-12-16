package clienteescritoriopw;

import clienteescritoriopw.dominio.ColoniaImp;
import clienteescritoriopw.dominio.SucursalImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Colonia;
import clienteescritoriopw.pojo.Sucursal;
import clienteescritoriopw.utilidad.Utilidades;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfCalle;
    @FXML
    private TextField tfNumero;
    @FXML
    private TextField tfCodigoPostal;
    @FXML
    private ComboBox<Colonia> cbColonia; // CAMBIO: Ahora es ComboBox
    @FXML
    private TextField tfMunicipio;
    @FXML
    private TextField tfEstado;

    private Sucursal sucursalEdicion;
    private ObservableList<Colonia> listaColonias;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaColonias = FXCollections.observableArrayList();
        cbColonia.setItems(listaColonias);
        
        configurarListeners();
        configurarComboBox();
    }    
    
    private void configurarComboBox() {
        // Para que el ComboBox muestre el nombre de la colonia y no el objeto raro
        cbColonia.setConverter(new StringConverter<Colonia>() {
            @Override
            public String toString(Colonia object) {
                return (object != null) ? object.getNombre() : "";
            }
            @Override
            public Colonia fromString(String string) { return null; }
        });
    }
    
    private void configurarListeners() {
        // 1. Al perder el foco del campo CP, buscar colonias
        tfCodigoPostal.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // Si perdió el foco
                buscarColonias();
            }
        });
        
        // 2. Al seleccionar una colonia, llenar Municipio y Estado automáticamente
        cbColonia.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                tfMunicipio.setText(newVal.getNombreMunicipio());
                tfEstado.setText(newVal.getNombreEstado());
            }
        });
    }
    
    private void buscarColonias() {
        String cp = tfCodigoPostal.getText().trim();
        if (cp.length() == 5) { // Validación básica de CP
            listaColonias.clear();
            List<Colonia> resultado = ColoniaImp.buscarPorCP(cp);
            if (resultado != null && !resultado.isEmpty()) {
                listaColonias.addAll(resultado);
            } else {
                Utilidades.mostrarAlertaSimple("Sin resultados", "No se encontraron colonias con ese CP.", Alert.AlertType.WARNING);
            }
        }
    }

    public void inicializarEdicion(Sucursal sucursal) {
        this.sucursalEdicion = sucursal;
        lblTitulo.setText("Editar Sucursal");
        
        tfNombre.setText(sucursal.getNombre());
        tfCalle.setText(sucursal.getCalle());
        tfNumero.setText(sucursal.getNumero());
        
        // IMPORTANTE: Aquí deberías tener el CP en tu objeto sucursal para poder cargar las colonias
        // Si tu objeto sucursal NO tiene getCodigoPostal(), tendrás que obtenerlo de la relación con Colonia
        // Por ahora asumo que sucursal tiene un CP guardado o accesible.
        // tfCodigoPostal.setText(sucursal.getCodigoPostal()); 
        // buscarColonias(); // Cargar la lista para poder seleccionar la correcta
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if(validarCampos()){
            Sucursal sucursal = new Sucursal();
            if(sucursalEdicion != null){
                sucursal.setIdSucursal(sucursalEdicion.getIdSucursal());
            }
            
            sucursal.setNombre(tfNombre.getText());
            sucursal.setCalle(tfCalle.getText());
            sucursal.setNumero(tfNumero.getText());
            
            // AQUÍ ES DONDE CAMBIA TODO: Guardamos el ID de la Colonia seleccionada
            if (cbColonia.getValue() != null) {
                sucursal.setIdColonia(cbColonia.getValue().getIdColonia());
            }

            Respuesta respuesta = (sucursalEdicion == null) ? 
                SucursalImp.registrar(sucursal) : SucursalImp.editar(sucursal);
            
            if(!respuesta.isError()){
                Utilidades.mostrarAlertaSimple("Éxito", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
                cerrarVentana();
            }else{
                Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        ((Stage) tfNombre.getScene().getWindow()).close();
    }
    
    private boolean validarCampos() {
        if(tfNombre.getText().trim().isEmpty() || tfCalle.getText().trim().isEmpty() ||
           cbColonia.getValue() == null){
            Utilidades.mostrarAlertaSimple("Campos requeridos", "Nombre, Calle, CP y Colonia son obligatorios.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
}
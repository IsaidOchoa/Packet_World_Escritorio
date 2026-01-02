package clienteescritoriopw;

import clienteescritoriopw.dominio.CatalogoImp;
import clienteescritoriopw.dominio.SucursalImp; // IMPORTANTE
import clienteescritoriopw.dominio.UnidadImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Sucursal; // IMPORTANTE
import clienteescritoriopw.pojo.TipoUnidad;
import clienteescritoriopw.pojo.Unidad;
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

public class FXMLFormularioUnidadController implements Initializable {

    @FXML private Label lbTitulo;
    @FXML private TextField tfMarca;
    @FXML private TextField tfModelo;
    @FXML private TextField tfAnio;
    @FXML private TextField tfVin;
    @FXML private TextField tfNII;
    @FXML private ComboBox<TipoUnidad> cbTipoUnidad;
    
    // --- NUEVO CAMPO ---
    @FXML private ComboBox<Sucursal> cbSucursal; 
    
    private Unidad unidadEdicion;
    private ObservableList<TipoUnidad> listaTipos;
    private ObservableList<Sucursal> listaSucursales; // Lista para el combo

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTiposUnidad();
        cargarSucursales(); // --- LLAMADA NUEVA ---
        configurarListenersNII();
    }
    
    private void cargarTiposUnidad() {
        listaTipos = FXCollections.observableArrayList();
        List<TipoUnidad> lista = CatalogoImp.obtenerTiposUnidad();
        if(lista != null) listaTipos.addAll(lista);
        cbTipoUnidad.setItems(listaTipos);
        
        // Convertidor para que se vea el nombre bonito
        cbTipoUnidad.setConverter(new StringConverter<TipoUnidad>() {
            @Override
            public String toString(TipoUnidad t) { return (t != null) ? t.getNombre() : null; }
            @Override
            public TipoUnidad fromString(String string) { return null; }
        });
    }

    // --- MÉTODO NUEVO PARA CARGAR SUCURSALES ---
    private void cargarSucursales() {
        listaSucursales = FXCollections.observableArrayList();
        List<Sucursal> lista = SucursalImp.obtenerSucursales(); // Reutilizamos tu método existente
        if(lista != null) listaSucursales.addAll(lista);
        cbSucursal.setItems(listaSucursales);
        
        cbSucursal.setConverter(new StringConverter<Sucursal>() {
            @Override
            public String toString(Sucursal s) { return (s != null) ? s.getNombre() : null; }
            @Override
            public Sucursal fromString(String string) { return null; }
        });
    }

    private void configurarListenersNII() {
        // ... (Tu código de listeners igual que antes) ...
        tfAnio.textProperty().addListener((obs, viejo, nuevo) -> calcularNII());
        tfVin.textProperty().addListener((obs, viejo, nuevo) -> calcularNII());
    }
    
    private void calcularNII() {
        String anio = tfAnio.getText().trim();
        String vin = tfVin.getText().trim();
        if(!anio.isEmpty() && vin.length() >= 4){
            tfNII.setText(anio + vin.substring(0, 4).toUpperCase());
        } else {
            tfNII.setText("");
        }
    }
    
    public void inicializarEdicion(Unidad unidad) {
        this.unidadEdicion = unidad;
        lbTitulo.setText("Editar Unidad");
        
        tfMarca.setText(unidad.getMarca());
        tfModelo.setText(unidad.getModelo());
        tfAnio.setText(String.valueOf(unidad.getAnio()));
        tfVin.setText(unidad.getVin());
        tfNII.setText(unidad.getNii());
        tfVin.setDisable(true); 
        
        // Seleccionar Tipo
        for(TipoUnidad t : cbTipoUnidad.getItems()){
            if(t.getIdTipoUnidad() == unidad.getIdTipoUnidad()){
                cbTipoUnidad.getSelectionModel().select(t);
                break;
            }
        }
        
        // --- SELECCIONAR SUCURSAL SI ES EDICIÓN ---
        for(Sucursal s : cbSucursal.getItems()){
            if(s.getIdSucursal() == unidad.getIdSucursal()){
                cbSucursal.getSelectionModel().select(s);
                break;
            }
        }
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if(validarCampos()){
            Unidad unidad = new Unidad();
            unidad.setMarca(tfMarca.getText().trim());
            unidad.setModelo(tfModelo.getText().trim());
            try {
                unidad.setAnio(Integer.parseInt(tfAnio.getText().trim()));
            } catch (NumberFormatException e) { return; }
            
            unidad.setVin(tfVin.getText().trim());
            unidad.setNii(tfNII.getText().trim());
            unidad.setIdTipoUnidad(cbTipoUnidad.getValue().getIdTipoUnidad());
            
            // --- AQUÍ GUARDAMOS LA SUCURSAL SELECCIONADA ---
            unidad.setIdSucursal(cbSucursal.getValue().getIdSucursal());
            
            if(unidadEdicion == null) {
                procesarRespuesta(UnidadImp.registrar(unidad), "registrada");
            } else {
                unidad.setIdUnidad(unidadEdicion.getIdUnidad());
                procesarRespuesta(UnidadImp.editar(unidad), "actualizada");
            }
        }
    }
    
    private boolean validarCampos() {
        String msg = "";
        if(tfMarca.getText().isEmpty()) msg += "- Marca\n";
        if(tfModelo.getText().isEmpty()) msg += "- Modelo\n";
        if(tfAnio.getText().isEmpty()) msg += "- Año\n";
        if(tfVin.getText().isEmpty()) msg += "- VIN\n";
        if(cbTipoUnidad.getValue() == null) msg += "- Tipo de Unidad\n";
        // Validar sucursal
        if(cbSucursal.getValue() == null) msg += "- Sucursal\n";
        
        if(!msg.isEmpty()){
            Utilidades.mostrarAlertaSimple("Campos vacíos", "Faltan:\n" + msg, Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
    
    // ... (Métodos procesarRespuesta y clicCancelar igual que antes) ...
    private void procesarRespuesta(Respuesta resp, String accion) {
        if(!resp.isError()) {
            Utilidades.mostrarAlertaSimple("Éxito", "Unidad " + accion + " correctamente.", Alert.AlertType.INFORMATION);
            clicCancelar(null);
        } else {
            Utilidades.mostrarAlertaSimple("Error", resp.getMensaje(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML private void clicCancelar(ActionEvent event) {
        ((Stage) tfMarca.getScene().getWindow()).close();
    }
}
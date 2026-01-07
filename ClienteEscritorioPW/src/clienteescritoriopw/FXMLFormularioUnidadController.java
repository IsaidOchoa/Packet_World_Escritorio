package clienteescritoriopw;

import clienteescritoriopw.dominio.CatalogoImp;
import clienteescritoriopw.dominio.SucursalImp;
import clienteescritoriopw.dominio.UnidadImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Sucursal;
import clienteescritoriopw.pojo.TipoUnidad;
import clienteescritoriopw.pojo.Unidad;
import clienteescritoriopw.utilidad.Utilidades;
import java.net.URL;
import java.time.Year;
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
    @FXML private ComboBox<Sucursal> cbSucursal; 
    
    private Unidad unidadEdicion;
    private ObservableList<TipoUnidad> listaTipos;
    private ObservableList<Sucursal> listaSucursales;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTiposUnidad();
        cargarSucursales();
        configurarValidaciones(); // Configura todas las validaciones
        configurarListenersNII();
    }
    
    private void cargarTiposUnidad() {
        listaTipos = FXCollections.observableArrayList();
        List<TipoUnidad> lista = CatalogoImp.obtenerTiposUnidad();
        if(lista != null) listaTipos.addAll(lista);
        cbTipoUnidad.setItems(listaTipos);
        
        cbTipoUnidad.setConverter(new StringConverter<TipoUnidad>() {
            @Override
            public String toString(TipoUnidad t) { return (t != null) ? t.getNombre() : null; }
            @Override
            public TipoUnidad fromString(String string) { return null; }
        });
    }

    private void cargarSucursales() {
        listaSucursales = FXCollections.observableArrayList();
        List<Sucursal> lista = SucursalImp.obtenerSucursales();
        if(lista != null) listaSucursales.addAll(lista);
        cbSucursal.setItems(listaSucursales);
        
        cbSucursal.setConverter(new StringConverter<Sucursal>() {
            @Override
            public String toString(Sucursal s) { return (s != null) ? s.getNombre() : null; }
            @Override
            public Sucursal fromString(String string) { return null; }
        });
    }

    //CONFIGURACIÓN DE TODAS LAS VALIDACIONES
    private void configurarValidaciones() {
        // Validación para Marca: máximo 30 caracteres, solo alfanuméricos
        configurarTextFieldAlfanumerico(tfMarca, 30);
        
        // Validación para Modelo: máximo 30 caracteres, solo alfanuméricos  
        configurarTextFieldAlfanumerico(tfModelo, 30);
        
        // Validación para Año: solo números y máximo 4 caracteres
        tfAnio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 4) {
                tfAnio.setText(oldValue);
            }
        });
        tfAnio.setOnKeyTyped(event -> {
            if (!event.getCharacter().matches("[0-9]")) {
                event.consume();
            }
        });

        // Validación para VIN: máximo 17 caracteres, solo alfanuméricos
        configurarTextFieldAlfanumerico(tfVin, 17);
    }
    
    //MÉTODO AUXILIAR PARA VALIDACIONES COMUNES
    private void configurarTextFieldAlfanumerico(TextField field, int maxLength) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                field.setText(oldValue);
            }
        });
        field.setOnKeyTyped(event -> {
            if (!event.getCharacter().matches("[a-zA-Z0-9]")) {
                event.consume();
            }
        });
    }
    
    private void configurarListenersNII() {
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
        
        // Seleccionar Sucursal
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
            
            // Validación final del año
            String anioStr = tfAnio.getText().trim();
            int anioActual = Year.now().getValue();
            int anioMaximo = anioActual + 1;
            
            try {
                int anio = Integer.parseInt(anioStr);
                if (anio < 1900 || anio > anioMaximo) {
                    Utilidades.mostrarAlertaSimple("Año inválido", 
                        "El año debe estar entre 1900 y " + anioMaximo + ".", 
                        Alert.AlertType.WARNING);
                    return;
                }
                unidad.setAnio(anio);
            } catch (NumberFormatException e) {
                Utilidades.mostrarAlertaSimple("Año inválido", 
                    "El año debe ser un número válido de 4 dígitos.", 
                    Alert.AlertType.WARNING);
                return;
            }
            
            unidad.setVin(tfVin.getText().trim().toUpperCase());
            unidad.setNii(tfNII.getText().trim());
            unidad.setIdTipoUnidad(cbTipoUnidad.getValue().getIdTipoUnidad());
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
        StringBuilder msg = new StringBuilder();
        
        if(tfMarca.getText().isEmpty()) msg.append("- Marca\n");
        if(tfModelo.getText().isEmpty()) msg.append("- Modelo\n");
        if(tfAnio.getText().isEmpty()) msg.append("- Año\n");
        if(tfVin.getText().isEmpty()) msg.append("- VIN\n");
        if(cbTipoUnidad.getValue() == null) msg.append("- Tipo de Unidad\n");
        if(cbSucursal.getValue() == null) msg.append("- Sucursal\n");
        
        // Validaciones adicionales
        if (unidadEdicion == null || 
        !tfVin.getText().trim().equals(unidadEdicion.getVin())) {

        String vin = tfVin.getText().trim();
        Integer idExcluir = unidadEdicion != null ? unidadEdicion.getIdUnidad() : null;

        if (!vin.isEmpty() && UnidadImp.existeVinDuplicado(vin, idExcluir)) {
            msg.append("- Ya existe una unidad con este VIN\n");
        }
    }
        if(!tfMarca.getText().isEmpty()) {
            String marca = tfMarca.getText().trim();
            if (marca.length() > 30 || !marca.matches("[a-zA-Z0-9]+")) {
                msg.append("- Marca: máximo 30 caracteres, solo letras y números\n");
            }
        }
        
        if(!tfModelo.getText().isEmpty()) {
            String modelo = tfModelo.getText().trim();
            if (modelo.length() > 30 || !modelo.matches("[a-zA-Z0-9]+")) {
                msg.append("- Modelo: máximo 30 caracteres, solo letras y números\n");
            }
        }
        
        if(!tfAnio.getText().isEmpty()) {
            String anio = tfAnio.getText().trim();
            if (!anio.matches("\\d{4}")) {
                msg.append("- Año debe tener 4 dígitos\n");
            }
        }
        
        if(!tfVin.getText().isEmpty()) {
            String vin = tfVin.getText().trim();
            if (vin.length() != 17 || !vin.matches("[a-zA-Z0-9]{17}")) {
                msg.append("- VIN debe tener 17 caracteres alfanuméricos\n");
            }
        }
        
        if(msg.length() > 0){
            Utilidades.mostrarAlertaSimple("Campos inválidos", "Corrige:\n" + msg.toString(), Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
    
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
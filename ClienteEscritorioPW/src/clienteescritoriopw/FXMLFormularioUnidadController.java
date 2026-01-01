package clienteescritoriopw;

import clienteescritoriopw.dominio.CatalogoImp;
import clienteescritoriopw.dominio.UnidadImp;
import clienteescritoriopw.dto.Respuesta;
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
    @FXML private TextField tfNII; // Automático
    @FXML private ComboBox<TipoUnidad> cbTipoUnidad;
    
    private Unidad unidadEdicion;
    private ObservableList<TipoUnidad> listaTipos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTiposUnidad();
        configurarListenersNII();
    }
    
    private void cargarTiposUnidad() {
        listaTipos = FXCollections.observableArrayList();
        // Ahora sí funcionará porque agregamos el método en CatalogoImp
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

    private void configurarListenersNII() {
        tfAnio.textProperty().addListener((obs, viejo, nuevo) -> calcularNII());
        tfVin.textProperty().addListener((obs, viejo, nuevo) -> calcularNII());
    }
    
    private void calcularNII() {
        String anio = tfAnio.getText().trim();
        String vin = tfVin.getText().trim();
        
        if(!anio.isEmpty() && vin.length() >= 4){
            // NII = Año + 4 primeros caracteres del VIN
            String niiCalculado = anio + vin.substring(0, 4).toUpperCase();
            tfNII.setText(niiCalculado);
        } else {
            tfNII.setText("");
        }
    }
    
    public void inicializarEdicion(Unidad unidad) {
        this.unidadEdicion = unidad;
        lbTitulo.setText("Editar Unidad");
        
        tfMarca.setText(unidad.getMarca());
        tfModelo.setText(unidad.getModelo());
        
        // CORRECCIÓN: Convertir int a String para mostrarlo
        tfAnio.setText(String.valueOf(unidad.getAnio()));
        
        tfVin.setText(unidad.getVin());
        tfNII.setText(unidad.getNii());
        
        for(TipoUnidad t : cbTipoUnidad.getItems()){
            if(t.getIdTipoUnidad() == unidad.getIdTipoUnidad()){
                cbTipoUnidad.getSelectionModel().select(t);
                break;
            }
        }
        
        // El VIN no se debe editar según el PDF
        tfVin.setDisable(true); 
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if(validarCampos()){
            Unidad unidad = new Unidad();
            unidad.setMarca(tfMarca.getText().trim());
            unidad.setModelo(tfModelo.getText().trim());
            
            // CORRECCIÓN: Parsear el año de String a int
            try {
                unidad.setAnio(Integer.parseInt(tfAnio.getText().trim()));
            } catch (NumberFormatException e) {
                Utilidades.mostrarAlertaSimple("Error", "El año debe ser un número válido.", Alert.AlertType.ERROR);
                return;
            }

            unidad.setVin(tfVin.getText().trim());
            
            // CORRECCIÓN: Usar setNii en lugar de getNii
            unidad.setNii(tfNII.getText().trim());
            
            unidad.setIdTipoUnidad(cbTipoUnidad.getValue().getIdTipoUnidad());
            
            // Ojo: Asignamos Sucursal por defecto o nula si el form no la tiene
            // Según tu POJO Unidad del servidor, requiere idSucursal. 
            // Si el backend permite nulos, bien. Si no, asigna 0 o maneja la lógica.
            // unidad.setIdSucursal(1); // Ejemplo temporal
            
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
        
        if(!msg.isEmpty()){
            Utilidades.mostrarAlertaSimple("Campos vacíos", "Faltan:\n" + msg, Alert.AlertType.WARNING);
            return false;
        }
        // Validación extra para que el año sea número
        try {
            Integer.parseInt(tfAnio.getText().trim());
        } catch (NumberFormatException e) {
            Utilidades.mostrarAlertaSimple("Datos inválidos", "El año debe ser numérico.", Alert.AlertType.WARNING);
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
    
    @FXML
    private void clicCancelar(ActionEvent event) {
        ((Stage) tfMarca.getScene().getWindow()).close();
    }
    
}
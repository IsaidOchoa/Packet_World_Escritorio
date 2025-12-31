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
        // Asegúrate que CatalogoImp tenga obtenerTiposUnidad()
        List<TipoUnidad> lista = CatalogoImp.obtenerTiposUnidad();
        if(lista != null) listaTipos.addAll(lista);
        cbTipoUnidad.setItems(listaTipos);
        
        // Convertidor para mostrar solo el nombre en el combo
        cbTipoUnidad.setConverter(new StringConverter<TipoUnidad>() {
            @Override
            public String toString(TipoUnidad t) { return (t != null) ? t.getNombre() : null; }
            @Override
            public TipoUnidad fromString(String string) { return null; }
        });
    }

    // --- REGLA: CÁLCULO AUTOMÁTICO DEL NII ---
    private void configurarListenersNII() {
        // Escuchamos cambios en Año
        tfAnio.textProperty().addListener((obs, viejo, nuevo) -> calcularNII());
        // Escuchamos cambios en VIN
        tfVin.textProperty().addListener((obs, viejo, nuevo) -> calcularNII());
    }
    
    private void calcularNII() {
        String anio = tfAnio.getText().trim();
        String vin = tfVin.getText().trim();
        
        // Regla PDF: Año + primeros 4 del VIN
        if(!anio.isEmpty() && vin.length() >= 4){
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
        tfAnio.setText(unidad.getAnio());
        tfVin.setText(unidad.getVin());
        tfNII.setText(unidad.getNii());
        
        // Seleccionar Tipo
        for(TipoUnidad t : cbTipoUnidad.getItems()){
            if(t.getIdTipoUnidad() == unidad.getIdTipoUnidad()){
                cbTipoUnidad.getSelectionModel().select(t);
                break;
            }
        }
        
        // --- REGLA: VIN NO EDITABLE ---
        tfVin.setDisable(true); 
        // El NII sigue calculándose si cambian el año, pero el VIN ya no cambia.
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if(validarCampos()){
            Unidad unidad = new Unidad();
            unidad.setMarca(tfMarca.getText().trim());
            unidad.setModelo(tfModelo.getText().trim());
            unidad.setAnio(tfAnio.getText().trim());
            unidad.setVin(tfVin.getText().trim());
            unidad.getNii(tfNII.getText().trim());
            unidad.setIdTipoUnidad(cbTipoUnidad.getValue().getIdTipoUnidad());
            
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
    @FXML
    private void clicEliminar(ActionEvent event) {
        Unidad seleccionada = tvUnidades.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            
            // 1. Pedir el motivo usando un diálogo de texto (TextInputDialog)
            javafx.scene.control.TextInputDialog dialogo = new javafx.scene.control.TextInputDialog();
            dialogo.setTitle("Dar de Baja Unidad");
            dialogo.setHeaderText("¿Estás seguro de eliminar la unidad: " + seleccionada.getModelo() + "?");
            dialogo.setContentText("Motivo de la baja:");
            
            // Esperar a que el usuario escriba y acepte
            java.util.Optional<String> resultado = dialogo.showAndWait();
            
            if (resultado.isPresent()) {
                String motivo = resultado.get().trim();
                if(motivo.isEmpty()){
                     Utilidades.mostrarAlertaSimple("Error", "Debes ingresar un motivo para dar de baja.", Alert.AlertType.WARNING);
                     return;
                }
                
                // 2. Proceder a eliminar (Aquí podrías mandar el motivo al backend si tuvieras el campo, 
                //    pero por ahora cumplimos con pedirlo en la interfaz).
                Respuesta respuesta = UnidadImp.eliminar(seleccionada.getIdUnidad());
                
                if (!respuesta.isError()) {
                    Utilidades.mostrarAlertaSimple("Éxito", "Unidad dada de baja.", Alert.AlertType.INFORMATION);
                    cargarDatosTabla();
                } else {
                    Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
                }
            }
        } else {
            Utilidades.mostrarAlertaSimple("Atención", "Selecciona una unidad para eliminar.", Alert.AlertType.WARNING);
        }
    }
}
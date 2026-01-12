package clienteescritoriopw;

import clienteescritoriopw.dominio.PaqueteImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Paquete;
import clienteescritoriopw.utilidad.Utilidades;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class FXMLFormularioPaqueteController implements Initializable {

    @FXML private Label lbTitulo;
    @FXML private Label lbNumeroGuia;
    @FXML private TextArea taDescripcion;
    @FXML private TextField tfPeso;
    @FXML private TextField tfAlto;
    @FXML private TextField tfAncho;
    @FXML private TextField tfProfundidad;
    @FXML private javafx.scene.control.Button btnGuardar;

    private Paquete paqueteEdicion;
    private boolean esEdicion = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarValidaciones();
    }    

    private void configurarValidaciones() {
        // Validación para descripción: máximo 300 caracteres
        taDescripcion.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() <= 300) {
                return change;
            }
            return null; // Rechaza el cambio si excede el límite
        }));

        // Validación para campos numéricos: solo números y punto decimal (máx 6 caracteres)
        configurarValidacionNumerica(tfPeso);
        configurarValidacionNumerica(tfAlto);
        configurarValidacionNumerica(tfAncho);
        configurarValidacionNumerica(tfProfundidad);
    }

    private void configurarValidacionNumerica(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            // Permite solo dígitos y un solo punto decimal, con un máximo de 6 caracteres
            if (newText.length() <= 6 && newText.matches("[0-9.]*")) {
                long puntos = newText.chars().filter(ch -> ch == '.').count();
                if (puntos <= 1) {
                    return change;
                }
            }
            return null;
        }));
    }

    public void inicializarPaquete(Paquete paquete) {
        this.paqueteEdicion = paquete;
        this.esEdicion = true;
        this.idEnvioParaGuardar = paquete.getIdEnvio();

        // Llenar los campos con los datos del paquete
        taDescripcion.setText(paquete.getDescripcion());
        tfPeso.setText(String.valueOf(paquete.getPeso()));
        tfAlto.setText(String.valueOf(paquete.getAlto()));
        tfAncho.setText(String.valueOf(paquete.getAncho()));
        tfProfundidad.setText(String.valueOf(paquete.getProfundidad()));

        // Cambiar el título y el texto del botón
        lbTitulo.setText("Editar Detalles del Paquete");
        btnGuardar.setText("Actualizar Paquete");
    }
    
    private Integer idEnvioParaGuardar;
    // Variable para almacenar el ID
    public void setNumeroGuia(String numeroGuia) {
        this.lbNumeroGuia.setText(numeroGuia);
    }    

    @FXML
    private void clicCancelar(ActionEvent event) {
        ((Stage) lbTitulo.getScene().getWindow()).close();
    }

    @FXML
    private void clicGuardarPaquete(ActionEvent event) {
        if (validarCampos()) {
            try {
                Paquete paquete = esEdicion ? paqueteEdicion : new Paquete();
                paquete.setIdEnvio(this.idEnvioParaGuardar); 
                paquete.setDescripcion(taDescripcion.getText().trim());
                paquete.setPeso(Float.parseFloat(tfPeso.getText().trim()));
                paquete.setAlto(Float.parseFloat(tfAlto.getText().trim()));
                paquete.setAncho(Float.parseFloat(tfAncho.getText().trim()));
                paquete.setProfundidad(Float.parseFloat(tfProfundidad.getText().trim()));

                Respuesta resp;
                if (esEdicion) {
                    resp = PaqueteImp.editar(paquete); // <-- Usar el método de edición
                } else {
                    resp = PaqueteImp.registrar(paquete);
                }

                if (!resp.isError()) {
                    String mensaje = esEdicion ? "Paquete actualizado correctamente." : "Paquete registrado correctamente.";
                    Utilidades.mostrarAlertaSimple("Éxito", mensaje, Alert.AlertType.INFORMATION);
                    ((Stage) lbTitulo.getScene().getWindow()).close();
                } else {
                    Utilidades.mostrarAlertaSimple("Error", resp.getMensaje(), Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException e) {
                Utilidades.mostrarAlertaSimple("Error", "Datos inválidos en los campos numéricos.", Alert.AlertType.ERROR);
            }
        }
    }
    
    private boolean validarCampos() {
        if (taDescripcion.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Error", "La descripción es requerida.", Alert.AlertType.WARNING);
            return false;
        }
        if (tfPeso.getText().trim().isEmpty() || tfAlto.getText().trim().isEmpty() ||
            tfAncho.getText().trim().isEmpty() || tfProfundidad.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Error", "Todos los campos numéricos son requeridos.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
}
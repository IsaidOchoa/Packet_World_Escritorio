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
import javafx.stage.Stage;

public class FXMLFormularioPaqueteController implements Initializable {

    @FXML private Label lbTitulo;
    @FXML private TextField tfIdEnvio;
    @FXML private TextArea taDescripcion;
    @FXML private TextField tfPeso;
    @FXML private TextField tfAlto;
    @FXML private TextField tfAncho;
    @FXML private TextField tfProfundidad;
    @FXML private javafx.scene.control.Button btnGuardar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar validaciones si es necesario
    }    

    @FXML
    private void clicCancelar(ActionEvent event) {
        ((Stage) lbTitulo.getScene().getWindow()).close();
    }

    @FXML
    private void clicGuardarPaquete(ActionEvent event) {
        if (validarCampos()) {
            try {
                Paquete paquete = new Paquete();
                paquete.setIdEnvio(Integer.parseInt(tfIdEnvio.getText().trim()));
                paquete.setDescripcion(taDescripcion.getText().trim());
                paquete.setPeso(Float.parseFloat(tfPeso.getText().trim()));
                paquete.setAlto(Float.parseFloat(tfAlto.getText().trim()));
                paquete.setAncho(Float.parseFloat(tfAncho.getText().trim()));
                paquete.setProfundidad(Float.parseFloat(tfProfundidad.getText().trim()));

                Respuesta resp = PaqueteImp.registrar(paquete);
                if (!resp.isError()) {
                    Utilidades.mostrarAlertaSimple("Éxito", "Paquete registrado correctamente.", Alert.AlertType.INFORMATION);
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
        if (tfIdEnvio.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Error", "El ID de envío es requerido.", Alert.AlertType.WARNING);
            return false;
        }
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
package clienteescritoriopw;

import clienteescritoriopw.dominio.ColaboradorImp;
import clienteescritoriopw.dominio.UnidadImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Colaborador;
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
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class FXMLAsignarConductorController implements Initializable {

    @FXML private Label lbUnidad;
    @FXML private ComboBox<Colaborador> cbConductores;

    private Unidad unidadSeleccionada;
    private ObservableList<Colaborador> listaConductores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarConductores();
    }

    public void inicializarDatos(Unidad unidad) {
        this.unidadSeleccionada = unidad;
        if (unidad != null) {
            lbUnidad.setText("Unidad: " + unidad.getMarca() + " " + unidad.getModelo() + 
                             " (" + unidad.getAnio() + ")");
            
            if (unidad.getIdColaborador() != null && unidad.getIdColaborador() > 0) {
                for (Colaborador c : cbConductores.getItems()) {
                    if (c.getIdColaborador() == unidad.getIdColaborador()) {
                        cbConductores.getSelectionModel().select(c);
                        break;
                    }
                }
            }
        }
    }

    private void cargarConductores() {
        listaConductores = FXCollections.observableArrayList();
        List<Colaborador> todos = ColaboradorImp.obtenerColaboradores();
        
        if (todos != null) {
            for (Colaborador c : todos) {
                if (c.getIdRol() == 3 || c.getRol().toLowerCase().contains("conductor")) {
                    listaConductores.add(c);
                }
            }
        }        
        cbConductores.setItems(listaConductores);
        cbConductores.setConverter(new StringConverter<Colaborador>() {
            @Override
            public String toString(Colaborador c) {
                return (c != null) ? c.getNombre() + " " + c.getApellidoPaterno() : null;
            }
            @Override
            public Colaborador fromString(String string) { return null; }
        });
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        Colaborador chofer = cbConductores.getValue();
        if (chofer != null) {
            enviarAsignacion(chofer.getIdColaborador());
        } else {
            Utilidades.mostrarAlertaSimple("Selección requerida", "Selecciona un conductor de la lista.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicDesasignar(ActionEvent event) {
        boolean confirmar = Utilidades.mostrarAlertaConfirmacion("Quitar Conductor", 
                "¿Deseas dejar esta unidad sin conductor asignado?");
        if (confirmar) {
            enviarAsignacion(0); 
        }
    }
    
    private void enviarAsignacion(int idColaborador) {
        Respuesta respuesta = UnidadImp.asignarConductor(unidadSeleccionada.getIdUnidad(), idColaborador);
        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Éxito", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            cerrarVentana();
        } else {
            Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        ((Stage) lbUnidad.getScene().getWindow()).close();
    }
}
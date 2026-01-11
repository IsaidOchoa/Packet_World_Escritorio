package clienteescritoriopw;

import clienteescritoriopw.dominio.EnvioImp;
import clienteescritoriopw.dominio.PaqueteImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Envio;
import clienteescritoriopw.pojo.Paquete;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class FXMLDetalleEnvioController implements Initializable {

    @FXML private Label lbGuia;
    @FXML private Label lbClienteDestino;
    @FXML private Label lbDestino;
    @FXML private Label lbCostoTotal;
    @FXML private Label lbEstatus; // Nueva etiqueta para estatus
    
    // Formulario Paquete
    @FXML private TextField tfDescripcion;
    @FXML private TextField tfPeso;
    @FXML private TextField tfAlto;
    @FXML private TextField tfAncho;
    @FXML private TextField tfProfundidad;
    
    // Tabla y botones
    @FXML private TableView<Paquete> tvPaquetes;
    @FXML private TableColumn<Paquete, String> colDescripcion;
    @FXML private TableColumn<Paquete, Float> colPeso;
    @FXML private TableColumn<Paquete, String> colDimensiones;
    
    @FXML private Button btnAgregarPaquete;
    @FXML private Button btnEliminarPaquete;

    private Envio envioSeleccionado;
    private ObservableList<Paquete> listaPaquetes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarValidaciones();
    }
    
    private void configurarTabla() {
        listaPaquetes = FXCollections.observableArrayList();
        
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        colDimensiones.setCellValueFactory(new PropertyValueFactory<>("dimensiones"));
        
        tvPaquetes.setItems(listaPaquetes);
        tvPaquetes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    private void configurarValidaciones() {
        // Validación para descripción: letras, números, espacios y paréntesis (máx 50 caracteres)
        tfDescripcion.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() <= 50 && newText.matches("[a-zA-Z0-9()\\s]*")) {
                return change;
            }
            return null;
        }));
        
        // Validación para campos numéricos: solo números y punto decimal (máx 6 caracteres)
        configurarValidacionNumerica(tfPeso);
        configurarValidacionNumerica(tfAlto);
        configurarValidacionNumerica(tfAncho);
        configurarValidacionNumerica(tfProfundidad);
    }
    
    private void configurarValidacionNumerica(TextField field) {
        field.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            if (newText.length() <= 6 && newText.matches("[0-9.]*")) {
                long puntos = newText.chars().filter(ch -> ch == '.').count();
                if (puntos <= 1) {
                    return change;
                }
            }
            return null;
        }));
    }

    public void inicializarEnvio(Envio envio) {
        this.envioSeleccionado = envio;
        if(envio != null){
            cargarDatosGenerales();
            cargarPaquetes();
            aplicarRestriccionesPorEstatus();
        }
    }

    private void cargarDatosGenerales() {
        lbGuia.setText(envioSeleccionado.getNumeroGuia());
        lbClienteDestino.setText(envioSeleccionado.getNombreCliente() + " -> " + envioSeleccionado.getNombreDestinatario());
        lbDestino.setText(envioSeleccionado.getCalleDestino() + ", " + envioSeleccionado.getNumeroDestino() + " (CP: " + envioSeleccionado.getCodigoPostalDestino() + ")");
        lbCostoTotal.setText("$ " + String.format("%.2f", envioSeleccionado.getCosto()));
        
        // Mostrar estatus
        lbEstatus.setText(envioSeleccionado.getEstatus());
        
        // Colorear según estatus
        switch (envioSeleccionado.getIdEstadoActual()) {
            case 1: // En tránsito
                lbEstatus.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                break;
            case 2: // Detenido
                lbEstatus.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                break;
            case 3: // Entregado
                lbEstatus.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                break;
            case 4: // Cancelado
                lbEstatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                break;
            default:
                lbEstatus.setStyle("-fx-text-fill: #666666;");
        }
    }

    private void cargarPaquetes() {
        if(envioSeleccionado != null){
            List<Paquete> paquetes = PaqueteImp.obtenerPaquetesPorEnvio(envioSeleccionado.getIdEnvio());
            listaPaquetes.clear();
            if(paquetes != null){
                listaPaquetes.addAll(paquetes);
            }
            tvPaquetes.setItems(listaPaquetes);
        }
    }
    
    private void aplicarRestriccionesPorEstatus() {
        int idEstatus = envioSeleccionado.getIdEstadoActual();
        boolean esEditable = (idEstatus == 1 || idEstatus == 2); // En tránsito o detenido
        
        // Deshabilitar formulario de paquetes si no es editable
        tfDescripcion.setDisable(!esEditable);
        tfPeso.setDisable(!esEditable);
        tfAlto.setDisable(!esEditable);
        tfAncho.setDisable(!esEditable);
        tfProfundidad.setDisable(!esEditable);
        
        btnAgregarPaquete.setDisable(!esEditable);
        btnEliminarPaquete.setDisable(!esEditable);
    }
    
    private void actualizarCostoYTabla() {
        new Thread(() -> {
            try {
                // 1. Obtener solo el envío actualizado (para el costo)
                Envio envioActualizado = EnvioImp.obtenerPorId(envioSeleccionado.getIdEnvio());
                if (envioActualizado != null) {
                    javafx.application.Platform.runLater(() -> {
                        // Actualizar SOLO el costo (el estatus no cambia aquí)
                        envioSeleccionado.setCosto(envioActualizado.getCosto());
                        lbCostoTotal.setText("$ " + String.format("%.2f", envioActualizado.getCosto()));
                    });
                }

                // 2. Obtener solo los paquetes actualizados
                List<Paquete> paquetesActualizados = PaqueteImp.obtenerPaquetesPorEnvio(envioSeleccionado.getIdEnvio());
                javafx.application.Platform.runLater(() -> {
                    listaPaquetes.clear();
                    if (paquetesActualizados != null) {
                        listaPaquetes.addAll(paquetesActualizados);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void clicAgregarPaquete(ActionEvent event) {
        if(validarCamposPaquete()){
            try {
                float peso = Float.parseFloat(tfPeso.getText().trim());
                float alto = Float.parseFloat(tfAlto.getText().trim());
                float ancho = Float.parseFloat(tfAncho.getText().trim());
                float profundidad = Float.parseFloat(tfProfundidad.getText().trim());
                
                if (peso <= 0 || alto <= 0 || ancho <= 0 || profundidad <= 0) {
                    Utilidades.mostrarAlertaSimple("Valores inválidos", "Todos los valores deben ser mayores a 0.", Alert.AlertType.WARNING);
                    return;
                }
                
                Paquete p = new Paquete();
                p.setIdEnvio(envioSeleccionado.getIdEnvio());
                p.setDescripcion(tfDescripcion.getText().trim());
                p.setPeso(peso);
                p.setAlto(alto);
                p.setAncho(ancho);
                p.setProfundidad(profundidad);
                
                new Thread(() -> {
                    try {
                        Respuesta resp = PaqueteImp.agregarPaquete(p);
                        
                        javafx.application.Platform.runLater(() -> {
                            if(!resp.isError()){
                                limpiarFormularioPaquete();
                                actualizarCostoYTabla();
                                Utilidades.mostrarAlertaSimple("Éxito", "Paquete agregado y costo actualizado.", Alert.AlertType.INFORMATION);
                            } else {
                                Utilidades.mostrarAlertaSimple("Error", resp.getMensaje(), Alert.AlertType.ERROR);
                            }
                        });
                        
                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() -> {
                            Utilidades.mostrarAlertaSimple("Error", "Error al procesar el paquete.", Alert.AlertType.ERROR);
                        });
                    }
                }).start();
                
            } catch(NumberFormatException ex){
                Utilidades.mostrarAlertaSimple("Datos inválidos", "Peso y dimensiones deben ser números válidos.", Alert.AlertType.WARNING);
            }
        }
    }
    
    @FXML
    private void clicEliminarPaquete(ActionEvent event) {
        Paquete paqueteSeleccionado = tvPaquetes.getSelectionModel().getSelectedItem();
        
        if (paqueteSeleccionado == null) {
            Utilidades.mostrarAlertaSimple("Selección requerida", 
                "Debe seleccionar un paquete de la tabla para eliminarlo.", 
                Alert.AlertType.WARNING);
            return;
        }
        
        // Verificar restricciones por estatus
        if (!btnEliminarPaquete.isDisable()) {
            boolean confirmar = Utilidades.mostrarAlertaConfirmacion(
                "Eliminar Paquete", 
                "¿Está seguro de eliminar el paquete '" + paqueteSeleccionado.getDescripcion() + "'?\n\n" +
                "Esta acción actualizará el costo del envío."
            );
            
            if (confirmar) {
                new Thread(() -> {
                    try {
                        Respuesta resp = PaqueteImp.eliminar(paqueteSeleccionado.getIdPaquete());
                        
                        javafx.application.Platform.runLater(() -> {
                            if(!resp.isError()){
                                actualizarCostoYTabla();
                                Utilidades.mostrarAlertaSimple("Éxito", "Paquete eliminado y costo actualizado.", Alert.AlertType.INFORMATION);
                            } else {
                                Utilidades.mostrarAlertaSimple("Error", resp.getMensaje(), Alert.AlertType.ERROR);
                            }
                        });
                        
                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() -> {
                            Utilidades.mostrarAlertaSimple("Error", "Error al eliminar el paquete.", Alert.AlertType.ERROR);
                        });
                    }
                }).start();
            }
        } else {
            // Intentó eliminar en estado no permitido
            Utilidades.mostrarAlertaSimple("Acción no permitida", 
                "No se pueden modificar paquetes en envíos con estatus '" + 
                envioSeleccionado.getEstatus() + "'.", 
                Alert.AlertType.WARNING);
        }
    }
    
    private void recargarDatosCompletos() {
        new Thread(() -> {
            try {
                // Usar el mismo método que se usa en FXMLEnvioController para obtener TODOS los datos
                Envio envioActualizado = EnvioImp.buscarPorGuia(envioSeleccionado.getNumeroGuia());
                if (envioActualizado != null) {
                    javafx.application.Platform.runLater(() -> {
                        this.envioSeleccionado = envioActualizado;
                        cargarDatosGenerales();
                        cargarPaquetes();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean validarCamposPaquete() {
        if(tfDescripcion.getText().trim().isEmpty() || 
           tfPeso.getText().trim().isEmpty() || 
           tfAlto.getText().trim().isEmpty() || 
           tfAncho.getText().trim().isEmpty() || 
           tfProfundidad.getText().trim().isEmpty()){
            Utilidades.mostrarAlertaSimple("Campos vacíos", "Llena todos los datos del paquete.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
    
    private void limpiarFormularioPaquete(){
        tfDescripcion.clear();
        tfPeso.clear();
        tfAlto.clear();
        tfAncho.clear();
        tfProfundidad.clear();
    }
    
    @FXML
    private void clicRegresar(ActionEvent event) {
        ((Stage) lbGuia.getScene().getWindow()).close();
    }
}
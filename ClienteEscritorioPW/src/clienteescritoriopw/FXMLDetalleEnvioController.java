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
import javafx.scene.control.Label;
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
    
    // Formulario Paquete
    @FXML private TextField tfDescripcion;
    @FXML private TextField tfPeso;
    @FXML private TextField tfAlto;
    @FXML private TextField tfAncho;
    @FXML private TextField tfProfundidad;
    
    // Tabla
    @FXML private TableView<Paquete> tvPaquetes;
    @FXML private TableColumn<Paquete, String> colDescripcion;
    @FXML private TableColumn<Paquete, Float> colPeso;
    @FXML private TableColumn<Paquete, String> colDimensiones; 

    private Envio envioSeleccionado;
    private ObservableList<Paquete> listaPaquetes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }
    
    private void configurarTabla() {
        listaPaquetes = FXCollections.observableArrayList();
        
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        colDimensiones.setCellValueFactory(new PropertyValueFactory<>("dimensiones"));      
    }

    public void inicializarEnvio(Envio envio) {
        this.envioSeleccionado = envio;
        if(envio != null){
            cargarDatosGenerales();
            cargarPaquetes();
        }
    }

    private void cargarDatosGenerales() {
        lbGuia.setText(envioSeleccionado.getNumeroGuia());
        lbClienteDestino.setText(envioSeleccionado.getNombreCliente() + " -> " + envioSeleccionado.getNombreDestinatario());
        lbDestino.setText(envioSeleccionado.getCalleDestino() + ", " + envioSeleccionado.getNumeroDestino() + " (CP: " + envioSeleccionado.getCodigoPostalDestino() + ")");
        lbCostoTotal.setText("$ " + String.format("%.2f", envioSeleccionado.getCosto()));
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
                
                // Ejecutar en segundo plano para evitar congelamiento
                new Thread(() -> {
                    try {
                        // USAR EL NUEVO MÉTODO QUE ACTUALIZA EL COSTO
                        Respuesta resp = PaqueteImp.agregarPaquete(p);
                        
                        javafx.application.Platform.runLater(() -> {
                            if(!resp.isError()){
                                limpiarFormularioPaquete();
                                // Recargar datos completos del envío
                                recargarDatosCompletos();
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
    
    private void recargarDatosCompletos() {
        new Thread(() -> {
            try {
                Envio envioActualizado = EnvioImp.obtenerPorId(envioSeleccionado.getIdEnvio());
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
    private void clicGuardarCostoTotal(ActionEvent event) {
        // El botón guardar solo cierra la ventana (los paquetes ya se guardan al agregarlos)
        Utilidades.mostrarAlertaSimple("Información", "Los paquetes ya se guardaron automáticamente al agregarlos.", Alert.AlertType.INFORMATION);
        clicRegresar(event);
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        ((Stage) lbGuia.getScene().getWindow()).close();
    }
}
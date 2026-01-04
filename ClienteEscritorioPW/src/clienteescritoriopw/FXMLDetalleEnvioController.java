package clienteescritoriopw;

import clienteescritoriopw.dominio.EnvioImp;
import clienteescritoriopw.dominio.PaqueteImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Envio;
import clienteescritoriopw.pojo.Paquete;
import clienteescritoriopw.utilidad.Utilidades;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
    @FXML private TableColumn colDescripcion;
    @FXML private TableColumn colPeso;
    @FXML private TableColumn colDimensiones; 

    private Envio envioSeleccionado;
    private ObservableList<Paquete> listaPaquetes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }
    
    
    private void configurarTabla() {
        listaPaquetes = FXCollections.observableArrayList();
        
       
        colDescripcion.setCellValueFactory(new PropertyValueFactory("descripcion"));
       
        colPeso.setCellValueFactory(new PropertyValueFactory("peso"));
       
        colDimensiones.setCellValueFactory(new PropertyValueFactory("dimensiones"));      
    }

    public void inicializarEnvio(Envio envio) {
        this.envioSeleccionado = envio;
        if(envio != null){
            cargarDatosGenerales();
            cargarPaquetes();
            
            recalcularCostoLocalmente();
        }
    }

    private void cargarDatosGenerales() {
        lbGuia.setText(envioSeleccionado.getNumeroGuia());
        lbClienteDestino.setText(envioSeleccionado.getNombreCliente() + " -> " + envioSeleccionado.getNombreDestinatario());
        lbDestino.setText(envioSeleccionado.getCalleDestino() + ", " + envioSeleccionado.getNumeroDestino() + " (CP: " + envioSeleccionado.getCodigoPostalDestino() + ")");
        lbCostoTotal.setText("$ " + envioSeleccionado.getCosto());
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
            Paquete p = new Paquete();
            p.setIdEnvio(envioSeleccionado.getIdEnvio());
            p.setDescripcion(tfDescripcion.getText());
            
            try {
              
                p.setPeso(Float.parseFloat(tfPeso.getText()));
                p.setAlto(Float.parseFloat(tfAlto.getText()));
                p.setAncho(Float.parseFloat(tfAncho.getText()));
                p.setProfundidad(Float.parseFloat(tfProfundidad.getText()));
                
              
                Respuesta resp = PaqueteImp.registrar(p);
                
                if(!resp.isError()){
                    Utilidades.mostrarAlertaSimple("Éxito", "Paquete agregado.", Alert.AlertType.INFORMATION);
                    limpiarFormularioPaquete();
                    
                 
                    cargarPaquetes();
                    
                  
                    recalcularCostoLocalmente();
                    
                } else {
                    Utilidades.mostrarAlertaSimple("Error", resp.getMensaje(), Alert.AlertType.ERROR);
                }
                
            } catch(NumberFormatException ex){
                Utilidades.mostrarAlertaSimple("Datos inválidos", "Peso y dimensiones deben ser números.", Alert.AlertType.WARNING);
            }
        }
    }
    
    
    private void recalcularCostoLocalmente() {
        if (listaPaquetes != null) {
            double costoTotalCalculado = 0.0;
            for (Paquete p : listaPaquetes) {
                costoTotalCalculado += (p.getPeso() * 10); 
            }
            lbCostoTotal.setText("$ " + String.format("%.2f", costoTotalCalculado));
            envioSeleccionado.setCosto(costoTotalCalculado);
        }
    }

    private boolean validarCamposPaquete() {
        if(tfDescripcion.getText().isEmpty() || tfPeso.getText().isEmpty() || 
           tfAlto.getText().isEmpty() || tfAncho.getText().isEmpty() || tfProfundidad.getText().isEmpty()){
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
        
    }

    @FXML
    private void clicRegresar(ActionEvent event) {
        ((Stage) lbGuia.getScene().getWindow()).close();
    }

    }
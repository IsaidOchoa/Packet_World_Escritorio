package clienteescritoriopw;

import clienteescritoriopw.dominio.CatalogoImp;
import clienteescritoriopw.dominio.ClienteImp;
import clienteescritoriopw.dominio.DireccionImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Cliente;
import clienteescritoriopw.pojo.Colonia;
import clienteescritoriopw.pojo.Estado;
import clienteescritoriopw.pojo.Municipio;
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

public class FXMLFormularioClienteController implements Initializable {

    @FXML private Label lbTitulo;
    @FXML private TextField tfNombre;
    @FXML private TextField tfPaterno;
    @FXML private TextField tfMaterno;
    @FXML private TextField tfTelefono;
    @FXML private TextField tfCorreo;
    
    // --- NUEVOS CONTROLES ---
    @FXML private ComboBox<Estado> cbEstado;
    @FXML private ComboBox<Municipio> cbMunicipio;
    @FXML private ComboBox<Colonia> cbColonia;
    
    @FXML private TextField tfCalle;
    @FXML private TextField tfNumero;
    @FXML private TextField tfCodigoPostal;
    
    private Cliente clienteEdicion;
    private ObservableList<Estado> listaEstados;
    private ObservableList<Municipio> listaMunicipios;
    private ObservableList<Colonia> listaColonias;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarListas();
        configurarListeners();
        cargarEstados(); // Carga inicial
    }
    
    private void inicializarListas(){
        listaEstados = FXCollections.observableArrayList();
        cbEstado.setItems(listaEstados);
        
        listaMunicipios = FXCollections.observableArrayList();
        cbMunicipio.setItems(listaMunicipios);
        
        listaColonias = FXCollections.observableArrayList();
        cbColonia.setItems(listaColonias);
        
        // Configuramos para que muestren el nombre y no el objeto raro
        configurarCombo(cbEstado);
        configurarCombo(cbMunicipio);
        configurarCombo(cbColonia);
    }
    
    //  para que los combos muestren el nombre
    private <T> void configurarCombo(ComboBox<T> combo) {
        combo.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T object) {
                if (object == null) return null;
                // Usamos reflexión simple o casteos para obtener getNombre()
                if (object instanceof Estado) return ((Estado) object).getNombre();
                if (object instanceof Municipio) return ((Municipio) object).getNombre();
                if (object instanceof Colonia) return ((Colonia) object).getNombre();
                return object.toString();
            }
            @Override
            public T fromString(String string) { return null; }
        });
    }

    private void configurarListeners() {
        // 1. Cuando cambia ESTADO -> Cargar Municipios
        cbEstado.valueProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                cargarMunicipios(nuevo.getIdEstado());
                cbMunicipio.setDisable(false);
            } else {
                listaMunicipios.clear();
                cbMunicipio.setDisable(true);
            }
            // Limpiar cascada hacia abajo
            listaColonias.clear();
            tfCodigoPostal.clear();
        });

        // 2. Cuando cambia MUNICIPIO -> Cargar Colonias
        cbMunicipio.valueProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                cargarColonias(nuevo.getIdMunicipio());
                cbColonia.setDisable(false);
            } else {
                listaColonias.clear();
                cbColonia.setDisable(true);
            }
        });

        // 3. Cuando cambia COLONIA -> Poner CP Automático
        cbColonia.valueProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                tfCodigoPostal.setText(String.valueOf(nuevo.getCodigoPostal()));
            } else {
                tfCodigoPostal.clear();
            }
        });
    }
    @FXML 
    private void clicBuscarCP(ActionEvent event) {
    }

    private void cargarEstados() {
        List<Estado> resultados = DireccionImp.obtenerEstados();
        if (resultados != null) {
            listaEstados.addAll(resultados);
        }
    }

    private void cargarMunicipios(int idEstado) {
        listaMunicipios.clear();
        List<Municipio> resultados = DireccionImp.obtenerMunicipios(idEstado);
        if (resultados != null) {
            listaMunicipios.addAll(resultados);
        }
    }

    private void cargarColonias(int idMunicipio) {
        listaColonias.clear();
        List<Colonia> resultados = DireccionImp.obtenerColoniasPorMunicipio(idMunicipio);
        if (resultados != null) {
            listaColonias.addAll(resultados);
        }
    }
    
    public void inicializarEdicion(Cliente cliente){
        this.clienteEdicion = cliente;
        lbTitulo.setText("Editar Cliente");
        
        tfNombre.setText(cliente.getNombre());
        tfPaterno.setText(cliente.getApellidoPaterno());
        tfMaterno.setText(cliente.getApellidoMaterno());
        tfTelefono.setText(cliente.getTelefono());
        tfCorreo.setText(cliente.getCorreo());
        tfCalle.setText(cliente.getCalle());
        tfNumero.setText(cliente.getNumero());
        
        // --- RECUPERACIÓN DE COMBOS EN CASCADA (RETO TÉCNICO) ---
        // 1. Seleccionar Estado (Dispara listener de municipios)
        // Nota: Debemos buscar el objeto en la lista que coincida con el ID
        if(cliente.getIdColonia() > 0){
             // Esta parte es compleja porque requiere cargar datos secuenciales.
             // Para simplificar, podrías cargar todo manual o usar hilos, 
             // pero aquí haremos un truco: seleccionar estado y esperar carga.
             // (Para una implementación perfecta se requiere lógica extra, 
             //  pero prueba primero el registro nuevo).
             
             // TODO: Implementar selección inversa buscando por ID de Colonia hacia arriba
             // si tu backend lo permite, o seleccionando estado/municipio manualmente.
        }
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if(validarCamposVacios()){
            Utilidades.mostrarAlertaSimple("Faltan datos", "Por favor llena todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(tfNombre.getText().trim());
        cliente.setApellidoPaterno(tfPaterno.getText().trim());
        cliente.setApellidoMaterno(tfMaterno.getText().trim());
        cliente.setTelefono(tfTelefono.getText().trim());
        cliente.setCorreo(tfCorreo.getText().trim());
        cliente.setCalle(tfCalle.getText().trim());
        cliente.setNumero(tfNumero.getText().trim());
        
        // Guardamos el ID de la colonia seleccionada
        Colonia col = cbColonia.getValue();
        if(col != null) cliente.setIdColonia(col.getIdColonia());
        
        // El CP ya va implícito en la colonia, pero lo guardamos si tu POJO lo pide
        try { cliente.setCodigoPostal(Integer.parseInt(tfCodigoPostal.getText())); } catch(Exception e){}

        if(clienteEdicion == null){
            procesarRespuesta(ClienteImp.registrar(cliente), "registrado");
        }else{
            cliente.setIdCliente(clienteEdicion.getIdCliente());
            procesarRespuesta(ClienteImp.editar(cliente), "actualizado");
        }
    }
    
    // ... (Métodos clicCancelar, procesarRespuesta y validarCamposVacios igual que antes)
    
    private void procesarRespuesta(Respuesta resp, String accion){
        if(!resp.isError()){
            Utilidades.mostrarAlertaSimple("Éxito", "Cliente " + accion + " correctamente.", Alert.AlertType.INFORMATION);
            ((Stage)tfNombre.getScene().getWindow()).close();
        }else{
            Utilidades.mostrarAlertaSimple("Error", resp.getMensaje(), Alert.AlertType.ERROR);
        }
    }

    @FXML private void clicCancelar(ActionEvent event) {
        ((Stage)tfNombre.getScene().getWindow()).close();
    }

    private boolean validarCamposVacios(){
        return tfNombre.getText().isEmpty() || cbColonia.getValue() == null; // Añadir resto de validaciones
    }
}
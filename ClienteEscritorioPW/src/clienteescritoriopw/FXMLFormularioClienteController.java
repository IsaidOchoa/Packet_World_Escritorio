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
        // Cuando cambia ESTADO -> Cargar Municipios
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

        // Cuando cambia MUNICIPIO -> Cargar Colonias
        cbMunicipio.valueProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                cargarColonias(nuevo.getIdMunicipio());
                cbColonia.setDisable(false);
            } else {
                listaColonias.clear();
                cbColonia.setDisable(true);
            }
        });

        // Cuando cambia COLONIA -> Poner CP Automático
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
       
        String cp = tfCodigoPostal.getText().trim();
        if(cp.isEmpty() || cp.length() != 5){
            Utilidades.mostrarAlertaSimple("CP Inválido", "Ingresa un CP de 5 dígitos.", Alert.AlertType.WARNING);
            return;
        }

        List<Colonia> coloniasEncontradas = DireccionImp.buscarPorCP(cp);        
        if(coloniasEncontradas != null && !coloniasEncontradas.isEmpty()){
            // Tomamos la primera colonia para saber Estado y Municipio
            Colonia primerResultado = coloniasEncontradas.get(0);
            
            // Verificamos que traiga el ID del Estado (gracias a tu Mapper)
            if(primerResultado.getIdEstado() != null && primerResultado.getIdEstado() > 0){
                // Buscamos en el combo el estado que coincida con el ID
                for(Estado e : cbEstado.getItems()){
                    if(e.getIdEstado().equals(primerResultado.getIdEstado())){
                        cbEstado.getSelectionModel().select(e);
                        break;
                    }
                }
                // Al seleccionar Estado, se cargan los municipios. Buscamos el correcto.
                for(Municipio m : cbMunicipio.getItems()){
                    if(m.getIdMunicipio().equals(primerResultado.getIdMunicipio())){
                        cbMunicipio.getSelectionModel().select(m);
                        break;
                    }
                }
                //  Llenar Combo de Colonias solo con las del CP
                cbColonia.getItems().clear();
                cbColonia.getItems().addAll(coloniasEncontradas);
                // Seleccionar la primera por defecto
                cbColonia.getSelectionModel().select(0);
            }
        } else {
            Utilidades.mostrarAlertaSimple("Sin resultados", "No hay colonias para el CP: " + cp, Alert.AlertType.INFORMATION);
            cbColonia.getItems().clear();
        }
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
        
        
       if(cliente.getCodigoPostal() > 0){
             // 1. Ponemos el CP en el campo
             tfCodigoPostal.setText(String.valueOf(cliente.getCodigoPostal()));
             
             // 2. Ejecutamos la búsqueda de CP automáticamente
             // Esto disparará la carga de Estados, Municipios y Colonias
             clicBuscarCP(null); 
             
             // 3. Seleccionamos la Colonia específica del cliente
             // (clicBuscarCP selecciona la primera por defecto, aquí la corregimos)
             if(cliente.getIdColonia() > 0){
                 // Usamos un pequeño retraso visual o seleccionamos directo si ya cargó
                 for(Colonia c : cbColonia.getItems()){
                     // Comparamos por ID
                     if(c.getIdColonia().equals(cliente.getIdColonia())){
                         cbColonia.getSelectionModel().select(c);
                         break;
                     }
                 }
             }
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
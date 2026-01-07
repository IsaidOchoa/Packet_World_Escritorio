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
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class FXMLFormularioClienteController implements Initializable {

    @FXML private Label lbTitulo;
    @FXML private TextField tfNombre;
    @FXML private TextField tfPaterno;
    @FXML private TextField tfMaterno;
    @FXML private TextField tfTelefono;
    @FXML private TextField tfCorreo;
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
    
    // Patrón para validar correo electrónico
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarListas();
        configurarListeners();
        configurarValidaciones(); // 👈 NUEVO: Configurar validaciones
        cargarEstados();
    }
    
    private void inicializarListas(){
        listaEstados = FXCollections.observableArrayList();
        cbEstado.setItems(listaEstados);
        
        listaMunicipios = FXCollections.observableArrayList();
        cbMunicipio.setItems(listaMunicipios);
        
        listaColonias = FXCollections.observableArrayList();
        cbColonia.setItems(listaColonias);
        
        configurarCombo(cbEstado);
        configurarCombo(cbMunicipio);
        configurarCombo(cbColonia);
    }
    
    private <T> void configurarCombo(ComboBox<T> combo) {
        combo.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T object) {
                if (object == null) return null;
                if (object instanceof Estado) return ((Estado) object).getNombre();
                if (object instanceof Municipio) return ((Municipio) object).getNombre();
                if (object instanceof Colonia) return ((Colonia) object).getNombre();
                return object.toString();
            }
            @Override
            public T fromString(String string) { return null; }
        });
    }

    // 👇 NUEVO: Configuración de validaciones en tiempo real
    private void configurarValidaciones() {
        // Nombre, Apellidos: solo letras, máximo 30
        configurarTextFieldLetras(tfNombre, 30);
        configurarTextFieldLetras(tfPaterno, 30);
        configurarTextFieldLetras(tfMaterno, 30);
        
        // Teléfono: solo números, máximo 10
        configurarTextFieldConCaracteres(tfTelefono, 10, "[0-9]");
        
        // Correo: máximo 30 caracteres (validación de formato al guardar)
        configurarTextFieldLongitudMaxima(tfCorreo, 30);
        
        // Calle: alfanuméricos + espacios + acentos, máximo 50
        configurarTextFieldConCaracteres(tfCalle, 50, "[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]");
        
        // Número: alfanuméricos + guión, máximo 10
        configurarTextFieldConCaracteres(tfNumero, 10, "[a-zA-Z0-9-]");
        
        // Código Postal: solo números, máximo 5
        configurarTextFieldConCaracteres(tfCodigoPostal, 5, "[0-9]");
    }
    
    // 👇 MÉTODOS AUXILIARES PARA VALIDACIONES
    private void configurarTextFieldLetras(TextField field, int maxLength) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                field.setText(oldValue);
            }
        });
        field.setOnKeyTyped(event -> {
            if (!event.getCharacter().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]")) {
                event.consume();
            }
        });
    }
    
    private void configurarTextFieldConCaracteres(TextField field, int maxLength, String regex) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                field.setText(oldValue);
            }
        });
        field.setOnKeyTyped(event -> {
            if (!event.getCharacter().matches(regex)) {
                event.consume();
            }
        });
    }
    
    private void configurarTextFieldLongitudMaxima(TextField field, int maxLength) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                field.setText(oldValue);
            }
        });
    }

    private void configurarListeners() {
        cbEstado.valueProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                cargarMunicipios(nuevo.getIdEstado());
                cbMunicipio.setDisable(false);
            } else {
                listaMunicipios.clear();
                cbMunicipio.setDisable(true);
            }
            listaColonias.clear();
            tfCodigoPostal.clear();
        });

        cbMunicipio.valueProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                cargarColonias(nuevo.getIdMunicipio());
                cbColonia.setDisable(false);
            } else {
                listaColonias.clear();
                cbColonia.setDisable(true);
            }
        });

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
            Colonia primerResultado = coloniasEncontradas.get(0);
            
            if(primerResultado.getIdEstado() != null && primerResultado.getIdEstado() > 0){
                for(Estado e : cbEstado.getItems()){
                    if(e.getIdEstado().equals(primerResultado.getIdEstado())){
                        cbEstado.getSelectionModel().select(e);
                        break;
                    }
                }
                for(Municipio m : cbMunicipio.getItems()){
                    if(m.getIdMunicipio().equals(primerResultado.getIdMunicipio())){
                        cbMunicipio.getSelectionModel().select(m);
                        break;
                    }
                }
                cbColonia.getItems().clear();
                cbColonia.getItems().addAll(coloniasEncontradas);
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
            tfCodigoPostal.setText(String.valueOf(cliente.getCodigoPostal()));
            clicBuscarCP(null); 
            if(cliente.getIdColonia() > 0){
                for(Colonia c : cbColonia.getItems()){
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
        if (!validarCampos()) {
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
        
        Colonia col = cbColonia.getValue();
        if(col != null) cliente.setIdColonia(col.getIdColonia());
        
        try { 
            cliente.setCodigoPostal(Integer.parseInt(tfCodigoPostal.getText())); 
        } catch(Exception e) {
            // El CP ya fue validado, esto no debería ocurrir
        }

        if(clienteEdicion == null){
            procesarRespuesta(ClienteImp.registrar(cliente), "registrado");
        }else{
            cliente.setIdCliente(clienteEdicion.getIdCliente());
            procesarRespuesta(ClienteImp.editar(cliente), "actualizado");
        }
    }
    
    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();
        
        // Validaciones básicas
        if (tfNombre.getText().trim().isEmpty()) errores.append("- Nombre\n");
        if (tfPaterno.getText().trim().isEmpty()) errores.append("- Apellido Paterno\n");
        if (cbColonia.getValue() == null) errores.append("- Colonia\n");
        if (tfCalle.getText().trim().isEmpty()) errores.append("- Calle\n");
        if (tfNumero.getText().trim().isEmpty()) errores.append("- Número\n");
        if (tfTelefono.getText().trim().isEmpty()) errores.append("- Teléfono\n");
        if (tfCorreo.getText().trim().isEmpty()) errores.append("- Correo\n");
        
        // Validaciones específicas
        // Validación de correo duplicado (solo en modo creación)
        if (clienteEdicion == null) {
            String correo = tfCorreo.getText().trim();
            if (!correo.isEmpty() && ClienteImp.existePorCorreo(correo)) {
                errores.append("- Ya existe un cliente con este correo electrónico\n");
            }
        }
        
        String nombre = tfNombre.getText().trim();
        if (!nombre.isEmpty() && (nombre.length() > 30 || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+"))) {
            errores.append("- Nombre: máximo 30 caracteres, solo letras\n");
        }
        
        String paterno = tfPaterno.getText().trim();
        if (!paterno.isEmpty() && (paterno.length() > 30 || !paterno.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+"))) {
            errores.append("- Apellido Paterno: máximo 30 caracteres, solo letras\n");
        }
        
        String materno = tfMaterno.getText().trim();
        if (!materno.isEmpty() && !materno.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
            if (materno.length() > 30 || !materno.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                errores.append("- Apellido Materno: máximo 30 caracteres, solo letras\n");
            }
        }
        
        String telefono = tfTelefono.getText().trim();
        if (!telefono.isEmpty() && (telefono.length() != 10 || !telefono.matches("[0-9]{10}"))) {
            errores.append("- Teléfono: debe tener exactamente 10 dígitos\n");
        }
        
        String correo = tfCorreo.getText().trim();
        if (!correo.isEmpty()) {
            if (correo.length() > 30) {
                errores.append("- Correo: máximo 30 caracteres\n");
            } else if (!EMAIL_PATTERN.matcher(correo).matches()) {
                errores.append("- Correo: formato de correo electrónico inválido\n");
            }
        }
        
        String calle = tfCalle.getText().trim();
        if (!calle.isEmpty() && calle.length() > 50) {
            errores.append("- Calle: máximo 50 caracteres\n");
        }
        
        String numero = tfNumero.getText().trim();
        if (!numero.isEmpty() && (numero.length() > 10 || !numero.matches("[a-zA-Z0-9-]+"))) {
            errores.append("- Número: máximo 10 caracteres, alfanuméricos y guión\n");
        }
        
        String cp = tfCodigoPostal.getText().trim();
        if (cp.isEmpty() || cp.length() != 5 || !cp.matches("[0-9]{5}")) {
            errores.append("- Código Postal: debe tener 5 dígitos\n");
        }
        
        if (errores.length() > 0) {
            Utilidades.mostrarAlertaSimple("Campos inválidos", "Corrige:\n" + errores.toString(), Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
    
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
}
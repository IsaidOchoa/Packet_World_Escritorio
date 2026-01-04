package clienteescritoriopw;

import clienteescritoriopw.dominio.ClienteImp;
import clienteescritoriopw.dominio.DireccionImp;
import clienteescritoriopw.dominio.EnvioImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Cliente;
import clienteescritoriopw.pojo.Colaborador;
import clienteescritoriopw.pojo.Colonia;
import clienteescritoriopw.pojo.Envio;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class FXMLFormularioEnvioController implements Initializable {

    @FXML private TextField tfNumeroGuia;
    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private TextField tfNombreDestinatario;
    
    //Lógica de Dirección 
    @FXML private TextField tfCP; 
    @FXML private ComboBox<Estado> cbEstado;
    @FXML private ComboBox<Municipio> cbMunicipio;
    @FXML private ComboBox<Colonia> cbColonia;
    @FXML private TextField tfCalle;
    @FXML private TextField tfNumero;

    private Colaborador colaboradorSesion;
    private ObservableList<Cliente> listaClientes;
    private ObservableList<Estado> listaEstados;
    private ObservableList<Municipio> listaMunicipios;
    private ObservableList<Colonia> listaColonias;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarListas();
        generarNumeroGuia();
        configurarListeners();
        cargarClientes();
        cargarEstados(); 
    }

    public void inicializarColaborador(Colaborador colaborador) {
        this.colaboradorSesion = colaborador;
    }

    private void inicializarListas() {
        // Clientes
        listaClientes = FXCollections.observableArrayList();
        cbCliente.setItems(listaClientes);
        configurarComboCliente();

        // Direcciones
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
    
    // Generador de Guía
    private void generarNumeroGuia() {
        long timestamp = System.currentTimeMillis();
        String guia = "PKW-" + (timestamp % 100000000); 
        tfNumeroGuia.setText(guia);
    }

    private void cargarClientes() {
        List<Cliente> clientes = ClienteImp.obtenerClientes();
        if (clientes != null) {
            listaClientes.addAll(clientes);
        }
    }
    
    private void cargarEstados() {
        List<Estado> resultados = DireccionImp.obtenerEstados();
        if (resultados != null) {
            listaEstados.addAll(resultados);
        }
    }

    // 
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
    
    private void configurarComboCliente() {
        cbCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) {
                return (c == null) ? null : c.getNombre() + " " + c.getApellidoPaterno();
            }
            @Override
            public Cliente fromString(String string) { return null; }
        });
    }

    // 
    private void configurarListeners() {
        // Estado -> Municipios
        cbEstado.valueProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                cargarMunicipios(nuevo.getIdEstado());
                cbMunicipio.setDisable(false);
            } else {
                listaMunicipios.clear();
                cbMunicipio.setDisable(true);
            }
            listaColonias.clear();
        });

        // Municipio -> Colonias
        cbMunicipio.valueProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                cargarColonias(nuevo.getIdMunicipio());
                cbColonia.setDisable(false);
            } else {
                listaColonias.clear();
                cbColonia.setDisable(true);
            }
        });

        // Colonia -> CP Automático
        cbColonia.valueProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                tfCP.setText(String.valueOf(nuevo.getCodigoPostal()));
            }
        });
    }

    private void cargarMunicipios(int idEstado) {
        listaMunicipios.clear();
        List<Municipio> resultados = DireccionImp.obtenerMunicipios(idEstado);
        if (resultados != null) listaMunicipios.addAll(resultados);
    }

    private void cargarColonias(int idMunicipio) {
        listaColonias.clear();
        List<Colonia> resultados = DireccionImp.obtenerColoniasPorMunicipio(idMunicipio);
        if (resultados != null) listaColonias.addAll(resultados);
    }

    
    @FXML
    private void btnBuscarCP(ActionEvent event) {
        String cp = tfCP.getText().trim();
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

    @FXML
    private void btnGuardar(ActionEvent event) {
        if (validarCampos()) {
            Envio envio = new Envio();
            envio.setNumeroGuia(tfNumeroGuia.getText());
            envio.setNombreDestinatario(tfNombreDestinatario.getText());
            envio.setCalleDestino(tfCalle.getText());
            envio.setNumeroDestino(tfNumero.getText());
            envio.setCodigoPostalDestino(tfCP.getText());
            
           
            envio.setIdCliente(cbCliente.getValue().getIdCliente());
            envio.setIdColoniaDestino(cbColonia.getValue().getIdColonia());
            
            if (colaboradorSesion != null) {
                envio.setIdSucursalOrigen(colaboradorSesion.getIdSucursal());
            } else {
                Utilidades.mostrarAlertaSimple("Error", "No se identificó al usuario.", Alert.AlertType.ERROR);
                return;
            }

            Respuesta respuesta = EnvioImp.registrar(envio);
            if (!respuesta.isError()) {
                Utilidades.mostrarAlertaSimple("Éxito", "Envío registrado con éxito. Costo calculado.", Alert.AlertType.INFORMATION);
                ((Stage) tfNumeroGuia.getScene().getWindow()).close();
            } else {
                Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void btnCancelar(ActionEvent event) {
        ((Stage) tfNumeroGuia.getScene().getWindow()).close();
    }

    private boolean validarCampos() {
        return !(tfCalle.getText().isEmpty() || cbCliente.getValue() == null || cbColonia.getValue() == null);
    }
}
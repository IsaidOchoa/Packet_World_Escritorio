package clienteescritoriopw;

import clienteescritoriopw.dominio.ClienteImp;
import clienteescritoriopw.dominio.DireccionImp;
import clienteescritoriopw.dominio.EnvioImp;
import clienteescritoriopw.dominio.SucursalImp;
import clienteescritoriopw.dominio.UnidadImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Cliente;
import clienteescritoriopw.pojo.Colaborador;
import clienteescritoriopw.pojo.Colonia;
import clienteescritoriopw.pojo.Envio;
import clienteescritoriopw.pojo.Estado;
import clienteescritoriopw.pojo.Municipio;
import clienteescritoriopw.pojo.Sucursal;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class FXMLFormularioEnvioController implements Initializable {

    @FXML private Label lbTitulo; 
    @FXML private TextField tfNumeroGuia;
    @FXML private ComboBox<Cliente> cbCliente;
    @FXML private TextField tfNombreDestinatario;
    @FXML private ComboBox<Sucursal> cbSucursalOrigen; 
    @FXML private TextField tfCP; 
    @FXML private ComboBox<Estado> cbEstado;
    @FXML private ComboBox<Municipio> cbMunicipio;
    @FXML private ComboBox<Colonia> cbColonia;
    @FXML private TextField tfCalle;
    @FXML private TextField tfNumero;
    @FXML private Button btnAccion; 
    @FXML private ComboBox<Unidad> cbUnidad; // NUEVO


    private Colaborador colaboradorSesion;
    private Envio envioEdicion; 
    private boolean esEdicion = false;

    private ObservableList<Cliente> listaClientes;
    private ObservableList<Sucursal> listaSucursales;
    private ObservableList<Estado> listaEstados;
    private ObservableList<Municipio> listaMunicipios;
    private ObservableList<Colonia> listaColonias;
    private ObservableList<Unidad> listaUnidades;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarListas();
        
        cargarClientes();     
        cargarSucursales();   
        cargarEstados();
        cargarUnidades();
        
        configurarListeners();
        generarNumeroGuia(); 
    }

    private void inicializarListas() {
        listaClientes = FXCollections.observableArrayList();
        cbCliente.setItems(listaClientes);
        
        listaSucursales = FXCollections.observableArrayList();
        cbSucursalOrigen.setItems(listaSucursales);
        
        listaEstados = FXCollections.observableArrayList();
        cbEstado.setItems(listaEstados);
        
        listaMunicipios = FXCollections.observableArrayList();
        cbMunicipio.setItems(listaMunicipios);
        
        listaColonias = FXCollections.observableArrayList();
        cbColonia.setItems(listaColonias);
        
        configurarConvertidores();
    }

    // Configura cómo se ven los objetos en el ComboBox
    private void configurarConvertidores() {
        cbCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) {
                return (c == null) ? null : c.getNombre() + " " + c.getApellidoPaterno();
            }
            @Override
            public Cliente fromString(String string) { return null; }
        });

        cbSucursalOrigen.setConverter(new StringConverter<Sucursal>() {
            @Override
            public String toString(Sucursal s) { return (s == null) ? null : s.getNombre(); }
            @Override
            public Sucursal fromString(String string) { return null; }
        });
        
        cbEstado.setConverter(new StringConverter<Estado>() {
            @Override
            public String toString(Estado e) { return (e == null) ? null : e.getNombre(); }
            @Override
            public Estado fromString(String string) { return null; }
        });
        
        cbMunicipio.setConverter(new StringConverter<Municipio>() {
            @Override
            public String toString(Municipio m) { return (m == null) ? null : m.getNombre(); }
            @Override
            public Municipio fromString(String string) { return null; }
        });
        
        cbColonia.setConverter(new StringConverter<Colonia>() {
            @Override
            public String toString(Colonia c) { return (c == null) ? null : c.getNombre(); }
            @Override
            public Colonia fromString(String string) { return null; }
        });
    }

    private void cargarClientes() {
        List<Cliente> respuesta = ClienteImp.obtenerClientes();
        if(respuesta != null && !respuesta.isEmpty()){
            listaClientes.addAll(respuesta);
        }
    }
    
    private void cargarSucursales() {
        List<Sucursal> respuesta = SucursalImp.obtenerSucursales();
        if(respuesta != null) listaSucursales.addAll(respuesta);
    }
    
    private void cargarEstados() {
        List<Estado> respuesta = DireccionImp.obtenerEstados();
        if(respuesta != null) listaEstados.addAll(respuesta);
    }

    public void inicializarEnvioEdicion(Envio envio) {
        this.envioEdicion = envio;
        this.esEdicion = true;
        
        if(lbTitulo != null) lbTitulo.setText("Editar Envío");
        if(btnAccion != null) btnAccion.setText("Actualizar Envío");
        
        tfNumeroGuia.setText(envio.getNumeroGuia());
        tfNumeroGuia.setDisable(true); 

        tfNombreDestinatario.setText(envio.getNombreDestinatario());
        tfCalle.setText(envio.getCalleDestino());
        tfNumero.setText(envio.getNumeroDestino());
        tfCP.setText(envio.getCodigoPostalDestino());

        // Seleccionar Cliente
        if(!listaClientes.isEmpty()){
            for(Cliente c : listaClientes){
                if(c.getIdCliente() == envio.getIdCliente()){
                    cbCliente.getSelectionModel().select(c);
                    break;
                }
            }
        }
        // Seleccionar Sucursal
        if(!listaSucursales.isEmpty()){
            for(Sucursal s : listaSucursales){
                if(s.getIdSucursal() == envio.getIdSucursalOrigen()){
                    cbSucursalOrigen.getSelectionModel().select(s);
                    break;
                }
            }
        }
        // Recuperar Dirección
        if(envio.getCodigoPostalDestino() != null){
             recuperarDireccionPorCP(envio.getCodigoPostalDestino(), envio.getIdColoniaDestino());
        }
       // Recuperar Unidad
        if (envio.getIdUnidad() != null && envio.getIdUnidad() > 0) {
        for (Unidad u : cbUnidad.getItems()) {
            if (u.getIdUnidad() == envio.getIdUnidad().intValue()) {
                cbUnidad.getSelectionModel().select(u);
                break;
            }
        }
        }
    }
    
    private void recuperarDireccionPorCP(String cp, int idColoniaSeleccionada) {
        List<Colonia> coloniasEncontradas = DireccionImp.buscarPorCP(cp);
        
        if(coloniasEncontradas != null && !coloniasEncontradas.isEmpty()){
            Colonia datos = coloniasEncontradas.get(0);
            
            if(datos.getIdEstado() != null){
                for(Estado e : cbEstado.getItems()){
                    if(e.getIdEstado().equals(datos.getIdEstado())){
                        cbEstado.getSelectionModel().select(e);
                        break;
                    }
                }
                cargarMunicipios(datos.getIdEstado());
                
                for(Municipio m : cbMunicipio.getItems()){
                    if(m.getIdMunicipio().equals(datos.getIdMunicipio())){
                        cbMunicipio.getSelectionModel().select(m);
                        break;
                    }
                }
                
                cbColonia.getItems().clear();
                cbColonia.getItems().addAll(coloniasEncontradas);
                cbColonia.setDisable(false);
                
                if(idColoniaSeleccionada > 0){
                    for(Colonia c : cbColonia.getItems()){
                        if(c.getIdColonia() == idColoniaSeleccionada){
                            cbColonia.getSelectionModel().select(c);
                            break;
                        }
                    }
                } else {
                    cbColonia.getSelectionModel().select(0);
                }
            }
        }
    }

    @FXML
    private void btnBuscarCP(ActionEvent event) {
         String cp = tfCP.getText().trim();
        if(cp.isEmpty() || cp.length() != 5){
            Utilidades.mostrarAlertaSimple("CP Inválido", "Ingresa un CP de 5 dígitos.", Alert.AlertType.WARNING);
            return;
        }
        recuperarDireccionPorCP(cp, -1); 
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
    
    private void generarNumeroGuia() {
        if(!esEdicion){
            long timestamp = System.currentTimeMillis();
            tfNumeroGuia.setText("PKW-" + (timestamp % 100000000));
        }
    }
    
    public void inicializarColaborador(Colaborador colaborador) {
        this.colaboradorSesion = colaborador;
        if (!esEdicion && colaboradorSesion != null && !listaSucursales.isEmpty()) {
            for (Sucursal s : listaSucursales) {
                if (s.getIdSucursal() == colaboradorSesion.getIdSucursal()) {
                    cbSucursalOrigen.getSelectionModel().select(s);
                    break;
                }
            }
        }
    }

    @FXML
    private void btnGuardar(ActionEvent event) {
      if (validarCampos()) {
            Envio envio = esEdicion ? this.envioEdicion : new Envio();
            
            envio.setNumeroGuia(tfNumeroGuia.getText());
            envio.setNombreDestinatario(tfNombreDestinatario.getText());
            envio.setCalleDestino(tfCalle.getText());
            envio.setNumeroDestino(tfNumero.getText());
            envio.setCodigoPostalDestino(tfCP.getText());
            envio.setIdCliente(cbCliente.getValue().getIdCliente());
            envio.setIdColoniaDestino(cbColonia.getValue().getIdColonia());
            envio.setIdSucursalOrigen(cbSucursalOrigen.getValue().getIdSucursal());

            if (cbUnidad.getValue() != null) {
                envio.setIdUnidad(cbUnidad.getValue().getIdUnidad());
            } else {
                
                envio.setIdUnidad(null); 
            }

            if (!esEdicion) {
                Respuesta respuesta = EnvioImp.registrar(envio);
                mostrarAlerta(respuesta, "registrado");
            } else {
                Respuesta respuesta = EnvioImp.editar(envio);
                mostrarAlerta(respuesta, "actualizado");
            }
        }
    }
    
    private void mostrarAlerta(Respuesta respuesta, String accion) {
        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Éxito", "Envío " + accion + " correctamente.", Alert.AlertType.INFORMATION);
            ((Stage) tfNumeroGuia.getScene().getWindow()).close();
        } else {
            Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML private void btnCancelar(ActionEvent event) {
        ((Stage) tfNumeroGuia.getScene().getWindow()).close();
    }

    private boolean validarCampos() {
        if (tfNombreDestinatario.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos requeridos", "Por favor ingresa el nombre del destinatario.", Alert.AlertType.WARNING);
            tfNombreDestinatario.requestFocus();
            return false;
        }
        if (tfCalle.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos requeridos", "La calle es obligatoria.", Alert.AlertType.WARNING);
            tfCalle.requestFocus();
            return false;
        }
        if (tfNumero.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos requeridos", "El número de casa es obligatorio.", Alert.AlertType.WARNING);
            tfNumero.requestFocus();
            return false;
        }
        if (tfCP.getText().trim().isEmpty() || tfCP.getText().trim().length() != 5) {
             Utilidades.mostrarAlertaSimple("Formato incorrecto", "El Código Postal debe tener 5 dígitos.", Alert.AlertType.WARNING);
             tfCP.requestFocus();
             return false;
        }

        if (cbCliente.getValue() == null) {
            Utilidades.mostrarAlertaSimple("Selección requerida", "Debes seleccionar un Cliente Remitente.", Alert.AlertType.WARNING);
            cbCliente.requestFocus();
            return false;
        }
        if (cbSucursalOrigen.getValue() == null) {
            Utilidades.mostrarAlertaSimple("Selección requerida", "Debes seleccionar la Sucursal de Origen.", Alert.AlertType.WARNING);
            cbSucursalOrigen.requestFocus();
            return false;
        }
        if (cbColonia.getValue() == null) {
            Utilidades.mostrarAlertaSimple("Dirección incompleta", "Debes buscar el CP y seleccionar una Colonia.", Alert.AlertType.WARNING);
            tfCP.requestFocus();
            return false;
        }
        return true;
    
    }
    
    private void cargarUnidades() {
        listaUnidades = FXCollections.observableArrayList();
        List<Unidad> lista = UnidadImp.obtenerUnidades(); 
        if (lista != null) {
            listaUnidades.addAll(lista);
            cbUnidad.setItems(listaUnidades);
            
            cbUnidad.setConverter(new StringConverter<Unidad>() {
                @Override
                public String toString(Unidad u) {
                    return (u != null) ? u.getMarca() + " " + u.getModelo() + " (" + u.getAnio() + ")" : null;
                }
                @Override
                public Unidad fromString(String string) { return null; }
            });
        }
    }
}
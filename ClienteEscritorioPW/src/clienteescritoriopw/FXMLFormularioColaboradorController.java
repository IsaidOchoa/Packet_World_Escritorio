package clienteescritoriopw;

import clienteescritoriopw.dominio.CatalogoImp;
import clienteescritoriopw.dominio.ColaboradorImp;
import clienteescritoriopw.dominio.SucursalImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Colaborador;
import clienteescritoriopw.pojo.Rol;
import clienteescritoriopw.pojo.Sucursal;
import clienteescritoriopw.pojo.ValidacionDuplicadoColaborador;
import clienteescritoriopw.utilidad.Utilidades;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class FXMLFormularioColaboradorController implements Initializable {

    @FXML private Label lblTitulo;
    @FXML private TextField tfNoPersonal;
    @FXML private TextField tfNombre;
    @FXML private TextField tfPaterno;
    @FXML private TextField tfMaterno;
    @FXML private TextField tfCurp;
    @FXML private TextField tfCorreo;
    @FXML private ComboBox<Rol> cbRol;
    @FXML private ComboBox<Sucursal> cbSucursal;
    @FXML private TextField tfLicencia;
    @FXML private Label lblPasswordActual;
    @FXML private PasswordField pfPasswordActual;
    @FXML private Label lblPasswordNueva;
    @FXML private PasswordField pfPasswordNueva;
    @FXML private PasswordField pfPasswordConfirmar;
    @FXML private ImageView ivFoto;
    @FXML private Label lblLicencia;
    
    private Colaborador colaboradorEdicion;
    private File archivoFoto;
    private ObservableList<Rol> listaRoles;
    private ObservableList<Sucursal> listaSucursales;
    
    // Patrón para validar correo electrónico
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboBoxes();
        cargarCatalogos();
        configurarValidaciones();
    }    

    private void configurarComboBoxes() {
        cbRol.setConverter(new StringConverter<Rol>() {
            @Override public String toString(Rol object) { return (object != null) ? object.getNombre() : ""; }
            @Override public Rol fromString(String string) { return null; }
        });
        cbSucursal.setConverter(new StringConverter<Sucursal>() {
            @Override public String toString(Sucursal object) { return (object != null) ? object.getNombre() : ""; }
            @Override public Sucursal fromString(String string) { return null; }
        });
    }

    //Configuración de todas las validaciones en tiempo real
    private void configurarValidaciones() {
        configurarTextFieldLetras(tfNombre, 30);
        configurarTextFieldLetras(tfPaterno, 30);
        configurarTextFieldLetras(tfMaterno, 30);
        
        configurarTextFieldConCaracteres(tfNoPersonal, 10, "[a-zA-Z0-9_-]");
        
        configurarTextFieldConCaracteres(tfCurp, 18, "[a-zA-Z0-9]");
        
        configurarTextFieldLongitudMaxima(tfCorreo, 30);

        configurarTextFieldConCaracteres(tfLicencia, 30, "[a-zA-Z0-9-]");
    }
    
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

    public void inicializarEdicion(Colaborador colaborador) {
        this.colaboradorEdicion = colaborador;
        lblTitulo.setText("Editar Colaborador: " + colaborador.getNombre());
        tfNoPersonal.setDisable(true);
        cbRol.setDisable(true);
        
        lblPasswordActual.setVisible(true);
        lblPasswordActual.setManaged(true);
        pfPasswordActual.setVisible(true);
        pfPasswordActual.setManaged(true);
        lblPasswordNueva.setText("Nueva contraseña:");
        
        cargarDatosEdicion();
    }
    
    private void cargarCatalogos() {
        listaRoles = FXCollections.observableArrayList();
        listaSucursales = FXCollections.observableArrayList();

        List<Rol> rolesWS = CatalogoImp.obtenerRoles();
        if (rolesWS != null) listaRoles.addAll(rolesWS);
        cbRol.setItems(listaRoles);

        List<Sucursal> sucursalesWS = SucursalImp.obtenerSucursales();
        if (sucursalesWS != null) listaSucursales.addAll(sucursalesWS);
        cbSucursal.setItems(listaSucursales);

        cbRol.setOnAction(e -> actualizarVisibilidadLicencia());
    }
    
    
    private void cargarDatosEdicion() {
        tfNoPersonal.setText(colaboradorEdicion.getNumeroPersonal());
        tfNombre.setText(colaboradorEdicion.getNombre());
        tfPaterno.setText(colaboradorEdicion.getApellidoPaterno());
        tfMaterno.setText(colaboradorEdicion.getApellidoMaterno());
        tfCurp.setText(colaboradorEdicion.getCurp());
        tfCorreo.setText(colaboradorEdicion.getCorreo());
        tfLicencia.setText(colaboradorEdicion.getNumeroLicencia());

        if(colaboradorEdicion.getIdRol() > 0){
            for(Rol r : listaRoles){
                if(r.getIdRol() == colaboradorEdicion.getIdRol()){
                    cbRol.getSelectionModel().select(r);
                    break;
                }
            }
        }
        if(colaboradorEdicion.getIdSucursal() > 0){
            for(Sucursal s : listaSucursales){
                if(s.getIdSucursal() == colaboradorEdicion.getIdSucursal()){
                    cbSucursal.getSelectionModel().select(s);
                    break;
                }
            }
        }

        if (colaboradorEdicion.getFotoBase64() != null && !colaboradorEdicion.getFotoBase64().isEmpty()) {
            Image imagen = Utilidades.decodificarImagen(colaboradorEdicion.getFotoBase64());
            if(imagen != null) ivFoto.setImage(imagen);
        }

        actualizarVisibilidadLicencia();
    }

    @FXML
    private void clicSeleccionarFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.jpeg", "*.png")
        );
        archivoFoto = fileChooser.showOpenDialog(null);
        if (archivoFoto != null) {
            //Validar extensión del archivo
            String nombreArchivo = archivoFoto.getName().toLowerCase();
            if (!nombreArchivo.endsWith(".jpg") && !nombreArchivo.endsWith(".jpeg") && 
                !nombreArchivo.endsWith(".png")) {
                Utilidades.mostrarAlertaSimple("Formato inválido", 
                    "Solo se permiten archivos .jpg, .jpeg y .png", Alert.AlertType.WARNING);
                archivoFoto = null;
                return;
            }
            ivFoto.setImage(new Image(archivoFoto.toURI().toString()));
        }
    }

    @FXML
    private void clicAceptar(ActionEvent event) {
        if (validarCampos()) {
            if (colaboradorEdicion == null) {
                Colaborador colaborador = crearColaboradorDesdeFormulario();
                Respuesta respuesta = ColaboradorImp.registrar(colaborador);
                mostrarResultadoYCerrar(respuesta);
            } else {
                editarPerfilYFotoYPass();
            }
        }
    }
    
    private void actualizarVisibilidadLicencia() {
        boolean esConductor = false;
        if (cbRol.getValue() != null) {
            esConductor = cbRol.getValue().getIdRol() == 3;
        }
        lblLicencia.setVisible(esConductor);
        lblLicencia.setManaged(esConductor);
        tfLicencia.setVisible(esConductor);
        tfLicencia.setManaged(esConductor);
    }

    private void editarPerfilYFotoYPass() {
        Colaborador perfil = new Colaborador();
        perfil.setIdColaborador(colaboradorEdicion.getIdColaborador());
        perfil.setNombre(tfNombre.getText());
        perfil.setApellidoPaterno(tfPaterno.getText());
        perfil.setApellidoMaterno(tfMaterno.getText());
        perfil.setCurp(tfCurp.getText());
        perfil.setCorreo(tfCorreo.getText());
        perfil.setNumeroLicencia(tfLicencia.getText());

        Respuesta respPerfil = ColaboradorImp.editarPerfil(perfil);
        if (respPerfil.isError()) {
            Utilidades.mostrarAlertaSimple("Error", "Perfil: " + respPerfil.getMensaje(), Alert.AlertType.ERROR);
            return;
        }

        if (archivoFoto != null) {
            try {
                byte[] bytes = Files.readAllBytes(archivoFoto.toPath());
                String base64 = Base64.getEncoder().encodeToString(bytes);
                Colaborador foto = new Colaborador();
                foto.setIdColaborador(colaboradorEdicion.getIdColaborador());
                foto.setFotoBase64(base64);
                Respuesta respFoto = ColaboradorImp.editarFoto(foto);
                if (respFoto.isError()) {
                    Utilidades.mostrarAlertaSimple("Error", "Foto: " + respFoto.getMensaje(), Alert.AlertType.ERROR);
                    return;
                }
            } catch (IOException e) {
                Utilidades.mostrarAlertaSimple("Error", "No se pudo leer la imagen.", Alert.AlertType.ERROR);
                return;
            }
        }

        if (!pfPasswordNueva.getText().isEmpty()) {
            Respuesta respPass = ColaboradorImp.editarPassword(
                colaboradorEdicion.getIdColaborador(), 
                pfPasswordActual.getText(),
                pfPasswordNueva.getText()
            );
            if (respPass.isError()) {
                Utilidades.mostrarAlertaSimple("Error", "Contraseña: " + respPass.getMensaje(), Alert.AlertType.ERROR);
                return;
            }
        }

        Utilidades.mostrarAlertaSimple("Éxito", "Cambios guardados correctamente.", Alert.AlertType.INFORMATION);
        cerrarVentana();
    }

    private Colaborador crearColaboradorDesdeFormulario() {
        Colaborador c = new Colaborador();
        c.setNumeroPersonal(tfNoPersonal.getText());
        c.setNombre(tfNombre.getText());
        c.setApellidoPaterno(tfPaterno.getText());
        c.setApellidoMaterno(tfMaterno.getText());
        c.setCurp(tfCurp.getText());
        c.setCorreo(tfCorreo.getText());
        c.setNumeroLicencia(tfLicencia.getText());
        c.setIdRol(cbRol.getValue().getIdRol());
        c.setIdSucursal(cbSucursal.getValue().getIdSucursal());
        c.setPassword(pfPasswordNueva.getText());
        if (archivoFoto != null) {
            try {
                c.setFoto(Files.readAllBytes(archivoFoto.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return c;
    }

    private void mostrarResultadoYCerrar(Respuesta respuesta) {
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
        ((Stage) tfNoPersonal.getScene().getWindow()).close();
    }
    
    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();
        
        // Validación básica de campos obligatorios
        if (tfNoPersonal.getText().trim().isEmpty()) errores.append("- Número de Personal\n");
        if (tfNombre.getText().trim().isEmpty()) errores.append("- Nombre\n");
        if (tfPaterno.getText().trim().isEmpty()) errores.append("- Apellido Paterno\n");
        if (tfCurp.getText().trim().isEmpty()) errores.append("- CURP\n");
        if (cbRol.getValue() == null) errores.append("- Rol\n");
        if (cbSucursal.getValue() == null) errores.append("- Sucursal\n");
        
        // Validaciones específicas

        if (colaboradorEdicion == null || 
            !tfNoPersonal.getText().trim().equals(colaboradorEdicion.getNumeroPersonal()) ||
            !tfCurp.getText().trim().equals(colaboradorEdicion.getCurp()) ||
            !tfCorreo.getText().trim().equals(colaboradorEdicion.getCorreo()) ||
            !tfLicencia.getText().trim().equals(colaboradorEdicion.getNumeroLicencia())) {

            ValidacionDuplicadoColaborador datos = new ValidacionDuplicadoColaborador();
            datos.setNumeroPersonal(tfNoPersonal.getText().trim());
            datos.setCurp(tfCurp.getText().trim());
            datos.setCorreo(tfCorreo.getText().trim());
            datos.setNumeroLicencia(tfLicencia.getText().trim());

            if (colaboradorEdicion != null) {
                datos.setIdColaboradorExcluir(colaboradorEdicion.getIdColaborador());
            }

            Respuesta validacion = ColaboradorImp.validarDuplicadosColaborador(datos);
            if (validacion.isError()) {
                errores.append(validacion.getMensaje()).append("\n");
            }
        }
        
        String noPersonal = tfNoPersonal.getText().trim();
        if (!noPersonal.isEmpty()) {
            if (noPersonal.length() > 10 || !noPersonal.matches("[a-zA-Z0-9_-]+")) {
                errores.append("- No. Personal: máximo 10 caracteres, solo letras, números, guión y guión bajo\n");
            }
        }
        
        String nombre = tfNombre.getText().trim();
        if (!nombre.isEmpty()) {
            if (nombre.length() > 30 || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                errores.append("- Nombre: máximo 30 caracteres, solo letras\n");
            }
        }
        
        String paterno = tfPaterno.getText().trim();
        if (!paterno.isEmpty()) {
            if (paterno.length() > 30 || !paterno.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                errores.append("- Apellido Paterno: máximo 30 caracteres, solo letras\n");
            }
        }
        
        String materno = tfMaterno.getText().trim();
        if (!materno.isEmpty() && !materno.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
            if (materno.length() > 30 || !materno.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                errores.append("- Apellido Materno: máximo 30 caracteres, solo letras\n");
            }
        }
        
        String curp = tfCurp.getText().trim();
        if (!curp.isEmpty()) {
            if (curp.length() != 18 || !curp.matches("[a-zA-Z0-9]{18}")) {
                errores.append("- CURP: debe tener exactamente 18 caracteres alfanuméricos\n");
            }
        }
        
        String correo = tfCorreo.getText().trim();
        if (!correo.isEmpty()) {
            if (correo.length() > 30) {
                errores.append("- Correo: máximo 30 caracteres\n");
            } else if (!EMAIL_PATTERN.matcher(correo).matches()) {
                errores.append("- Correo: formato de correo electrónico inválido\n");
            }
        }
        
        String licencia = tfLicencia.getText().trim();
        if (!licencia.isEmpty() && cbRol.getValue() != null && cbRol.getValue().getIdRol() == 3) {
            if (licencia.length() > 30 || !licencia.matches("[a-zA-Z0-9_-]+")) {
                errores.append("- Licencia: máximo 30 caracteres alfanuméricos y (-) \n");
            }
        }
        
        // Validación de contraseña
        if (colaboradorEdicion == null) {
            if (pfPasswordNueva.getText().isEmpty()) {
                errores.append("- Contraseña (requerida al registrar)\n");
            } else if (!pfPasswordNueva.getText().equals(pfPasswordConfirmar.getText())) {
                errores.append("- Las contraseñas no coinciden\n");
            }
        } else {
            if (!pfPasswordNueva.getText().isEmpty()) {
                if (pfPasswordActual.getText().isEmpty()) {
                    errores.append("- Contraseña actual requerida\n");
                } else if (!pfPasswordNueva.getText().equals(pfPasswordConfirmar.getText())) {
                    errores.append("- Las nuevas contraseñas no coinciden\n");
                }
            }
        }
        
        if (errores.length() > 0) {
            Utilidades.mostrarAlertaSimple("Campos inválidos", "Corrige:\n" + errores.toString(), Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
}
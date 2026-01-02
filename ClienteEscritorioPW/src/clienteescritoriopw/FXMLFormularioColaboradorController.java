package clienteescritoriopw;

import clienteescritoriopw.dominio.CatalogoImp;
import clienteescritoriopw.dominio.ColaboradorImp;
import clienteescritoriopw.dominio.SucursalImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Colaborador;
import clienteescritoriopw.pojo.Rol;
import clienteescritoriopw.pojo.Sucursal;
import clienteescritoriopw.utilidad.Utilidades;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboBoxes();
        cargarCatalogos();
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

    public void inicializarEdicion(Colaborador colaborador) {
        this.colaboradorEdicion = colaborador;
        lblTitulo.setText("Editar Colaborador: " + colaborador.getNombre());
        tfNoPersonal.setDisable(true);
        cbRol.setDisable(true);
        
        // Mostrar campos de contraseña actual en modo edición
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

        // Agregar listener para detectar cambios en el rol
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

        // Actualizar visibilidad de licencia después de cargar los datos
        actualizarVisibilidadLicencia();
    }

    @FXML
    private void clicSeleccionarFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png", "*.jpeg"));
        archivoFoto = fileChooser.showOpenDialog(null);
        if (archivoFoto != null) {
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
            esConductor = cbRol.getValue().getIdRol() == 3; // ID de conductor = 3
        }

        // Mostrar/ocultar en el VBox de la derecha
        lblLicencia.setVisible(esConductor);
        lblLicencia.setManaged(esConductor);
        tfLicencia.setVisible(esConductor);
        tfLicencia.setManaged(esConductor);
    }

    private void editarPerfilYFotoYPass() {
        // 1. ACTUALIZAR PERFIL
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

        // 2. ACTUALIZAR FOTO
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

        // 3. ACTUALIZAR CONTRASEÑA (si aplica)
        if (!pfPasswordNueva.getText().isEmpty()) {
            Respuesta respPass = ColaboradorImp.editarPassword(
                colaboradorEdicion.getIdColaborador(), 
                pfPasswordActual.getText(), // ← Contraseña real del usuario
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
        c.setPassword(pfPasswordNueva.getText()); // ← Usa pfPasswordNueva
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
        if (tfNoPersonal.getText().trim().isEmpty() || tfNombre.getText().trim().isEmpty() || 
            tfPaterno.getText().trim().isEmpty() || tfCurp.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos requeridos", "Por favor, llene los campos obligatorios (Nombre, Paterno, No. Personal, CURP).", Alert.AlertType.WARNING);
            return false;
        }
        if (cbRol.getValue() == null) {
            Utilidades.mostrarAlertaSimple("Selección requerida", "Debe asignar un Rol al colaborador.", Alert.AlertType.WARNING);
            return false;
        }
        if (cbSucursal.getValue() == null) {
            Utilidades.mostrarAlertaSimple("Selección requerida", "Debe asignar una Sucursal.", Alert.AlertType.WARNING);
            return false;
        }

        // VALIDACIÓN DE CONTRASEÑA
        if (colaboradorEdicion == null) {
            // MODO CREACIÓN
            if (pfPasswordNueva.getText().isEmpty()) {
                Utilidades.mostrarAlertaSimple("Seguridad", "Debe establecer una contraseña.", Alert.AlertType.WARNING);
                return false;
            }
            if (!pfPasswordNueva.getText().equals(pfPasswordConfirmar.getText())) {
                Utilidades.mostrarAlertaSimple("Error", "Las contraseñas no coinciden.", Alert.AlertType.WARNING);
                return false;
            }
        } else {
            // MODO EDICIÓN
            if (!pfPasswordNueva.getText().isEmpty()) {
                if (pfPasswordActual.getText().isEmpty()) {
                    Utilidades.mostrarAlertaSimple("Error", "Debe ingresar su contraseña actual.", Alert.AlertType.WARNING);
                    return false;
                }
                if (!pfPasswordNueva.getText().equals(pfPasswordConfirmar.getText())) {
                    Utilidades.mostrarAlertaSimple("Error", "Las nuevas contraseñas no coinciden.", Alert.AlertType.WARNING);
                    return false;
                }
            }
        }
        return true;
    }
}
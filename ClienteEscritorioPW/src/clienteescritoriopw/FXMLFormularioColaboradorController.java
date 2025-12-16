/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteescritoriopw;

import clienteescritoriopw.dominio.CatalogoImp;
import clienteescritoriopw.dominio.ColaboradorImp;
import clienteescritoriopw.dto.Respuesta;
import clienteescritoriopw.pojo.Colaborador;
import clienteescritoriopw.pojo.Rol;
import clienteescritoriopw.pojo.Sucursal;
import clienteescritoriopw.utilidad.Utilidades;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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
import javafx.util.StringConverter; // IMPORTANTE PARA EL COMBOBOX


public class FXMLFormularioColaboradorController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField tfNoPersonal;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfPaterno;
    @FXML
    private TextField tfMaterno;
    @FXML
    private TextField tfCurp;
    @FXML
    private TextField tfCorreo;
    @FXML
    private ComboBox<Rol> cbRol;
    @FXML
    private ComboBox<Sucursal> cbSucursal;
    @FXML
    private TextField tfLicencia;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private PasswordField pfConfirmarPassword;
    @FXML
    private ImageView ivFoto;
    
    // Variables auxiliares
    private Colaborador colaboradorEdicion;
    private File archivoFoto;
    private ObservableList<Rol> listaRoles;
    private ObservableList<Sucursal> listaSucursales;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboBoxes();
        cargarCatalogos();
    }    
    
    // 1. Configuración visual para que los ComboBox muestren el NOMBRE y no el objeto raro
    private void configurarComboBoxes() {
        cbRol.setConverter(new StringConverter<Rol>() {
            @Override
            public String toString(Rol object) {
                return (object != null) ? object.getNombre() : "";
            }

            @Override
            public Rol fromString(String string) {
                return null; // No necesario para ComboBox de selección
            }
        });
        
        cbSucursal.setConverter(new StringConverter<Sucursal>() {
            @Override
            public String toString(Sucursal object) {
                return (object != null) ? object.getNombre() : "";
            }

            @Override
            public Sucursal fromString(String string) {
                return null;
            }
        });
    }

    // 2. Método llamado desde la lista para pasar los datos
    public void inicializarEdicion(Colaborador colaborador) {
        this.colaboradorEdicion = colaborador;
        lblTitulo.setText("Editar Colaborador: " + colaborador.getNombre());
        
        // RESTRICCIONES DE EDICIÓN (Lo que pediste)
        tfNoPersonal.setDisable(true); // No editar No. Personal
        cbRol.setDisable(true);        // No editar Rol
        
        cargarDatosEdicion();
        
        // Ocultar placeholders de contraseña
        pfPassword.setPromptText("Dejar vacío para mantener actual");
        pfConfirmarPassword.setPromptText("Dejar vacío para mantener actual");
    }
    
    private void cargarCatalogos() {
        listaRoles = FXCollections.observableArrayList();
        listaSucursales = FXCollections.observableArrayList();
        
        List<Rol> rolesWS = CatalogoImp.obtenerRoles();
        if (rolesWS != null) listaRoles.addAll(rolesWS);
        cbRol.setItems(listaRoles);
        
        List<Sucursal> sucursalesWS = CatalogoImp.obtenerSucursales();
        if (sucursalesWS != null) listaSucursales.addAll(sucursalesWS);
        cbSucursal.setItems(listaSucursales);
    }
    
    private void cargarDatosEdicion() {
        tfNoPersonal.setText(colaboradorEdicion.getNumeroPersonal());
        tfNombre.setText(colaboradorEdicion.getNombre());
        tfPaterno.setText(colaboradorEdicion.getApellidoPaterno());
        tfMaterno.setText(colaboradorEdicion.getApellidoMaterno());
        tfCurp.setText(colaboradorEdicion.getCurp());
        tfCorreo.setText(colaboradorEdicion.getCorreo());
        tfLicencia.setText(colaboradorEdicion.getNumeroLicencia());
        
        // Seleccionar Rol Correcto en el ComboBox
        if(colaboradorEdicion.getIdRol() > 0){
             for(Rol r : listaRoles){
                 if(r.getIdRol() == colaboradorEdicion.getIdRol()){
                     cbRol.getSelectionModel().select(r);
                     break;
                 }
             }
        }
        
        // Seleccionar Sucursal Correcta
        if(colaboradorEdicion.getIdSucursal() > 0){
             for(Sucursal s : listaSucursales){
                 if(s.getIdSucursal() == colaboradorEdicion.getIdSucursal()){
                     cbSucursal.getSelectionModel().select(s);
                     break;
                 }
             }
        }

        // Cargar Foto
        if (colaboradorEdicion.getFotoBase64() != null && !colaboradorEdicion.getFotoBase64().isEmpty()) {
            Image imagen = Utilidades.decodificarImagen(colaboradorEdicion.getFotoBase64());
            if(imagen != null) ivFoto.setImage(imagen);
        }
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
            
            Colaborador colaborador = new Colaborador();
            
            if (colaboradorEdicion != null) {
                colaborador.setIdColaborador(colaboradorEdicion.getIdColaborador());
            }

            colaborador.setNumeroPersonal(tfNoPersonal.getText());
            colaborador.setNombre(tfNombre.getText());
            colaborador.setApellidoPaterno(tfPaterno.getText());
            colaborador.setApellidoMaterno(tfMaterno.getText());
            colaborador.setCurp(tfCurp.getText());
            colaborador.setCorreo(tfCorreo.getText());
            colaborador.setNumeroLicencia(tfLicencia.getText());
            
            colaborador.setIdRol(cbRol.getValue().getIdRol());
            colaborador.setIdSucursal(cbSucursal.getValue().getIdSucursal());

       
            if (colaboradorEdicion == null || !pfPassword.getText().isEmpty()) {
                colaborador.setPassword(pfPassword.getText());
            } else {
                colaborador.setPassword(colaboradorEdicion.getPassword());
            }

            try {
                if (archivoFoto != null) {
                    colaborador.setFoto(Files.readAllBytes(archivoFoto.toPath()));
                } else if (colaboradorEdicion != null) {
         
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Respuesta respuesta = (colaboradorEdicion == null) ? 
                ColaboradorImp.registrar(colaborador) : ColaboradorImp.editar(colaborador);

            if (!respuesta.isError()) {
                Utilidades.mostrarAlertaSimple("Éxito", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
                cerrarVentana();
            } else {
                Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
            }
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

        if (colaboradorEdicion == null && pfPassword.getText().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Seguridad", "Debe establecer una contraseña para el nuevo colaborador.", Alert.AlertType.WARNING);
            return false;
        }
        
        // Si hay texto en la contraseña (sea nuevo o edición), deben coincidir
        if (!pfPassword.getText().isEmpty()) {
            if (!pfPassword.getText().equals(pfConfirmarPassword.getText())) {
                Utilidades.mostrarAlertaSimple("Error de contraseña", "Las contraseñas no coinciden.", Alert.AlertType.WARNING);
                return false;
            }
        }

        return true;
    }
}
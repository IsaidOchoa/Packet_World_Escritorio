package clienteescritoriopw.pojo;

import clienteescritoriopw.pojo.Paquete;
import java.util.List; 

public class Envio {
    private Integer idEnvio;
    private String numeroGuia;
    private double costo;
    private double peso;
    
    
    private List<Paquete> paquetes; 
    
    // Cliente
    private int idCliente;
    private String nombreCliente; 
    
    // Origen
    private int idSucursalOrigen;
    private String nombreSucursalOrigen; 
    
    // Destino (Datos de la tabla Envio)
    private int idColoniaDestino;
    private String calleDestino;
    private String numeroDestino;
    private String nombreDestinatario;
    
    // Destino ( Colonia/Municipio)
    private String nombreColonia;
    private String municipio;
    private String estado;
    
    
    private String codigoPostalDestino; 
    
    // Unidad
    private int idUnidad;
    private String infoUnidad; 
    private int idConductor;
    private String nombreConductor;
    
    // Estatus
    private Integer idEstadoActual;
    private String estatus; 

    public Envio() {
    }

    public Envio(Integer idEnvio, String numeroGuia, double costo, double peso, List<Paquete> paquetes, int idCliente, String nombreCliente, int idSucursalOrigen, String nombreSucursalOrigen, int idColoniaDestino, String calleDestino, String numeroDestino, String nombreDestinatario, String nombreColonia, String municipio, String estado, String codigoPostalDestino, int idUnidad, String infoUnidad, int idConductor, String nombreConductor, Integer idEstadoActual, String estatus) {
        this.idEnvio = idEnvio;
        this.numeroGuia = numeroGuia;
        this.costo = costo;
        this.peso = peso;
        this.paquetes = paquetes;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.idSucursalOrigen = idSucursalOrigen;
        this.nombreSucursalOrigen = nombreSucursalOrigen;
        this.idColoniaDestino = idColoniaDestino;
        this.calleDestino = calleDestino;
        this.numeroDestino = numeroDestino;
        this.nombreDestinatario = nombreDestinatario;
        this.nombreColonia = nombreColonia;
        this.municipio = municipio;
        this.estado = estado;
        this.codigoPostalDestino = codigoPostalDestino;
        this.idUnidad = idUnidad;
        this.infoUnidad = infoUnidad;
        this.idConductor = idConductor;
        this.nombreConductor = nombreConductor;
        this.idEstadoActual = idEstadoActual;
        this.estatus = estatus;
    }

    public Integer getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(Integer idEnvio) {
        this.idEnvio = idEnvio;
    }

    public String getNumeroGuia() {
        return numeroGuia;
    }

    public void setNumeroGuia(String numeroGuia) {
        this.numeroGuia = numeroGuia;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public List<Paquete> getPaquetes() {
        return paquetes;
    }

    public void setPaquetes(List<Paquete> paquetes) {
        this.paquetes = paquetes;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public int getIdSucursalOrigen() {
        return idSucursalOrigen;
    }

    public void setIdSucursalOrigen(int idSucursalOrigen) {
        this.idSucursalOrigen = idSucursalOrigen;
    }

    public String getNombreSucursalOrigen() {
        return nombreSucursalOrigen;
    }

    public void setNombreSucursalOrigen(String nombreSucursalOrigen) {
        this.nombreSucursalOrigen = nombreSucursalOrigen;
    }

    public int getIdColoniaDestino() {
        return idColoniaDestino;
    }

    public void setIdColoniaDestino(int idColoniaDestino) {
        this.idColoniaDestino = idColoniaDestino;
    }

    public String getCalleDestino() {
        return calleDestino;
    }

    public void setCalleDestino(String calleDestino) {
        this.calleDestino = calleDestino;
    }

    public String getNumeroDestino() {
        return numeroDestino;
    }

    public void setNumeroDestino(String numeroDestino) {
        this.numeroDestino = numeroDestino;
    }

    public String getNombreDestinatario() {
        return nombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
    }

    public String getNombreColonia() {
        return nombreColonia;
    }

    public void setNombreColonia(String nombreColonia) {
        this.nombreColonia = nombreColonia;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigoPostalDestino() {
        return codigoPostalDestino;
    }

    public void setCodigoPostalDestino(String codigoPostalDestino) {
        this.codigoPostalDestino = codigoPostalDestino;
    }

    public int getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(int idUnidad) {
        this.idUnidad = idUnidad;
    }

    public String getInfoUnidad() {
        return infoUnidad;
    }

    public void setInfoUnidad(String infoUnidad) {
        this.infoUnidad = infoUnidad;
    }

    public int getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(int idConductor) {
        this.idConductor = idConductor;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    public Integer getIdEstadoActual() {
        return idEstadoActual;
    }

    public void setIdEstadoActual(Integer idEstadoActual) {
        this.idEstadoActual = idEstadoActual;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

}
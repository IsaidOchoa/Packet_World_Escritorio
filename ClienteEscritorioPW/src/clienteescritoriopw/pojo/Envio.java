/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteescritoriopw.pojo;

/**
 *
 * @author pepeg
 */
public class Envio {
    private int idEnvio;
    private String numeroGuia;
    private double costo;
    private double peso;
    
    // Cliente
    private int idCliente;
    private String nombreCliente; 
    
    // Origen
    private int idSucursalOrigen;
    private String nombreSucursalOrigen; 
    
    // Destino (Datos crudos + ID Colonia de mexico.sql)
    private int idColoniaDestino;
    private String calleDestino;
    private String numeroDestino;
    private String nombreDestinatario;
    
    // Destino (Datos visuales obtenidos del JOIN)
    private String nombreColonia;
    private String municipio;
    private String estado;
    private int cp;
    
    // Logística
    private int idUnidad;
    private String infoUnidad; 
    private int idConductor;
    private String nombreConductor;
    
    // Estatus
    private int idEstadoActual;
    private String estatus; // Nombre del estado (ej. En Tránsito)

    public Envio() {
    }

    public Envio(int idEnvio, String numeroGuia, double costo, double peso, int idCliente, String nombreCliente, int idSucursalOrigen, String nombreSucursalOrigen, int idColoniaDestino, String calleDestino, String numeroDestino, String nombreDestinatario, String nombreColonia, String municipio, String estado, int cp, int idUnidad, String infoUnidad, int idConductor, String nombreConductor, int idEstadoActual, String estatus) {
        this.idEnvio = idEnvio;
        this.numeroGuia = numeroGuia;
        this.costo = costo;
        this.peso = peso;
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
        this.cp = cp;
        this.idUnidad = idUnidad;
        this.infoUnidad = infoUnidad;
        this.idConductor = idConductor;
        this.nombreConductor = nombreConductor;
        this.idEstadoActual = idEstadoActual;
        this.estatus = estatus;
    }

    public int getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(int idEnvio) {
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

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
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

    public int getIdEstadoActual() {
        return idEstadoActual;
    }

    public void setIdEstadoActual(int idEstadoActual) {
        this.idEstadoActual = idEstadoActual;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    
}

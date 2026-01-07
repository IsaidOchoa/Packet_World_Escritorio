/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojo;

public class Unidad {
    
    
    private Integer idUnidad;
    private String marca;
    private String modelo;
    private int anio;
    private String vin;
    private String nii; // Número de Identificación Interno
    
    // Relación con Tipo de Unidad
    private int idTipoUnidad;
    private String nombreTipo; // Ejemplo:(Gasolina, Diesel, Eléctrica,Hibrida)
    
    // Relación con Sucursal
    private int idSucursal;
    private String nombreSucursal;
    
    private Integer idColaborador;  
    private String nombreColaborador;

    public Unidad() {
    }

    public Unidad(int idUnidad, String marca, String modelo, int anio, String vin, String nii, int idTipoUnidad, String nombreTipo, int idSucursal, String nombreSucursal, Integer idColaborador, String nombreColaborador) {
        this.idUnidad = idUnidad;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.vin = vin;
        this.nii = nii;
        this.idTipoUnidad = idTipoUnidad;
        this.nombreTipo = nombreTipo;
        this.idSucursal = idSucursal;
        this.nombreSucursal = nombreSucursal;
        this.idColaborador = idColaborador;
        this.nombreColaborador = nombreColaborador;
    }

    public Integer getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(Integer idUnidad) {
        this.idUnidad = idUnidad;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getNii() {
        return nii;
    }

    public void setNii(String nii) {
        this.nii = nii;
    }

    public int getIdTipoUnidad() {
        return idTipoUnidad;
    }

    public void setIdTipoUnidad(int idTipoUnidad) {
        this.idTipoUnidad = idTipoUnidad;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public Integer getIdColaborador() {
        return idColaborador;
    }

    public void setIdColaborador(Integer idColaborador) {
        this.idColaborador = idColaborador;
    }

    public String getNombreColaborador() {
        return nombreColaborador;
    }

    public void setNombreColaborador(String nombreColaborador) {
        this.nombreColaborador = nombreColaborador;
    }

   
}
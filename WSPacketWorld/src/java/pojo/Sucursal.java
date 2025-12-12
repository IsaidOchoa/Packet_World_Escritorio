/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojo;

/**
 *
 * @author pepeg
 */
public class Sucursal {

    private int idSucursal;
    private String nombre;
    private int idColonia; // Clave foránea a la tabla colonias de mexico.sql
    private String calle;
    private String numero;
    private Double latitud;
    private Double longitud;
    
    // Campos extra para mostrar información bonita en la tabla (vienen de los Joins)
    private String nombreColonia;
    private int cp;
    private String municipio;
    private String estado;

    public Sucursal() {
    }

    public Sucursal(int idSucursal, String nombre, int idColonia, String calle, String numero, Double latitud, Double longitud, String nombreColonia, int cp, String municipio, String estado) {
        this.idSucursal = idSucursal;
        this.nombre = nombre;
        this.idColonia = idColonia;
        this.calle = calle;
        this.numero = numero;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombreColonia = nombreColonia;
        this.cp = cp;
        this.municipio = municipio;
        this.estado = estado;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdColonia() {
        return idColonia;
    }

    public void setIdColonia(int idColonia) {
        this.idColonia = idColonia;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getNombreColonia() {
        return nombreColonia;
    }

    public void setNombreColonia(String nombreColonia) {
        this.nombreColonia = nombreColonia;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
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

  
}


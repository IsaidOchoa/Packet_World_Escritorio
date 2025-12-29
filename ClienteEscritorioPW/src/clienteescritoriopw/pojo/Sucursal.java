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
public class Sucursal {
    private Integer idSucursal;
    private String nombre;
    private String calle;
    private String numero;
    private Integer idColonia;
    private Integer estatus; 
    
    
    private String nombreColonia;
    private String codigoPostal;
    private String municipio;
    private String estado;

    public Sucursal() {
    }

    public Sucursal(Integer idSucursal, String nombre, String calle, String numero, Integer idColonia, Integer estatus, String nombreColonia, String codigoPostal, String municipio, String estado) {
        this.idSucursal = idSucursal;
        this.nombre = nombre;
        this.calle = calle;
        this.numero = numero;
        this.idColonia = idColonia;
        this.estatus = estatus;
        this.nombreColonia = nombreColonia;
        this.codigoPostal = codigoPostal;
        this.municipio = municipio;
        this.estado = estado;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public Integer getIdColonia() {
        return idColonia;
    }

    public void setIdColonia(Integer idColonia) {
        this.idColonia = idColonia;
    }

    public Integer getEstatus() {
        return estatus;
    }

    public void setEstatus(Integer estatus) {
        this.estatus = estatus;
    }

    public String getNombreColonia() {
        return nombreColonia;
    }

    public void setNombreColonia(String nombreColonia) {
        this.nombreColonia = nombreColonia;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
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
  
    public String getEstatusTexto() {
    return (estatus != null && estatus == 1) ? "Activa" : "Inactiva";
}
 }

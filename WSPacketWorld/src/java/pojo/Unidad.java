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
public class Unidad {
    
    
    private int idUnidad;
    private String marca;
    private String modelo;
    private int anio;
    private String vin;
    private String placa;
    
    // Relación con Tipo de Unidad
    private int idTipoUnidad;
    private String tipo; // Ejemplo:(Gasolina, Diesel, Eléctrica,Hibrida)
    
    // Relación con Sucursal
    private int idSucursal;
    private String nombreSucursal;

    public Unidad() {
    }

        public int getIdUnidad() {
            return idUnidad;
        }

        public void setIdUnidad(int idUnidad) {
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

        public String getPlaca() {
            return placa;
        }

        public void setPlaca(String placa) {
            this.placa = placa;
        }

        public int getIdTipoUnidad() {
            return idTipoUnidad;
        }

        public void setIdTipoUnidad(int idTipoUnidad) {
            this.idTipoUnidad = idTipoUnidad;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
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

   
}
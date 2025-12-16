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
public class RespuestaHTTP {

     private int codigo;
   private String contenido;

    public RespuestaHTTP() {
    }

    public RespuestaHTTP(int codigo, String contenido) {
        this.codigo = codigo;
        this.contenido = contenido;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
   
   
   
}
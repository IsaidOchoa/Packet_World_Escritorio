/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteescritoriopw.dto;

import clienteescritoriopw.pojo.Colaborador;

/**
 *
 * @author pepeg
 */
public class RSAutenticacionColaborador {
  
    private boolean error;
    private String mensaje;
    private Colaborador colaborador;

    public RSAutenticacionColaborador() {
    }

    public RSAutenticacionColaborador(boolean error, String mensaje, Colaborador colaborador) {
        this.error = error;
        this.mensaje = mensaje;
        this.colaborador = colaborador;
    }

    public boolean isError() { 
        return error; 
    }
    
    public void setError(boolean error) { 
        this.error = error; 
    }

    public String getMensaje() { 
        return mensaje; 
    }
    
    public void setMensaje(String mensaje) { 
        this.mensaje = mensaje; 
    }

    public Colaborador getColaborador() { 
        return colaborador; 
    }
    
    public void setColaborador(Colaborador colaborador) { 
        this.colaborador = colaborador; 
    }
}


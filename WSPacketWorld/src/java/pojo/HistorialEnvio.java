package pojo;

import java.sql.Timestamp;

public class HistorialEnvio {
    private int idHistorial;
    private int idEnvio;
    private int idEstadoEnvio;
    private Timestamp fechaCambio;
    private String comentario;
    private int idColaborador;

    public HistorialEnvio() {
    }

    public HistorialEnvio(int idHistorial, int idEnvio, int idEstadoEnvio, Timestamp fechaCambio, String comentario, int idColaborador) {
        this.idHistorial = idHistorial;
        this.idEnvio = idEnvio;
        this.idEstadoEnvio = idEstadoEnvio;
        this.fechaCambio = fechaCambio;
        this.comentario = comentario;
        this.idColaborador = idColaborador;
    }

    public int getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(int idHistorial) {
        this.idHistorial = idHistorial;
    }

    public int getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(int idEnvio) {
        this.idEnvio = idEnvio;
    }

    public int getIdEstadoEnvio() {
        return idEstadoEnvio;
    }

    public void setIdEstadoEnvio(int idEstadoEnvio) {
        this.idEstadoEnvio = idEstadoEnvio;
    }

    public Timestamp getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(Timestamp fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getIdColaborador() {
        return idColaborador;
    }

    public void setIdColaborador(int idColaborador) {
        this.idColaborador = idColaborador;
    }
    
}
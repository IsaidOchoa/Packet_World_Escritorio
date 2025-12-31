package clienteescritoriopw.pojo;

public class EstadoEnvio {

    private int idEstadoEnvio;
    private String nombre;

    public EstadoEnvio() {
    }

    public EstadoEnvio(int idEstadoEnvio, String nombre) {
        this.idEstadoEnvio = idEstadoEnvio;
        this.nombre = nombre;
    }

    public int getIdEstadoEnvio() {
        return idEstadoEnvio;
    }

    public void setIdEstadoEnvio(int idEstadoEnvio) {
        this.idEstadoEnvio = idEstadoEnvio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    
}

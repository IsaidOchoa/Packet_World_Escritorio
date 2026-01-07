package dto;

public class ValidacionDuplicadoCliente {
    private String correo;
    private String telefono;
    private Integer idClienteExcluir;

    public ValidacionDuplicadoCliente() {
    }

    public ValidacionDuplicadoCliente(String correo, String telefono, Integer idClienteExcluir) {
        this.correo = correo;
        this.telefono = telefono;
        this.idClienteExcluir = idClienteExcluir;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getIdClienteExcluir() {
        return idClienteExcluir;
    }

    public void setIdClienteExcluir(Integer idClienteExcluir) {
        this.idClienteExcluir = idClienteExcluir;
    }
    
    
}

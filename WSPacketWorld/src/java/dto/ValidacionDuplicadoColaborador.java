package dto;

public class ValidacionDuplicadoColaborador {
    private String numeroPersonal;
    private String curp;
    private String correo;
    private String numeroLicencia;
    private Integer idColaboradorExcluir;

    public ValidacionDuplicadoColaborador() {
    }

    public ValidacionDuplicadoColaborador(String numeroPersonal, String curp, String correo, String numeroLicencia, Integer idColaboradorExcluir) {
        this.numeroPersonal = numeroPersonal;
        this.curp = curp;
        this.correo = correo;
        this.numeroLicencia = numeroLicencia;
        this.idColaboradorExcluir = idColaboradorExcluir;
    }

    public String getNumeroPersonal() {
        return numeroPersonal;
    }

    public void setNumeroPersonal(String numeroPersonal) {
        this.numeroPersonal = numeroPersonal;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNumeroLicencia() {
        return numeroLicencia;
    }

    public void setNumeroLicencia(String numeroLicencia) {
        this.numeroLicencia = numeroLicencia;
    }

    public Integer getIdColaboradorExcluir() {
        return idColaboradorExcluir;
    }

    public void setIdColaboradorExcluir(Integer idColaboradorExcluir) {
        this.idColaboradorExcluir = idColaboradorExcluir;
    }
    
    
}
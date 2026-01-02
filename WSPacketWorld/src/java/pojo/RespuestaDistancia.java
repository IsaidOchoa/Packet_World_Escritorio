package pojo;

public class RespuestaDistancia {
    private Double distanciaKM;
    private boolean error;
    private String mensaje;

    public RespuestaDistancia() {
    }

    public RespuestaDistancia(Double distanciaKM, boolean error, String mensaje) {
        this.distanciaKM = distanciaKM;
        this.error = error;
        this.mensaje = mensaje;
    }

    public Double getDistanciaKM() {
        return distanciaKM;
    }

    public void setDistanciaKM(Double distanciaKM) {
        this.distanciaKM = distanciaKM;
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

    
}
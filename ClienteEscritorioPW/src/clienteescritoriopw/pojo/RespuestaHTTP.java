package clienteescritoriopw.pojo;

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
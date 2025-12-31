package clienteescritoriopw.pojo;

public class TipoUnidad {
  
    private int idTipoUnidad;
    private String nombre;

    public TipoUnidad() {
    }

    public TipoUnidad(int idTipoUnidad, String nombre) {
        this.idTipoUnidad = idTipoUnidad;
        this.nombre = nombre;
    }

    public int getIdTipoUnidad() {
        return idTipoUnidad;
    }

    public void setIdTipoUnidad(int idTipoUnidad) {
        this.idTipoUnidad = idTipoUnidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

   
}


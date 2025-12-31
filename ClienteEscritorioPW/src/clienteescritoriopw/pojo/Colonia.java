package clienteescritoriopw.pojo;

public class Colonia {
    
    private Integer idColonia;
    private String nombre;
    private String codigoPostal;
    private Integer idMunicipio;
    private String nombreMunicipio;
    private String nombreEstado;
    private Integer idEstado; 

    public Colonia() {
    }

    public Colonia(Integer idColonia, String nombre, String codigoPostal, Integer idMunicipio, String nombreMunicipio, String nombreEstado, Integer idEstado) {
        this.idColonia = idColonia;
        this.nombre = nombre;
        this.codigoPostal = codigoPostal;
        this.idMunicipio = idMunicipio;
        this.nombreMunicipio = nombreMunicipio;
        this.nombreEstado = nombreEstado;
        this.idEstado = idEstado;
    }

    public Integer getIdColonia() {
        return idColonia;
    }

    public void setIdColonia(Integer idColonia) {
        this.idColonia = idColonia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public Integer getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    public void setNombreMunicipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    
}

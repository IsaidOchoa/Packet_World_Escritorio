package ws;

import com.google.gson.Gson;
import dominio.ColaboradorImp;
import dto.RSAutenticacionColaborador;
import dto.Respuesta;
import dto.ValidacionDuplicadoColaborador;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import pojo.Colaborador;

@Path("colaborador")
public class ColaboradorWS {

    @Path("obtener-todos")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Colaborador> obtenerTodos() {
        return ColaboradorImp.obtenerColaboradores();
    }

    @Path("por-sucursal/{idSucursal}/conductores")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Colaborador> obtenerConductoresPorSucursal(@PathParam("idSucursal") Integer idSucursal) {
        if (idSucursal == null || idSucursal <= 0) {
            return new ArrayList<>();
        }
        return ColaboradorImp.obtenerConductoresPorSucursal(idSucursal);
    }

    @Path("registrar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta registrar(String json) {
        Gson gson = new Gson();
        Respuesta resp = new Respuesta();
        try {
            Colaborador c = gson.fromJson(json, Colaborador.class);
            
            if (c.getNombre() != null && !c.getNombre().isEmpty() &&
                c.getNumeroPersonal() != null && !c.getNumeroPersonal().isEmpty() &&
                c.getPassword() != null && !c.getPassword().isEmpty() &&
                c.getIdRol() > 0) {
                
                return ColaboradorImp.registrar(c);
            } else {
                resp.setError(true);
                resp.setMensaje("Faltan datos (Nombre, No. Personal, Contraseña o Rol).");
            }
        } catch (Exception e) {
            resp.setError(true);
            resp.setMensaje("Error al procesar el registro: " + e.getMessage());
        }
        return resp;
    }

    // =============== PERFIL ===============
    @PUT
    @Path("editar-perfil")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta editarPerfil(String json) {
        Gson gson = new Gson();
        Colaborador colab = gson.fromJson(json, Colaborador.class);

        if (colab.getIdColaborador() <= 0) {
            return new Respuesta(true, "ID de colaborador requerido.");
        }

        return ColaboradorImp.editarPerfil(colab);
    }

    // =============== FOTO ===============
    @PUT
    @Path("editar-foto")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta actualizarFoto(String json) {
        Gson gson = new Gson();
        Colaborador colab = gson.fromJson(json, Colaborador.class);

        if (colab.getIdColaborador() <= 0 ||
        colab.getFotoBase64() == null || colab.getFotoBase64().isEmpty()) {
        return new Respuesta(true, "ID y fotoBase64 son requeridos.");
    }

        return ColaboradorImp.actualizarFoto(colab);
    }

    // =============== CONTRASEÑA ===============
    @PUT
    @Path("editar-password")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta cambiarPassword(String json) {
        Gson gson = new Gson();
        Map<String, Object> datos = gson.fromJson(json, Map.class);

        try {
            Integer id = ((Double) datos.get("idColaborador")).intValue();
            String passActual = (String) datos.get("passwordActual");
            String passNueva = (String) datos.get("passwordNueva");

            if (id == null || passActual == null || passNueva == null) {
                return new Respuesta(true, "Faltan datos: idColaborador, passwordActual, passwordNueva.");
            }

            return ColaboradorImp.cambiarPassword(id, passActual, passNueva);
        } catch (Exception e) {
            return new Respuesta(true, "Error en los datos enviados.");
        }
    }

    @Path("eliminar/{idColaborador}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Respuesta eliminar(@PathParam("idColaborador") Integer idColaborador) {
        Respuesta resp = new Respuesta();
        try {
            if (idColaborador != null && idColaborador > 0) {
                return ColaboradorImp.eliminar(idColaborador);
            }
            resp.setError(true);
            resp.setMensaje("ID inválido.");
        } catch (Exception e) {
            resp.setError(true);
            resp.setMensaje("Error al eliminar: " + e.getMessage());
        }
        return resp;
    }
    
    @Path("buscar/{filtro}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Colaborador> buscar(@PathParam("filtro") String filtro) {
        return ColaboradorImp.buscarColaborador(filtro);
    }
    
    @Path("perfil/{idColaborador}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Colaborador obtenerPerfilCompleto(@PathParam("idColaborador") Integer idColaborador) {
        if (idColaborador == null || idColaborador <= 0) {
            return null; // O podrías lanzar una excepción, pero JAX-RS convertirá null a 204/404
        }
        return ColaboradorImp.obtenerPorIdCompleto(idColaborador);
    }
    
    @Path("validar-duplicados")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Respuesta validarDuplicadosColaborador(String json) {
        Gson gson = new Gson();
        try {
            ValidacionDuplicadoColaborador datos = gson.fromJson(json, ValidacionDuplicadoColaborador.class);
            return ColaboradorImp.validarDuplicadosColaborador(datos);
        } catch (Exception e) {
            Respuesta error = new Respuesta();
            error.setError(true);
            error.setMensaje("Error al procesar la validación: " + e.getMessage());
            return error;
        }
    }
    
    @POST
    @Path("login-movil")
    @Produces(MediaType.APPLICATION_JSON)
    public RSAutenticacionColaborador loginMovil(@FormParam("noPersonal") String noPersonal,
                                               @FormParam("password") String password) {
        RSAutenticacionColaborador respuesta = new RSAutenticacionColaborador();

        if (noPersonal == null || password == null) {
            respuesta.setError(true);
            respuesta.setMensaje("Datos incompletos");
            return respuesta;
        }

        try {
            // Llama al método que NO trae la foto
            Colaborador colaborador = ColaboradorImp.loginMovil(noPersonal, password);

            if (colaborador != null && colaborador.getIdColaborador() > 0) {
                // Verifica el rol aquí mismo para mayor seguridad
                if ("Conductor".equalsIgnoreCase(colaborador.getRol())) {
                    respuesta.setColaborador(colaborador);
                    respuesta.setError(false);
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("Acceso denegado: solo para conductores.");
                }
            } else {
                respuesta.setError(true);
                respuesta.setMensaje("Credenciales incorrectas.");
            }
        } catch (Exception e) {
            respuesta.setError(true);
            respuesta.setMensaje("Error interno del servidor.");
            e.printStackTrace();
        }

        return respuesta;
    }
}
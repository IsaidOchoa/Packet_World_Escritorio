package dominio;

import dto.Respuesta;
import dto.ValidacionDuplicadoCliente;
import java.util.List;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Cliente;

public class ClienteImp {
    

    public static List<Cliente> obtenerClientes() {
        List<Cliente> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if(conexionBD != null){
            try { lista = conexionBD.selectList("cliente.obtenerTodos"); } 
            catch(Exception e){ e.printStackTrace(); } 
            finally{ conexionBD.close(); }
        }
        return lista;
    }

    public static Respuesta registrar(Cliente cliente){
        Respuesta msj = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();
        if(conexionBD != null){
            try {
                int filas = conexionBD.insert("cliente.registrar", cliente);
                conexionBD.commit();
                if(filas > 0){
                    msj.setError(false);
                    msj.setMensaje("Cliente registrado");
                }else{
                    msj.setError(true);
                    msj.setMensaje("No se pudo registrar");
                }
            } catch(Exception e){
                msj.setError(true);
                msj.setMensaje("Error: " + e.getMessage());
            } finally{ conexionBD.close(); }
        }
        return msj;
    }
    
    public static Respuesta editar(Cliente cliente){
    Respuesta msj = new Respuesta();
    SqlSession conexion = MyBatisUtil.getSession();
    if(conexion!=null){
        try{
            int filas = conexion.update("cliente.editar", cliente);
            conexion.commit();
            msj.setError(filas <= 0);
            msj.setMensaje(filas > 0 ? "Cliente editado" : "No encontrado");
        }catch(Exception e){ msj.setError(true); msj.setMensaje("Error: "+e.getMessage()); }
        finally{ conexion.close(); }
    }
    return msj;
}

    public static Respuesta eliminar(int idCliente){
    Respuesta msj = new Respuesta();
    SqlSession conexion = MyBatisUtil.getSession();
    if(conexion!=null){
        try{
            int filas = conexion.delete("cliente.eliminar", idCliente);
            conexion.commit();
            msj.setError(filas <= 0);
            msj.setMensaje(filas > 0 ? "Cliente eliminado" : "No encontrado");
        }catch(Exception e){ msj.setError(true); msj.setMensaje("Error (tiene envíos?): "+e.getMessage()); }
        finally{ conexion.close(); }
    }
    return msj;
}

    public static List<Cliente> buscar(String filtro){
        List<Cliente> lista = null;
        SqlSession conexion = MyBatisUtil.getSession();
        if(conexion!=null){
            try{ lista = conexion.selectList("cliente.buscarPorNombre", "%"+filtro+"%"); }
            catch(Exception e){ e.printStackTrace(); }
            finally{ conexion.close(); }
        }
        return lista;
    }
    
    public static Respuesta validarDuplicadosCliente(ValidacionDuplicadoCliente datos) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexionBD = MyBatisUtil.getSession();

        if (conexionBD != null) {
            try {
                StringBuilder errores = new StringBuilder();

                // Verificar correo
                if (datos.getCorreo() != null && !datos.getCorreo().isEmpty()) {
                    Cliente porCorreo = conexionBD.selectOne("cliente.buscarPorCorreo", datos.getCorreo());
                    if (porCorreo != null && 
                        (datos.getIdClienteExcluir() == null || 
                         !porCorreo.getIdCliente().equals(datos.getIdClienteExcluir()))) {
                        errores.append("Ya existe un cliente con este correo electrónico.\n");
                    }
                }

                // Verificar teléfono
                if (datos.getTelefono() != null && !datos.getTelefono().isEmpty()) {
                    Cliente porTelefono = conexionBD.selectOne("cliente.buscarPorTelefono", datos.getTelefono());
                    if (porTelefono != null && 
                        (datos.getIdClienteExcluir() == null || 
                         !porTelefono.getIdCliente().equals(datos.getIdClienteExcluir()))) {
                        errores.append("Ya existe un cliente con este número telefónico.\n");
                    }
                }

                if (errores.length() > 0) {
                    respuesta.setError(true);
                    respuesta.setMensaje(errores.toString().trim());
                } else {
                    respuesta.setError(false);
                    respuesta.setMensaje("Validación exitosa.");
                }

            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error en la validación: " + e.getMessage());
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Error de conexión a la base de datos.");
        }
        return respuesta;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominio;

import dto.Respuesta;
import java.util.List;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Cliente;

/**
 *
 * @author pepeg
 */
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
}

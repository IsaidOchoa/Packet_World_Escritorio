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
import pojo.Envio;
import pojo.Paquete;
import pojo.Sucursal;
import utilidades.CalculadoraEnvios;

/**
 *
 * @author pepeg
 */
public class PaqueteImp {

   public static Respuesta registrar(Paquete paquete) {
    Respuesta respuesta = new Respuesta();
    respuesta.setError(true);
    
    SqlSession conexion = MyBatisUtil.getSession();
    boolean registroExitoso = false; 

    if (conexion != null) {
        try {
            int filasAfectadas = conexion.insert("paquete.registrar", paquete);
            conexion.commit();
            
            if (filasAfectadas > 0) {
                respuesta.setError(false);
                respuesta.setMensaje("Paquete registrado correctamente.");
                registroExitoso = true;
            } else {
                respuesta.setMensaje("No se pudo registrar el paquete.");
            }
        } catch (Exception e) {
            respuesta.setMensaje("Error BD: " + e.getMessage());
        } finally {
            // 1. Cerramos la conexión AQUÍ para liberar el recurso
            conexion.close();
        }
    } else {
        respuesta.setMensaje("Error de conexión BD.");
    }
    
    // 2. Llamamos a recalcular AFUERA del bloque try/finally anterior
    if (registroExitoso && paquete.getIdEnvio() != null) {
        try {
            EnvioImp.recalcularCosto(paquete.getIdEnvio());
        } catch(Exception ex) {
            System.err.println("Error invocando recalcularCosto: " + ex.getMessage());
        }
    }
    
    return respuesta;
}


    public static List<Paquete> obtenerPorEnvio(int idEnvio) {
        List<Paquete> lista = null;
        SqlSession conexion = MyBatisUtil.getSession();
        
        if (conexion != null) {
            try {
                lista = conexion.selectList("paquete.obtenerPorEnvio", idEnvio);
            } catch (Exception e) {
                e.printStackTrace(); 
            } finally {
                conexion.close();
            }
        }
        return lista;
    }
    
    public static Respuesta editar(Paquete paquete) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);
        
        SqlSession conexion = MyBatisUtil.getSession();
        
        if (conexion != null) {
            try {
                int filasAfectadas = conexion.update("paquete.editar", paquete);
                conexion.commit();
                
                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Información del paquete editada correctamente.");
                } else {
                    respuesta.setMensaje("No se pudo editar la información.");
                }
            } catch (Exception e) {
                respuesta.setMensaje("Error al editar: " + e.getMessage());
            } finally {
                conexion.close();
            }
        } else {
            respuesta.setMensaje("Error de conexión con la base de datos.");
        }
        
        return respuesta;
    }
    public static Respuesta eliminar(int idPaquete) {
        Respuesta respuesta = new Respuesta();
        SqlSession conexion = MyBatisUtil.getSession();
        
        if (conexion != null) {
            try {
                int filasAfectadas = conexion.delete("paquete.eliminar", idPaquete);
                conexion.commit();
                
                if (filasAfectadas > 0) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Paquete eliminado del envío.");
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("No se encontró el paquete.");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al eliminar: " + e.getMessage());
            } finally {
                conexion.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Error de conexión a la BD.");
        }
        
        return respuesta;
    }
}


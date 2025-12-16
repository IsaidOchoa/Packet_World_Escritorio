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
import pojo.Sucursal;

/**
 *
 * @author pepeg
 */
public class SucursalImp {
    
    public static List<Sucursal> obtenerTodas() {
        List<Sucursal> lista = null;
        SqlSession conexion = MyBatisUtil.getSession();
        
        if (conexion != null) {
            try {
                lista = conexion.selectList("sucursal.obtenerTodas");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conexion.close();
            }
        }
        return lista;
    }

    public static Respuesta registrar(Sucursal sucursal) {
        Respuesta resp = new Respuesta();
        resp.setError(true);
        SqlSession conexion = MyBatisUtil.getSession();
        
        if (conexion != null) {
            try {
                int filas = conexion.insert("sucursal.registrar", sucursal);
                conexion.commit();
                
                if (filas > 0) {
                    resp.setError(false);
                    resp.setMensaje("Sucursal registrada correctamente.");
                } else {
                    resp.setMensaje("No se pudo registrar la sucursal.");
                }
            } catch (Exception e) {
                resp.setMensaje("Error al guardar: " + e.getMessage());
            } finally {
                conexion.close();
            }
        } else {
            resp.setMensaje("Sin conexión a la BD.");
        }
        return resp;
    }

    public static Respuesta editar(Sucursal sucursal) {
        Respuesta resp = new Respuesta();
        resp.setError(true);
        SqlSession conexion = MyBatisUtil.getSession();
        
        if (conexion != null) {
            try {
                int filas = conexion.update("sucursal.editar", sucursal);
                conexion.commit();
                
                if (filas > 0) {
                    resp.setError(false);
                    resp.setMensaje("Sucursal editada correctamente.");
                } else {
                    resp.setMensaje("No se pudo editar la sucursal.");
                }
            } catch (Exception e) {
                resp.setMensaje("Error al editar: " + e.getMessage());
            } finally {
                conexion.close();
            }
        } else {
            resp.setMensaje("Sin conexión a la BD.");
        }
        return resp;
    }

   public static Respuesta eliminar(int idSucursal) {
    Respuesta resp = new Respuesta();
    SqlSession conexion = MyBatisUtil.getSession();
    if (conexion != null) {
        try {
            // Llama al UPDATE estatus = 0 del Mapper
            int filas = conexion.update("sucursal.eliminar", idSucursal);
            conexion.commit();
            if (filas > 0) {
                resp.setError(false);
                resp.setMensaje("Sucursal dada de baja correctamente.");
            } else {
                resp.setError(true);
                resp.setMensaje("No se encontró la sucursal.");
            }
        } catch (Exception e) {
            resp.setError(true);
            resp.setMensaje("Error: " + e.getMessage());
        } finally {
            conexion.close();
        }
    }
    return resp;
}
}   


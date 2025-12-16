/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominio;

import java.util.List;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.EstadoEnvio;
import pojo.Rol;
import pojo.TipoUnidad;

/**
 *
 * @author pepeg
 */
public class CatalogoImp {
 
  
    public static List<Rol> obtenerRoles(){
        List<Rol> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if(conexionBD != null){
            try{
                lista = conexionBD.selectList("catalogo.obtenerRoles");
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                conexionBD.close();
            }
        }
        return lista;
    }
    
    public static List<TipoUnidad> obtenerTiposUnidad(){
        List<TipoUnidad> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if(conexionBD != null){
            try{
                lista = conexionBD.selectList("catalogo.obtenerTiposUnidad");
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                conexionBD.close();
            }
        }
        return lista;
    }
    
    public static List<EstadoEnvio> obtenerEstatusEnvio(){
        List<EstadoEnvio> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if(conexionBD != null){
            try{
                lista = conexionBD.selectList("catalogo.obtenerEstatusEnvio");
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                conexionBD.close();
            }
        }
        return lista;
    }
}

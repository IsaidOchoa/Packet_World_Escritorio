/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominio;

import java.util.List;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Colonia;
import pojo.Estado;
import pojo.Municipio;

/**
 *
 * @author pepeg
 */
public class DireccionImp {
  
    
    public static List<Estado> obtenerEstados() {
        List<Estado> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                lista = conexionBD.selectList("direccion.obtenerEstados");
            } catch (Exception e) { e.printStackTrace(); } 
            finally { conexionBD.close(); }
        }
        return lista;
    }
    
    public static List<Municipio> obtenerMunicipios(int idEstado) {
        List<Municipio> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                lista = conexionBD.selectList("direccion.obtenerMunicipios", idEstado);
            } catch (Exception e) { e.printStackTrace(); } 
            finally { conexionBD.close(); }
        }
        return lista;
    }
    
    public static List<Colonia> obtenerColonias(int idMunicipio) {
        List<Colonia> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                lista = conexionBD.selectList("direccion.obtenerColonias", idMunicipio);
            } catch (Exception e) { e.printStackTrace(); } 
            finally { conexionBD.close(); }
        }
        return lista;
    }
    
    public static List<Colonia> buscarPorCP(String codigoPostal) {
        List<Colonia> lista = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                lista = conexionBD.selectList("direccion.buscarPorCP", codigoPostal);
            } catch (Exception e) { e.printStackTrace(); } 
            finally { conexionBD.close(); }
        }
        return lista;
    }
    public static Municipio obtenerMunicipio(int idMunicipio) {
        Municipio municipio = null;
        SqlSession conexionBD = MyBatisUtil.getSession();
        if (conexionBD != null) {
            try {
                municipio = conexionBD.selectOne("direccion.obtenerMunicipio", idMunicipio);
            } catch (Exception e) { 
                e.printStackTrace(); 
            } finally { 
                conexionBD.close(); 
            }
        }
        return municipio;
    }
}


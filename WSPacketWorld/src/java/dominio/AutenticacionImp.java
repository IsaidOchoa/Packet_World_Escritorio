/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominio;

import dto.RSAutenticacionColaborador;
import java.util.HashMap;
import java.util.LinkedHashMap;
import modelo.mybatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import pojo.Colaborador;

/**
 *
 * @author pepeg
 */
public class AutenticacionImp {
    
    public static RSAutenticacionColaborador autenticacionColaborador(String noPersonal, String password) {
        RSAutenticacionColaborador respuesta = new RSAutenticacionColaborador();
        SqlSession conexionBD = MyBatisUtil.getSession();
        
        if (conexionBD != null) {
            try {
                HashMap<String, Object> parametros = new LinkedHashMap<>();
                
                
                parametros.put("numeroPersonal", noPersonal); 
                parametros.put("password", password);
                
                Colaborador colaborador = conexionBD.selectOne("autenticacion.loginColaborador", parametros);
                
                if (colaborador != null) {
                    respuesta.setError(false);
                    respuesta.setMensaje("Bienvenido " + colaborador.getNombre());
                    respuesta.setColaborador(colaborador);
                } else {
                    respuesta.setError(true);
                    respuesta.setMensaje("Número de personal y/o contraseña incorrectos");
                }
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error: " + e.getMessage());
                e.printStackTrace(); 
            } finally {
                conexionBD.close();
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje("Por el momento no hay conexión a la base de datos");
        }
        
        return respuesta;
    }
}
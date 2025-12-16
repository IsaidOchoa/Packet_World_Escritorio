/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.mybatis;

import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 *
 * @author pepeg
 */
public class MyBatisUtil {
    private static final String RESOURCE ="modelo/mybatis/mybatis-config.xml";
    private static final String ENVIROMENT = "desarrollo";
    
    public static SqlSession getSession() {
    SqlSession session = null ;
        try {
            Reader reader = Resources.getResourceAsReader(RESOURCE);
            SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader, ENVIROMENT);
            session = sqlMapper.openSession();
        } catch (Exception e) {
        }
        return session;
}
}

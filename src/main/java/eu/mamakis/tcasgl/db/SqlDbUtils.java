/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.db;

import eu.mamakis.tcasgl.utils.*;
import java.sql.*;

/**
 *
 * @author gmamakis
 */
public class SqlDbUtils {

   static Connection con;
   
   
 
    public static void connectSql(String db) throws SQLException, ClassNotFoundException {
     
      Database cm = new  Database(db);
        con =cm.getConnection();

    }

    public static void disconnectSQL() throws Exception {
        con.close();
    }

   
    public static Connection getConnection() {
        
        return con;
    }

    

    public static void executeInsert(String query) throws SQLException{
          Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        stmt.executeUpdate(query);
    }
    public static void executePreparedInsert(String query,String... value) throws SQLException {
      
        
        PreparedStatement ps = con.prepareStatement(query);
       for (int i=0;i<value.length;i++){
        ps.setString(i+1, value[i]);
    }
        ps.executeUpdate();
        
    }

    public static ResultSet executeSelect(String query) throws SQLException {
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return stmt.executeQuery(query);
    }
    
  

}

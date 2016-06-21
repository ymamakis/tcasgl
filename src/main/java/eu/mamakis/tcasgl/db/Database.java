/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author gmamakis
 */
public class Database {
    private DataSource dataSource;

public Database(String jndiname) {
    try {
        dataSource = (DataSource) new InitialContext().lookup("java:comp/env/" + jndiname);
    } catch (NamingException e) {
        // Handle error that it's not configured in JNDI.
        throw new RuntimeException(jndiname + " is missing in JNDI!", e);
    }
}

public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
}
}

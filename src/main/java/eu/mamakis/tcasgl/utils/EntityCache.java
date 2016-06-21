/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.utils;

import eu.mamakis.tcasgl.db.Database;
import eu.mamakis.tcasgl.db.SqlDbUtils;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gmamakis
 */
public class EntityCache {
    
    private final static String TABLE_NAME="TABLE_NAME";
    private static EntityCache entityCache;
    
    private Map<String,Map<String,Double>> category;

    public Map<String, Map<String, Double>> getCategory() {
        return category;
    }
    
    
    
    private EntityCache(){
       
        
        try {
            SqlDbUtils.connectSql("jdbc/cognitive");
            DatabaseMetaData mData = SqlDbUtils.getConnection().getMetaData();
            ResultSet rs = mData.getTables(null, null, null, null);
            while(rs.next()){
                if(!rs.getString(TABLE_NAME).equalsIgnoreCase("totals")&&!rs.getString(TABLE_NAME).startsWith("SYS")){
                    ResultSet resTotal = SqlDbUtils.executeSelect("Select * from "+rs.getString(TABLE_NAME));
                    while(resTotal.next()){
                        if(category==null){
                            category = new HashMap<String, Map<String, Double>>();
                        }
                        if(category.get(rs.getString(TABLE_NAME))==null){
                            category.put(rs.getString(TABLE_NAME), new HashMap<String,Double>());
                        }
                        Map<String,Double> wordMap = category.get(rs.getString(TABLE_NAME));
                        wordMap.put(resTotal.getString("word"),resTotal.getDouble("ai"));
                        category.put(rs.getString(TABLE_NAME),wordMap);
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EntityCache.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EntityCache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static EntityCache getInstance(){
        if (entityCache==null){
           entityCache = new EntityCache();
            
        }
        
        return entityCache;
    }
}

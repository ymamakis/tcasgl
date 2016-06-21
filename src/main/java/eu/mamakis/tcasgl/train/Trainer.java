/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.train;

import java.util.List;

/**
 *
 * @author gmamakis
 */
public interface Trainer {
    
    public long train(String... params);

    public String getDB();
    
    public List<String> getClasses();
    
    public String getWordField();
    
    public String getTfField();
    
    public String getDfField();
    
    public String getStatisticsQuery();
    
    public String getStatisticsCategoryField();
    
    public String getStatisticsWordsField();
    
    public String getDriver();
    
    public String getUsername();
    
    public String getPassword();

}

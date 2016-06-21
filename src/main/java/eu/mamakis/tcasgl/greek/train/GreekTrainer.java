/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.greek.train;

import java.sql.Clob;
import eu.mamakis.tcasgl.db.SqlDbUtils;
import eu.mamakis.tcasgl.greek.GreekPreProcessor;
import eu.mamakis.tcasgl.greek.GreekStemmer;
import eu.mamakis.tcasgl.train.Trainer;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 *
 * @author gmamakis
 */


public class GreekTrainer implements Trainer{

 private final static String LANGUAGE="jdbc/language";
    
    private final static String COGNITIVE = "jdbc/cognitive";
    
    private final static String TOTALS = "totals";
    private final static String TOTALSTABLE = "create table totals (id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), category varchar(40), words DOUBLE)";
    private final String ARTICLE = "article";
   
    private final static String DRIVER= "org.apache.derby.jdbc.EmbeddedDriver";
    private final static String USERNAME="APP";
    private final static String PASSWORD="APP";
    private List<String>classQueries;
    private void train(String documentPath, String suffix) {
        createDocumentDB(documentPath,suffix);
    }
   
  
    private void createDocumentDB(String documentPath,String suffix) {
        try {
            
            SqlDbUtils.connectSql(COGNITIVE);
            SqlDbUtils.executeInsert("DROP TABLE totals");
            File dir = new File(documentPath);
            
            classQueries = new ArrayList<String>(); 
            for (File f : dir.listFiles()) {
                SqlDbUtils.connectSql(LANGUAGE);
                
                String domain = StringUtils.substringBefore(f.getName(), suffix);
                try {
                    try{
                    String drop = "DROP TABLE "+domain;
                    SqlDbUtils.executeInsert(drop);
                    }catch (Exception e){
                        
                    }
                    String insertSQL = "CREATE TABLE " + domain + " (id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), article clob(50000))";
                    SqlDbUtils.executeInsert(insertSQL);
                    
                    classQueries.add(domain);
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document catDoc = db.parse(new File(documentPath+"/"+f.getName()));
                    Element root = catDoc.getDocumentElement();
                    NodeList articleList = root.getElementsByTagName("article");
                    int k = 0;
                    while (k < articleList.getLength()) {
                        
                        String article = articleList.item(k).getFirstChild().getNodeValue();
                        article = article.replaceAll("\\'", "\\?");
                        article = article.replaceAll(";","?");
                        String articleSQL = "INSERT INTO " + domain + "(article) VALUES(?)";
                        
                        SqlDbUtils.executePreparedInsert(articleSQL,article);
                        
                      
                        k++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                  
                 
                Map<String, Word> domainWords = computeWordAppearances(domain);
                generateWordDatabase(domainWords, domain);
          
                
            }



            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                SqlDbUtils.disconnectSQL();
            } catch (Exception ex) {
                Logger.getLogger(SqlDbUtils.class.getName()).log(Level.SEVERE, null, ex);
            }


        }

    }

    private void generateWordDatabase(Map<String, Word> words, String domain) {

        try {
            try {
                SqlDbUtils.connectSql(COGNITIVE);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GreekTrainer.class.getName()).log(Level.SEVERE, null, ex);
            }
           try{
                 SqlDbUtils.executeInsert(TOTALSTABLE);
           } catch (Exception e){
               
           }
           
            try{
                    String drop = "DROP TABLE "+domain;
                    SqlDbUtils.executeInsert(drop);
                    }catch (Exception e){
                        
                    }
            String domainTable = "create table " + domain
                    + " (id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), word varchar(100) NOT NULL, ai DOUBLE NOT NULL, doc DOUBLE NOT NULL)";

            SqlDbUtils.executeInsert(domainTable);
            
         

            for (Map.Entry<String, Word> word : words.entrySet()) {

                String stem = word.getValue().getWord();

               
                String insertWord = "Insert into " + domain + "(word,ai,doc) VALUES('" + stem + "'," + word.getValue().getTf() + "," + word.getValue().getDf() + ")";
                SqlDbUtils.executeInsert(insertWord);

            }

            String insertTotal = "Insert into totals(category,words) VALUES('" + domain + "'," + words.size() + ")";
           
            SqlDbUtils.executeInsert(insertTotal);
            
            

        } catch (SQLException e) {
            e.printStackTrace();
        
        }
    }

    
    private Map<String, Word> computeWordAppearances(String domain) {
        Map<String, Word> words = new HashMap<String, Word>();
        try {
            SqlDbUtils.connectSql(LANGUAGE);
            
          
            
            ResultSet rs = SqlDbUtils.executeSelect("SELECT * FROM " + domain);
            rs.beforeFirst();
            while (rs.next()) {
                Clob article =  rs.getClob(ARTICLE);
                Map<String, Word> articleWords = new HashMap<String, Word>();
               
              
            
                String[] wordArr = new GreekPreProcessor().repairString( IOUtils.toString(article.getCharacterStream())).split(" ");
               
                for (String wordStr : wordArr) {
                    
                            String stemmedWordStr =new GreekStemmer().stem(wordStr);
                            Word word;
                            if(stemmedWordStr!=null){
                            if (articleWords.containsKey(stemmedWordStr)) {
                                word = articleWords.get(stemmedWordStr);

                            } else {
                                word = new Word(stemmedWordStr);
                            }
                            word.setTf(word.getTf() + 1);
                            articleWords.put(stemmedWordStr, word);
                            }
                }
                for (Map.Entry<String, Word> articleWord : articleWords.entrySet()) {
                    Word word;
                    if (words.containsKey(articleWord.getKey())) {
                        word = words.get(articleWord.getKey());
                        word.setDf(word.getDf() + 1);
                        word.setTf(word.getTf() + articleWord.getValue().getTf());
                        words.put(articleWord.getKey(), word);
                    } else {
                        words.put(articleWord.getKey(), articleWord.getValue());
                    }

                }
            }
            return words;
        } catch (IOException ex) {
            Logger.getLogger(GreekTrainer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SqlDbUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SqlDbUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return words;
    }

    public long train(String... params) {
        long start = new Date().getTime();
        train(params[0],params[1]);
        return new Date().getTime()-start;
    }

    public String getDB() {
        return "cognitive";
    }

    //TODO:Check if needed
    public int getTotalNumberOfDocuments(){
        return 0;
    }
    
    public List<String> getClasses() {
        if(classQueries!=null){
            return classQueries;
        } else {
            classQueries = new ArrayList<String>();
            try {
                SqlDbUtils.connectSql(COGNITIVE);
                
                DatabaseMetaData dbmd = SqlDbUtils.getConnection().getMetaData();
            ResultSet rs = dbmd.getTables(null,null,null,null);
            //rs.beforeFirst();
                while(rs.next()){
                    if(!rs.getString("TABLE_NAME").equalsIgnoreCase(TOTALS)||!rs.getString("TABLE_NAME").startsWith("SYS")){
                    classQueries.add(rs.getString(1));
                    }
                }
                return classQueries;
           } catch (SQLException ex) {
                Logger.getLogger(GreekTrainer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GreekTrainer.class.getName()).log(Level.SEVERE, null, ex);
            } 
    }
        return null;
    }
    public String getWordField() {
        return "word";
    }

    public String getTfField() {
        return "ai";
    }

    public String getDfField() {
        return "doc";
    }

    public String getStatisticsQuery() {
        return "SELECT * FROM totals";
    }
    
    public String getStatisticsCategoryField(){
        return "category";
    }
    
    public String getStatisticsWordsField(){
        return "words";
    }
    
    public String getDriver(){
        return DRIVER;
    }
    
    public String getUsername(){
        return USERNAME;
    }
    
    public String getPassword(){
        return PASSWORD;
    }
}

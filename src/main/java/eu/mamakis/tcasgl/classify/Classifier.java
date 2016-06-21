
package eu.mamakis.tcasgl.classify;

import eu.mamakis.tcasgl.db.SqlDbUtils;
import eu.mamakis.tcasgl.train.PreProcessor;
import eu.mamakis.tcasgl.train.Stemmer;
import eu.mamakis.tcasgl.train.Trainer;
import eu.mamakis.tcasgl.utils.EntityCache;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author gmamakis
 */
public class Classifier {

    private Trainer trainer;
    private PreProcessor preProcessor;
    private static Map<String,Map<String,Double>> wordWeights;
    private static String category;
    private Stemmer stemmer;
    
    public Classifier(Trainer trainer, PreProcessor preProcessor, Stemmer stemmer) {
        this.trainer = trainer;
        this.preProcessor = preProcessor;
        this.stemmer =stemmer;
        wordWeights = new HashMap<String,Map<String, Double>>();
    }

    public String classify(String document) {
        String[] wordArr = preProcessor.repairString(document).split(" ");
      
                   
        Map<String, DocWord> docWords = new HashMap<String, DocWord>();
        for (String word : wordArr) {
            DocWord docWord;
            String stemmedWord = stemmer.stem(word); 
            if(stemmedWord!=null && stemmedWord.length()>0){
            if (docWords.containsKey(stemmedWord)) {
                docWord = docWords.get(stemmedWord);
            } else {
                docWord = new DocWord(stemmedWord);
            }
            docWord.setTf(docWord.getTf()+1);
            docWords.put(stemmedWord, docWord);
            }
        }
        try {

            SqlDbUtils.connectSql("jdbc/"+trainer.getDB());
            Map<String, Double> categoryFrequencies = createCategoryFrequencyMap(trainer.getStatisticsQuery(), trainer.getStatisticsCategoryField(), trainer.getStatisticsWordsField());
            
            List<Score> scores = new ArrayList<Score>();

            for (Entry<String, Double> cat : categoryFrequencies.entrySet()) {
                Score score = new Score();
                score.setCategory(cat.getKey());
                
                score.setScore(0d);
                scores.add(score);
            }
            List<Score> finalScores = calculateScores(docWords, categoryFrequencies, scores);
            Collections.sort(finalScores);
         
            
            category = finalScores.get(0).getCategory();
            return finalScores.get(0).getCategory();
        } catch (SQLException ex) {
            Logger.getLogger(Classifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Classifier.class.getName()).log(Level.SEVERE, null, ex);
        }



        return null;
    }

    private Map<String, Double> createCategoryFrequencyMap(String statisticsQuery, String statisticsCategoryField, String statisticsWordsField) {
        try {
           
            ResultSet rs = SqlDbUtils.executeSelect(statisticsQuery);
            rs.beforeFirst();
            Map<String, Double> categoryFrequencies = new HashMap<String, Double>();
            while (rs.next()) {
                categoryFrequencies.put(rs.getString(statisticsCategoryField), rs.getDouble(statisticsWordsField));
            }
            return categoryFrequencies;
        } catch (SQLException ex) {
            Logger.getLogger(Classifier.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }

    private List<Score> calculateScores(Map<String, DocWord> docWords, Map<String, Double> categoryFrequencies, List<Score> scores) {
        EntityCache cache = EntityCache.getInstance();
        for (Entry<String, DocWord> docWord : docWords.entrySet()) {
             Double totalInCategories=0d;
            for (Entry<String, Double> category : categoryFrequencies.entrySet()) {
               
                /*
                String sqlQuery = "SELECT * FROM " + category.getKey() + " WHERE " + trainer.getWordField() + "='" + docWord.getKey() + "'";
                try {
                    ResultSet rs = SqlDbUtils.executeSelect(sqlQuery);
                    rs.beforeFirst();
                    while (rs.next()) {
                              Double wordOccurrence = rs.getDouble(trainer.getTfField());
                              
                              totalInCategories += wordOccurrence*docWord.getValue().getTf()/category.getValue();
                              
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Classifier.class.getName()).log(Level.SEVERE, null, ex);
                }*/
              
                Map<String,Double> words = cache.getCategory().get(StringUtils.upperCase(category.getKey()));
               
                if(words.containsKey(docWord.getKey())){
                    totalInCategories += words.get(docWord.getKey())*docWord.getValue().getTf()/category.getValue();
                }
                
            }
           
            
            for (Entry<String, Double> category : categoryFrequencies.entrySet()) {
                Map<String,Double>words = cache.getCategory().get(StringUtils.upperCase(category.getKey()));
                Score score = getByName(scores, category.getKey());
                Map<String,Double>temp = new HashMap<String, Double>();
                if(words.containsKey(docWord.getKey())){
                Double categoryWeight = words.get(docWord.getKey())*docWord.getValue().getTf()/category.getValue();
                  temp.put(docWord.getValue().getWord(),categoryWeight/totalInCategories);
wordWeights.put(category.getKey(),temp);
 score.setScore(score.getScore()+(categoryWeight/totalInCategories));
                }
                  //                String sqlQuery = "SELECT * FROM " + category.getKey() + " WHERE " + trainer.getWordField() + "='" + docWord.getKey() + "'";
//                Double categoryWeight;
//                try {
//                    
//                    ResultSet rs = SqlDbUtils.executeSelect(sqlQuery);
//                    rs.beforeFirst();
//                    while (rs.next()) {
//                              Double wordOccurrence = rs.getDouble(trainer.getTfField());
//                              categoryWeight =  wordOccurrence*docWord.getValue().getTf()/category.getValue();
//                              Score score = getByName(scores, category.getKey());
//                              Map<String,Double>temp = new HashMap<String, Double>();
//                              temp.put(docWord.getValue().getWord(),categoryWeight/totalInCategories);
//                              wordWeights.put(category.getKey(),temp);
//                              score.setScore(score.getScore()+(categoryWeight/totalInCategories));
//                              
//                    }
//                } catch (SQLException ex) {
//                    Logger.getLogger(Classifier.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        }
            
        return scores;
    }

    private Score getByName(List<Score> scores, String scoreName) {
        for (Score score : scores) {
            if (score.getCategory().equalsIgnoreCase(scoreName)) {
                return score;
            }
        }
        return null;
    }

    public Map<String,Map<String,Double>> getWordWeights(){
        return wordWeights;
    }
    public String getCategory(){
        return category;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.summarize;

import eu.mamakis.tcasgl.classify.Classifier;
import eu.mamakis.tcasgl.train.PreProcessor;
import eu.mamakis.tcasgl.train.Stemmer;
import eu.mamakis.tcasgl.utils.SentenceDetector;
import eu.mamakis.tcasgl.utils.StringArrayUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author gmamakis
 */
public class Summarizer {
    private Classifier classifier;
    private Stemmer stemmer;
    private PreProcessor preprocessor;
    private List<String> documentSentences;
    private float percentage;
    public Summarizer(Classifier classifier, Stemmer stemmer,PreProcessor preprocessor,float percentage){
        this.classifier = classifier;
        this.stemmer=stemmer;
        this.preprocessor = preprocessor;
        this.percentage = percentage;
    }
    
    public String summarize(String document){
        List<Paragraph> paragraphs =splitIntoParagraphs(document);
        List<Sentence> sentences = constructSentences(paragraphs);
        int noOfSentences = Math.round(sentences.size()*percentage);
        if(noOfSentences<1){
            noOfSentences=1;
        }
        Collections.sort(sentences);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<noOfSentences;i++){
            Sentence sent = sentences.get(i);
            sb.append(documentSentences.get(sent.getGlobalIndex()));
            sb.append(". ");
        }
        return sb.toString();
    }
    
    
    
    
    private List<Paragraph> splitIntoParagraphs(String text) {
        
        List<Paragraph> pars = new ArrayList<Paragraph>();
        String[] paragraphArr = text.split("\\. \n");
        int i = 0;
        for (String paragraph : paragraphArr) {
            Paragraph par = new Paragraph();
            par.setIndex(i);
           //TODO:NPE edo
            List<String> sents = StringArrayUtils.toStringList(SentenceDetector.findSentences(paragraph));
            documentSentences.addAll(sents);
            par.setSentences(sents);
            
            pars.add(par);
        }
        return pars;
    }
    
    private double calculateMultiplier(double index, double maxSize) {
        return (maxSize - index) / maxSize;
    }

    private List<Sentence> constructSentences(List<Paragraph> paragraphs) {
        List<Sentence> documentTree = new ArrayList <Sentence>();
       int i=0;
        for(Paragraph paragraph:paragraphs){
            int j=0;
            for(String sentence : paragraph.getSentences()){
                sentence = preprocessor.repairString(sentence);
                String[] words = sentence.split(" ");
                List<String> stems = new ArrayList<String>();
                for(String word:words){
                    stems.add(stemmer.stem(word));
                }
                Sentence sent = new Sentence();
                sent.setGlobalIndex(i);
                sent.setLocalIndex(j);
                sent.setWords(stems);
                sent.setMultiplier(calculateMultiplier(j,paragraph.getSentences().size()));
                sent.setWeight(calculateWeight(sent.getMultiplier(),stems));
                documentTree.add(sent);
                j++;
                i++;
            }
        }
        return documentTree;
    }

    private double calculateWeight(double multiplier, List<String> stems) {
        double weight=0;
        for(String stem:stems){
            weight+=classifier.getWordWeights().get(classifier.getCategory()).get(stem);
        }
        return multiplier*weight/stems.size();
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.greek.train;

/**
 *
 * @author Yorgos Mamakis
 */
public class Word {
    //Word string
    private String word;
    //Term Freuency
    private double tf;
    //Document Frequency
    private double df;

    public Word(String str){
        this.word = str;
        this.tf=0;
        this.df=1;
    }
    
    public double getDf() {
        return df;
    }

    public void setDf(double df) {
        this.df = df;
    }

    public double getTf() {
        return tf;
    }

    public void setTf(double tf) {
        this.tf = tf;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
    
    
}

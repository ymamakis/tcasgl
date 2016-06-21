/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.classify;

/**
 *
 * @author gmamakis
 */
public class DocWord {
    
    public DocWord(String word){
        this.word=word;
    }
    
    private String word;
    
    private double tf;

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

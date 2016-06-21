/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.classify;

/**
 *
 * @author gmamakis
 */
public class Score implements Comparable<Score>{
    private String category;
    private Double score;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public int compareTo(Score o) {
       if (this.score>o.score){
           return -1;
       } else if(this.score<o.score){
           return 1;
       } 
       return 0;
    }
    
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.greek;

import eu.mamakis.tcasgl.greek.utils.GreekStopWord;
import eu.mamakis.tcasgl.train.Stemmer;
import eu.mamakis.tcasgl.utils.StringArrayUtils;

/**
 *
 * @author gmamakis
 */
public class GreekStemmer implements Stemmer{

    public String stem(String input) {
        if (!StringArrayUtils.contains(GreekStopWord.STOPWORDS, input)
                            || !StringArrayUtils.endsWith(GreekStopWord.VERB_ENDINGS, input)) {
                        if (StringArrayUtils.endsWith(GreekStopWord.NOUN_ENDINGS, input)) {
                            String stem = StringArrayUtils.stem(GreekStopWord.NOUN_ENDINGS, input);
                            return stem.length()>0?stem:null;
                        }
        }
        return null;
    }
    
}

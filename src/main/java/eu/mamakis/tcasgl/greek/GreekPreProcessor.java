/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.greek;

import eu.mamakis.tcasgl.greek.enums.Marks;
import eu.mamakis.tcasgl.greek.enums.PunctuatedChars;
import eu.mamakis.tcasgl.greek.utils.GreekSpecialTone;
import eu.mamakis.tcasgl.greek.utils.GreeklishReplacer;
import eu.mamakis.tcasgl.train.PreProcessor;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author gmamakis
 */
public class GreekPreProcessor implements PreProcessor{

    public static String WHITESPACE=" ";
    
    public String repairString(String input) {
         input = GreekSpecialTone.repairMarks(input);
         input = input.toLowerCase();
         input = GreeklishReplacer.replace(input);
          
           for (Marks mark : Marks.values()) {
                    input = input.replaceAll(mark.toString(), WHITESPACE);
                }
                for (PunctuatedChars chars : PunctuatedChars.values()) {
                    input = StringUtils.replaceChars(input, chars.getPunctuated(), chars.getNotPunctuated());
                }
                return input;
    }
    
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.greek.utils;

/**
 *
 * @author gmamakis
 */
public class GreeklishReplacer {
    
    public static String replace(String input){
        input = input.replaceAll("a", "α");
                input = input.replaceAll("b", "β");
                input = input.replaceAll("e", "ε");
                input = input.replaceAll("h", "η");
                input = input.replaceAll("i", "ι");
                input = input.replaceAll("k", "κ");
                input = input.replaceAll("m", "μ");
                input = input.replaceAll("n", "ν");
                input = input.replaceAll("o", "ο");
                input = input.replaceAll("p", "ρ");
                input = input.replaceAll("t", "τ");
                input = input.replaceAll("x", "χ");
                input = input.replaceAll("y", "υ");
                input = input.replaceAll("z", "ζ");
                return input;
    }
}

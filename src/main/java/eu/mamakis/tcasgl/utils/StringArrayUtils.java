/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mamakis.tcasgl.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author gmamakis
 */
public class StringArrayUtils {
    
    public static boolean contains(String[] arr, String str){
       
        for (String strCompare:arr){
            if(StringUtils.equals(str, strCompare)){
                return true;
            }
        }
        return false;
    }
    
    public static boolean endsWith(String[] arr,String str){
        if(StringUtils.endsWithAny(str, arr)){
            return true;
        }
        return false;
    }
    
    public static String stem (String[] arr, String str){
        for(String arrStr:arr){
            if(StringUtils.endsWith(str, arrStr)){
                return StringUtils.substringBefore(str, arrStr);
            }
        }
        return str;
    }
    
    public static List<String> toStringList(String[] arr){
       return new ArrayList<String>(Arrays.asList(arr));
    }
}

package com.easycoding.easycoding.util;

public class StringUtil {
    public static boolean isEmpty(String value){
        if(null == value || value.equals("")){
            return true;
        }
        return false;
    }
}

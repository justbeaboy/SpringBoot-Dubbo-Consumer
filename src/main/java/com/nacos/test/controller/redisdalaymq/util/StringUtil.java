package com.nacos.test.controller.redisdalaymq.util;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
public class StringUtil {



    public static boolean isEmpty(String value) {
        if (value == null || value.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

}

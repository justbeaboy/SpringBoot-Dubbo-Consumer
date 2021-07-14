package com.nacos.test.controller.redisdalaymq.util;

import lombok.Data;

/**
 * @author Mr.SoftRock
 * @Date 2021/7/14 16:48
 **/
@Data
public class Test {


    String name;

    Test1 test1;

    @Data
    public static class Test1{
        String name;
    }
}

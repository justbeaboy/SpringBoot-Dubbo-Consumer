package com.nacos.controller;

import com.nacos.impl.ICunsumerProxySv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.SoftRock
 * @Date 2021/3/1913:08
 **/
@RestController
public class TestController {


    @Autowired
    ICunsumerProxySv cunsumerProxySv;


    @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public String hello() {
        return "hello===>" + cunsumerProxySv.sayHello();
    }
}

package com.nacos.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
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

    @NacosValue(value = "${name:你好}", autoRefreshed = true)
    private String name;

    @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public String hello() {
        String s = cunsumerProxySv.sayHello();
        System.out.println("+++++++"+s);
        return "hello===>" + cunsumerProxySv.sayHello();
    }

    @GetMapping(value = "/hello1", produces = MediaType.APPLICATION_JSON_VALUE)
    public String hello1() {
        return "hello===>" + name;
    }
}

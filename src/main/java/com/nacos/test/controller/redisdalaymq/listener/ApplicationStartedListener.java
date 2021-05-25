package com.nacos.test.controller.redisdalaymq.listener;

import com.alibaba.fastjson.JSONObject;
import com.nacos.test.controller.redisdalaymq.biz.DelayJobBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Objects;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@Configuration
@Slf4j
public class ApplicationStartedListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private DelayJobBiz delayJobBiz;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        log.info("----获取监听----{}", Objects.isNull(applicationContext));

        delayJobBiz.start();
    }
}

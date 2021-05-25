package com.nacos.test.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.nacos.test.config.NacosConfig;
import com.nacos.test.controller.dalymq.delaymq.DelayMqProducer;
import com.nacos.test.controller.redisdalaymq.message.DelayMessage;
import com.nacos.test.controller.redisdalaymq.rocketmq.MessageProducer;
import com.nacos.test.service.ICunsumerProxySv;
import com.nacos.test.service.IMqtestSv;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.SoftRock
 * @Date 2021/3/1913:08
 **/
@RestController
@Slf4j
public class TestController {


    @Autowired
    ICunsumerProxySv cunsumerProxySv;

    @Autowired
    NacosConfig nacosConfig;

    @Autowired
    IMqtestSv mqtestSv;

    @Autowired
    MessageProducer messageProducer;


    @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public String hello() {
        String s = cunsumerProxySv.sayHello();
        System.out.println("+++++++" + s);
        return "hello===>" + cunsumerProxySv.sayHello();
    }

    @GetMapping(value = "/hello1", produces = MediaType.APPLICATION_JSON_VALUE)
    public String hello1() {
        return "hello===>" + nacosConfig.getUserId() + "ssss" + nacosConfig.getName();
    }


    @GetMapping(value = "/hello2", produces = MediaType.APPLICATION_JSON_VALUE)
    public void hello2() {
        mqtestSv.send();

    }

    @GetMapping(value = "/hello3", produces = MediaType.APPLICATION_JSON_VALUE)
    public void hello3() {
        int i = 5;
        for (int i1 = 0; i1 < i; i1++) {
            send(i1);
        }

    }

    private void send(int n) {
        DelayMessage delayMessage = new DelayMessage();
        delayMessage.setTopic("mrsoftrocktest");
        delayMessage.setTag("tag");
        delayMessage.setMessageKey(IdWorker.get32UUID());

        JSONObject body = new JSONObject();
        body.put("orderNo", "123456789+hello+88mesoftrock1" + n);
        body.put("status", "已经过期5" + n);
        delayMessage.setMessageBody(body.toJSONString());
        //先测试1分钟延迟
        delayMessage.setDelayTime(n * 60 * 1000L);
        messageProducer.sendDelayMessage(delayMessage);
    }

    private void sendDelayMq(int i) {
        //相当于代理topic要获取到的group分组
        DelayMqProducer producer = new DelayMqProducer("guava-group");
//        DelayMqProducer producer = new DelayMqProducer();
        try {

            producer.setNamesrvAddr("127.0.0.1:9876");
            producer.setRetryTimesWhenSendFailed(3);
            producer.start();
            Map<String, String> content = new HashMap<>();
            content.put("name", "mrsoftrock");
            content.put("message", "hello mrsoftrock------119" + i);
            //业务topic
            Message message = new Message("guava_hello_topic", null, JSON.toJSONBytes(content));
            SendResult sendResult = producer.sendDelay(message, DateUtils.addSeconds(new Date(), 90));
            System.out.println("发送返回结果：" + JSON.toJSONString(sendResult));
            System.out.println("消息发送时间：" + String.format("%tF %<tT", new Date()));
        } catch (Exception e) {
            log.info("发送延时消息失败" + e);
        } finally {
            System.out.println("-----");
        }
    }
}

package com.nacos.test.controller;

import com.alibaba.fastjson.JSON;
import com.nacos.test.config.NacosConfig;
import com.nacos.test.controller.delaymq.DelayMqProducer;
import com.nacos.test.service.ICunsumerProxySv;
import com.nacos.test.service.IMqtestSv;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
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
        DelayMqProducer producer = new DelayMqProducer("guava-group");
        try {
            producer.setNamesrvAddr("127.0.0.1:9876");
            producer.setRetryTimesWhenSendFailed(3);
            producer.start();
            Map<String, String> content = new HashMap<>();
            content.put("name", "guava");
            content.put("message", "hello mrsoftrock +== delaymq");
            Message message = new Message("guava_hello_topic", "delaytag", JSON.toJSONBytes(content));
            SendResult sendResult = producer.sendDelay(message, DateUtils.addSeconds(new Date(), 89));
            System.out.println("发送返回结果：" + JSON.toJSONString(sendResult));
            System.out.println("消息发送时间：" + String.format("%tF %<tT", new Date()));
        } catch (Exception exception) {
            log.info("发送延时消息失败" + exception.toString());
        }
        ;
    }
}

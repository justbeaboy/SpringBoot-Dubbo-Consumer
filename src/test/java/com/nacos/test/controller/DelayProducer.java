package com.nacos.test.controller;

import com.alibaba.fastjson.JSON;
import com.nacos.test.controller.delaymq.DelayMqProducer;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/12 14:39
 **/
public class DelayProducer {

    public static void main(String[] args) throws Exception {
        DelayMqProducer producer = new DelayMqProducer("guava-group");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.setRetryTimesWhenSendFailed(3);
        producer.start();
        Map<String, String> content = new HashMap<>();
        content.put("name", "guava");
        content.put("message", "hello mrsoftrock");
        Message message = new Message("guava_hello_topic", null, JSON.toJSONBytes(content));
        SendResult sendResult = producer.sendDelay(message, DateUtils.addSeconds(new Date(), 89));
        System.out.println("发送返回结果：" + JSON.toJSONString(sendResult));
        System.out.println("消息发送时间：" + String.format("%tF %<tT", new Date()));
    }

}

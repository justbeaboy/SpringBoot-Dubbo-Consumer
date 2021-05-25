package com.nacos.test.controller;

import com.alibaba.fastjson.JSON;
import com.nacos.test.controller.dalymq.delaymq.DelayMqProducer;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DelayProducer {

    public static void main(String[] args) throws Exception {
        int i = 5;
        DelayMqProducer producer = new DelayMqProducer("guava-group");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.setRetryTimesWhenSendFailed(3);
        producer.start();
        for (int i1 = 0; i1 < i; i1++) {
            sendDelayMq(i1,producer);
        }
    }

    private static void sendDelayMq(int i, DelayMqProducer producer) {
        //相当于代理topic要获取到的group分组

        try {
            Map<String, String> content = new HashMap<>();
            content.put("name", "mrsoftrock");
            content.put("message", "hello mrsoftrock------120x"+i);
            //业务topic
            Message message = new Message("guava_hello_topic", null, JSON.toJSONBytes(content));
//            SendResult sendResult = producer.sendDelay(message, DateUtils.addSeconds(new Date(), 90));
            SendResult sendResult = producer.sendDelay(message, DateUtils.addHours(new Date(), 14));
            System.out.println("发送返回结果：" + JSON.toJSONString(sendResult));
            System.out.println("消息发送时间：" + String.format("%tF %<tT", new Date()));
        } catch (Exception e) {
            System.out.println("发送延时消息失败" + e);
        } finally {
            System.out.println("-----");
        }
    }

}

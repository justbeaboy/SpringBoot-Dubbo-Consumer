package com.nacos.test.controller.mq;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @author Mr.SoftRock
 * @Date 2020/11/1714:53
 **/
@Service
@Slf4j
public class MQProducerService {

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    /**
     *
     * @param topic
     * @param tag
     * @param message
     * @param keys 消息标识
     * @param isSendOrderly 是否顺序消费
     * @param dataId 顺序消费唯一标识
     */
    public void sendMessage(String topic, String tag, Object message, String keys, boolean isSendOrderly, String dataId) {

        String destination = String.join(":", topic, tag);
        String payload = JSONObject.toJSONString(message);
        log.info("destination [{}],payload[{}]", destination, payload);
        if (!isSendOrderly) {
            Message<String> msg = MessageBuilder.withPayload(payload).setHeader(RocketMQHeaders.KEYS, keys).build();
            try {
                rocketMQTemplate.send(destination, msg);
            } catch (Exception e) {
                log.error("send mq mesage [{}] error for reason：[{}]", payload, e.getMessage());
                throw new RuntimeException(e);
            }
            return;
        }
        try {
            rocketMQTemplate.syncSendOrderly(destination, MessageBuilder.withPayload(payload).setHeader(RocketMQHeaders.KEYS, dataId).build(), dataId);
        } catch (Exception e) {
            log.error("send mq mesage [{}] error for reason：[{}]", payload, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

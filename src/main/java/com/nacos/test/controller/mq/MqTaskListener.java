package com.nacos.test.controller.mq;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Mr.SoftRock
 * @Date 2021/3/2513:05
 **/
@Service
@Slf4j
@RocketMQMessageListener(consumerGroup = "mrsoftmqgroup", topic = "mrsoftrock-mq-test")
public class MqTaskListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {
    @Override
    public void onMessage(MessageExt message) {
        log.info("---consume noOrder message begin");
        try {
            log.info(new String(message.getBody(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("---consume noOrder message end");
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        try {
            consumer.subscribe("mrsoftrock-mq-test", "tag");
        } catch (Exception e) {
            e.printStackTrace();
        }
        consumer.registerMessageListener((MessageListenerOrderly)(msgs, context) -> {
            log.info("消费消息---consume order message begin");
            try {
                String message = new String(msgs.get(0).getBody(), StandardCharsets.UTF_8);
                log.info("消费消息==>msgId:{},message:{}", msgs.get(0).getMsgId(), message);

            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("消费消息---consume order message end");

            return ConsumeOrderlyStatus.SUCCESS;
        });
    }
}

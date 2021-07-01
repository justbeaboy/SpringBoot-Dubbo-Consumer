package com.nacos.test.controller.mq;

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

/**
 * @author Mr.SoftRock
 * @Date 2021/3/2513:05
 **/
@Service
@Slf4j
@RocketMQMessageListener(consumerGroup = "guava-group------1", topic = "guava_topic1")
public class MqDelayTaskListener1 implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {
    @Override
    public void onMessage(MessageExt message) {

        try {
            log.info(new String(message.getBody(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        try {
            consumer.subscribe("guava_topic1", "*");
        } catch (Exception e) {
            e.printStackTrace();
        }
        consumer.registerMessageListener((MessageListenerOrderly)(msgs, context) -> {
            log.info("guava_topic消费消息---consume order message begin");
            try {
                String message = new String(msgs.get(0).getBody(), StandardCharsets.UTF_8);
                log.info("消费消息==>msgId:{},message:{}", msgs.get(0).getMsgId(), message);

            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("guava_topic消费消息---consume order message end");

            return ConsumeOrderlyStatus.SUCCESS;
        });
    }
}

package com.nacos.test.controller.redisdalaymq.config;


import com.nacos.test.controller.redisdalaymq.listener.ConsumerMessageListener;
import com.nacos.test.controller.redisdalaymq.rocketmq.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@Component
@Configuration
public class MqConsumerConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${rocketmq.name-server}")
    private String nameServerAddress;

    @PostConstruct
    public void init() {
        Map<String, Consumer> beansOfType = applicationContext.getBeansOfType(Consumer.class);
        for (Map.Entry<String, Consumer> next : beansOfType.entrySet()) {
            Consumer consumer = next.getValue();
            // 注册消费者
            registerConsumer(consumer);
        }
    }


    private void registerConsumer(Consumer consumer) {
        final String canonicalName = consumer.getClass().getCanonicalName();
        MqConsumer mqConsumer = consumer.getClass().getAnnotation(MqConsumer.class);

        // 消费组名称
        String group = StringUtils.isBlank(mqConsumer.group()) ?
                canonicalName.replaceAll("\\.", "-") : mqConsumer.group().replaceAll("[^\\w\\-_]", "");

        try {
            DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(group);
            pushConsumer.setNamesrvAddr(nameServerAddress);
            pushConsumer.subscribe(mqConsumer.topic(), mqConsumer.tag());

            // 从上次消费的断点继续消费
            pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            pushConsumer.registerMessageListener(new ConsumerMessageListener(consumer));
            pushConsumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
}

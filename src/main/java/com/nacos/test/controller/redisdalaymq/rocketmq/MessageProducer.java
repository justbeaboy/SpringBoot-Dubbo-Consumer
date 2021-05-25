package com.nacos.test.controller.redisdalaymq.rocketmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.nacos.test.controller.mq.MQProducerService;
import com.nacos.test.controller.redisdalaymq.biz.JobBiz;
import com.nacos.test.controller.redisdalaymq.constant.ConsumeResult;
import com.nacos.test.controller.redisdalaymq.message.DelayMessage;
import com.nacos.test.controller.redisdalaymq.message.MessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.io.IOException;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@Component
@Slf4j
public class MessageProducer implements Closeable {

    @Value("${rocketmq.name-server}")
    private String nameServerAddress;

    private DefaultMQProducer defaultMQProducer;

    @Autowired
    MQProducerService mqProducerService;

    @Autowired
    private JobBiz delayMessageBiz;

//    @PostConstruct
//    private void init() {
//        defaultMQProducer = new DefaultMQProducer("guava-group");
//        defaultMQProducer.setNamesrvAddr(nameServerAddress);
//        try {
//            defaultMQProducer.start();
//        } catch (MQClientException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 发送mq消息
     *
     * @param messageWrapper
     */
    public boolean sendMessage(MessageWrapper messageWrapper) {
        if (messageWrapper == null) {
            return false;
        }
        log.info("mqProducerService  发送消息===》{}", JSONObject.toJSONString(messageWrapper));
//        Message message = new Message();
//        message.setTopic(messageWrapper.getTopic());
//        message.setTags(messageWrapper.getTag());
//        message.setKeys(messageWrapper.getMessageKey());
//        message.setBody(messageWrapper.getMessageBody().getBytes());
//        log.info("发送rocket消息到特定topic中{},messageKey{}",messageWrapper.getTopic(), message.getKeys());
        try {
            mqProducerService.sendMessage(messageWrapper.getTopic(), messageWrapper.getTag(), messageWrapper.getMessageBody(), messageWrapper.getMessageKey(), false, "");
            log.info("发送rocket消息到特定topic messageKey: {}, send MQ message success!", messageWrapper.getMessageKey());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.error("发送rocket消息到特定topic messageKey: {}, send MQ message failure!", messageWrapper.getMessageKey());
        return false;
    }

    /**
     * 发送延时队列
     *
     * @param delayMessage
     * @return
     */
    public boolean sendDelayMessage(DelayMessage delayMessage) {
//        MessageWrapper messageWrapper = new MessageWrapper();
//        // 固定的消费延时消息的topic
//        messageWrapper.setTopic("dq-message-consumer");
//        messageWrapper.setTag("dq-tag");
//        String body = JSONObject.toJSONString(delayMessage);
//        messageWrapper.setMessageBody(body);
//        StringBuilder messageKey = new StringBuilder("delayMessage")
//                .append("-")
//                .append(delayMessage.getMessageKey());
//        messageWrapper.setMessageKey(messageKey.toString());
//        DelayMessage delayMessage = JSON.parseObject(messageBody, DelayMessage.class);
//        log.info("获取到 dq-message-consumer topic 中的 延时任务消息{}", messageBody);
        boolean success = delayMessageBiz.addMessage(delayMessage);
        if (success) {
            log.info("添加延时任务消息成功");
            log.info("add message to delay job success !");
//            return ConsumeResult.failure("add message to delay job success !");
            return true;
        }
        log.info("添加延时任务消息失败");
        return false;

//        return this.sendMessage(messageWrapper);
    }

    @Override
    public void close() throws IOException {
        if (defaultMQProducer != null) {
            defaultMQProducer.shutdown();
            defaultMQProducer = null;
        }
    }
}

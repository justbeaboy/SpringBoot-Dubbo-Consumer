package com.nacos.test.controller.redisdalaymq.rocketmq;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.nacos.test.controller.redisdalaymq.biz.JobBiz;
import com.nacos.test.controller.redisdalaymq.config.MqConsumer;
import com.nacos.test.controller.redisdalaymq.constant.ConsumeResult;
import com.nacos.test.controller.redisdalaymq.message.DelayMessage;
import com.nacos.test.controller.redisdalaymq.message.MessageWrapper;
import com.nacos.test.controller.redisdalaymq.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * 延时消息消费者
 *
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@Slf4j
@MqConsumer(topic = "dq-message-consumer", tag = "*")
public class MessageConsumer extends Consumer {

    @Autowired
    private JobBiz delayMessageBiz;

    /**
     * @param message
     * @return
     */
    @Override
    public ConsumeResult consume(MessageWrapper message) {
        log.info("监听获取到 topic {dq-message-consumer} 的消息{}", JSONObject.toJSONString(message));

        if (message == null) {
            return ConsumeResult.failure("message is null !");
        }

        if (StringUtil.isEmpty(message.getMessageKey())) {
            return ConsumeResult.failure("message key is null !");
        }

        String messageBody = message.getMessageBody();
        if (!StringUtils.hasLength(messageBody)) {
            return ConsumeResult.failure("consume delay message failure, body is null !");
        }

        try {
            //添加消息体中的延时任务到延时队列里
            // add message to delay job
            DelayMessage delayMessage = JSON.parseObject(messageBody, DelayMessage.class);
            log.info("获取到 dq-message-consumer topic 中的 延时任务消息{}", messageBody);
            boolean success = delayMessageBiz.addMessage(delayMessage);
            if (success) {
                log.info("添加延时任务消息成功");
                return ConsumeResult.failure("add message to delay job success !");
            }else{
                log.info("添加延时任务消息失败");
            }

        } catch (RuntimeException re) {
            return ConsumeResult.failure("add message to delay job failure, errMsg: " + re.getMessage());
        }
        return ConsumeResult.success();
    }


}

package com.nacos.test.controller.redisdalaymq.listener;



import com.nacos.test.controller.redisdalaymq.constant.ConsumeResult;
import com.nacos.test.controller.redisdalaymq.message.MessageWrapper;
import com.nacos.test.controller.redisdalaymq.rocketmq.Consumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *  mq消费者-消息监听器
 *
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
public class ConsumerMessageListener implements MessageListenerConcurrently {

    private Consumer consumer;


    public ConsumerMessageListener(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
                                                    ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        MessageExt messageExt = list.get(0);
        MessageWrapper messageWrapper = new MessageWrapper();
        messageWrapper.setTopic(messageExt.getTopic());
        messageWrapper.setTag(messageExt.getTags());
        messageWrapper.setMessageBody(new String(messageExt.getBody(), StandardCharsets.UTF_8));
        messageWrapper.setMessageKey(messageExt.getMsgId());
        ConsumeResult consumeResult = consumer.consume(messageWrapper);
        if (consumeResult.getConsumeStatus()) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } else {
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}

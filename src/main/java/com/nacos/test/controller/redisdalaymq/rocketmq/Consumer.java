package com.nacos.test.controller.redisdalaymq.rocketmq;


import com.nacos.test.controller.redisdalaymq.constant.ConsumeResult;
import com.nacos.test.controller.redisdalaymq.message.MessageWrapper;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
public abstract class Consumer {

    /**
     * 消息消费
     * @param message
     * @return
     */
    public abstract ConsumeResult consume(MessageWrapper message);

}

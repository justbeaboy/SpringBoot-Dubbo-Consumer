package com.nacos.test.controller.redisdalaymq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageWrapper implements Serializable {

    /**
     * rocketMq topic
     */
    private String topic;

    /**
     *  rocketMq tag
     */
    private String tag;

    /**
     *  消息唯一标示
     */
    private String messageKey;

    /**
     *  消息内容
     */
    private String messageBody;

    /**
     * 消息发送时间
     */
    private Date sendTime;


    public Date getSendTime() {
        if (sendTime == null) {
           return this.sendTime = new Date();
        } else {
           return this.sendTime = sendTime;
        }
    }
}

package com.nacos.test.controller.redisdalaymq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@NoArgsConstructor
@AllArgsConstructor
public class DelayMessage extends MessageWrapper implements Serializable {

    /**
     *  延时时间 单位秒
     */
    private Long delayTime;


    public Long getDelayTime() {
        if (delayTime == null) {
            return this.delayTime = 0L;
        } else {
            return delayTime;
        }
    }

    public void setDelayTime(Long delayTime) {
        this.delayTime = delayTime;
    }
}

package com.nacos.test.controller.redisdalaymq.constant;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@Data
public class ConsumeResult implements Serializable {


    private Boolean consumeStatus;

    private String message;


    public ConsumeResult() {

    }

    public ConsumeResult(Boolean consumeStatus, String message) {
        this.consumeStatus = consumeStatus;
        this.message = message;
    }

    public ConsumeResult(Boolean consumeStatus) {
        this.consumeStatus = consumeStatus;
    }


    public static ConsumeResult success() {
        return new ConsumeResult(true);
    }

    public static ConsumeResult failure(String message) {
        return new ConsumeResult(false, message);
    }
}



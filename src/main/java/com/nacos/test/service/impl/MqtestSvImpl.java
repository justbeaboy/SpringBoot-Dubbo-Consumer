package com.nacos.test.service.impl;

import com.nacos.test.controller.mq.MQProducerService;
import com.nacos.test.service.IMqtestSv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author Mr.SoftRock
 * @Date 2021/3/3114:32
 **/
//@Component
@Service
public class MqtestSvImpl implements IMqtestSv {

    @Autowired
    MQProducerService mqProducerService;
    @Override
    public void send() {
        mqProducerService.sendMessage("mrsoftrocktest", "tag", "writeBacks", "test", false, "test");

    }
}

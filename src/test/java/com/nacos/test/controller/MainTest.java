package com.nacos.test.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.nacos.test.controller.redisdalaymq.message.DelayMessage;
import com.nacos.test.controller.redisdalaymq.service.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 14:47
 **/
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class MainTest {
    @Autowired
    private RedisUtil redisUtil;

    @Value("${rocketmq.name-server}")
    private String nameServerAddress;

    @Test
    public void redisTest() {
//        String str = "{\"delayTime\":120,\"messageBody\":\"{\\\"orderNo\\\":\\\"123456789\\\",\\\"status\\\":\\\"已经过期\\\"}\",\"messageKey\":\"f62761a1e5710893ad4b75f1395980f4\",\"sendTime\":1620888152072,\"tag\":\"tag\",\"topic\":\"mrsoftrocktest\"}";
//        // 添加元数据
//        DelayMessage delayMessage = JSONObject.parseObject(str, DelayMessage.class);
//        redisUtil.putHashValue("dq-metaDataHashKey", delayMessage.getMessageKey(), JSON.toJSONString(delayMessage));
//        redisUtil.setObject("k6", "v6");
//        System.out.println(redisUtil.getObject("k6"));
        System.out.println(IdWorker.getId());

    }

}

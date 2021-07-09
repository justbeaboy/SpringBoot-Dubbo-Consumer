package com.nacos.test.controller.mq;

import com.alibaba.fastjson.JSONObject;
import com.nacos.test.controller.mq.bean.CanalBean;
import com.nacos.test.controller.mq.bean.DictDesc;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Mr.SoftRock
 * @Date 2021/07/08 20:33
 **/
@Service
@Slf4j
@RocketMQMessageListener(consumerGroup = "canal-group",
        topic = "mrsoftrock_dict_desc",
        selectorExpression = "*",
        consumeMode = ConsumeMode.ORDERLY,
        consumeThreadMax = 1

)
public class CanalListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {
    @Override
    public void onMessage(MessageExt message) {
        log.info("---consume canal message begin");
        try {
            log.info(new String(message.getBody(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("---consume canal message end");
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        try {
            consumer.subscribe("mrsoftrock_dict_desc", "*");
        } catch (Exception e) {
            e.printStackTrace();
        }
        consumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
            log.info("消费消息-mrsoftrock_dict_desc--consume order message begin");
            try {
                String message = new String(msgs.get(0).getBody(), StandardCharsets.UTF_8);
                log.info("消费消息==>msgId:{},message:{}", msgs.get(0).getMsgId(), message);
                CanalBean canalBean = JSONObject.parseObject(message, CanalBean.class);
                String table = canalBean.getTable();
                System.out.println(table.toString());
                String type = canalBean.getType();
                System.out.println(type);
                List<DictDesc> data = canalBean.getData();
                data.stream().forEach(tbTest -> {
                    log.info("获取到的数据库操作数据：---》" + JSONObject.toJSONString(tbTest));
                    if ("UPDATE".equals(type) && "dict_desc".equals(table)) {
                        //删除缓存
                        log.info("执行redis的操作---UPDATE");

                    } else if ("INSERT".equals(type) && "dict_desc".equals(table)) {

                        //添加缓存
                        log.info("执行redis的操作---INSERT");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("消费消息-mrsoftrock_dict_desc--consume order message end");

            return ConsumeOrderlyStatus.SUCCESS;
        });
    }
}

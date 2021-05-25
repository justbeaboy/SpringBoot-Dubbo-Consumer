package com.nacos.test.controller.redisdalaymq.runner;

import com.alibaba.fastjson.JSONObject;

import com.nacos.test.controller.redisdalaymq.message.MessageWrapper;
import com.nacos.test.controller.redisdalaymq.rocketmq.MessageProducer;
import com.nacos.test.controller.redisdalaymq.service.DelayJobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务处理
 *
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@Component
@Slf4j
public class HandleJobRunner extends DelayJobRunner {

    @Autowired
    private DelayJobService delayJobService;

    @Autowired
    private MessageProducer messageProducer;

    @Override
    void run() {
        executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder()
                        .namingPattern("delayJob-handler-runner-%d")
                        .daemon(true).build()
        );
        // 起始延迟500ms执行 每300ms执行一次
        executorService.scheduleWithFixedDelay(new HandleJobRunnable(delayJobService,
                messageProducer), 500, 300, TimeUnit.MILLISECONDS);
    }

    @Override
    void stop() {
        if (executorService != null) {
            try {
                executorService.shutdown();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException in) {
                in.printStackTrace();
                executorService.shutdownNow();
            }
        }
    }


    public static class HandleJobRunnable implements Runnable {

        private DelayJobService delayJobService;

        private MessageProducer messageProducer;

        public HandleJobRunnable(DelayJobService delayJobService,
                                 MessageProducer messageProducer) {

            this.delayJobService = delayJobService;
            this.messageProducer = messageProducer;
        }

        @Override
        public void run() {
            // 这里一定要加上异常捕获若未捕获ScheduledThreadPoolExecutor会另启一个线程或停止线程池
            // 原理: https://blog.csdn.net/weixin_39837207/article/details/111298518
            try {
                // pull toBeExecuteJob  将放入待处理的任务取出来，进行业务操作
                List<String> toBeExecutedList = delayJobService.pullToBeExecutedList();
                log.info("toBeExecutedList---》{}", JSONObject.toJSONString(toBeExecutedList));
                if (!CollectionUtils.isEmpty(toBeExecutedList)) {
                    for (String key : toBeExecutedList) {
                        String jobMetaData = delayJobService.getJobMetaData(key);
                        log.info("toBeExecutedList  jobMetaData --》{}", jobMetaData);
                        MessageWrapper messageWrapper = JSONObject.parseObject(jobMetaData, MessageWrapper.class);
                        if (!Objects.isNull(messageWrapper)) {
                            // send message
                            //将需要处理的延时数据，通过MQ发送
                            messageProducer.sendMessage(messageWrapper);
                            // remove metaData 将处理完成的数据 从延迟任务列表中移除
                            delayJobService.removeJobMetaData(key);
                            delayJobService.removeToBeExecutedList(key);

                        } else {

                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

package com.nacos.test.controller.redisdalaymq.runner;

import com.alibaba.fastjson.JSONObject;

import com.nacos.test.controller.redisdalaymq.message.DelayMessage;
import com.nacos.test.controller.redisdalaymq.service.DelayJobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  拉取延时任务
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@Component
@Slf4j
public class PullJobRunner extends DelayJobRunner {

    @Autowired
    private DelayJobService delayJobService;

    @Override
    void run() {
        executorService = new ScheduledThreadPoolExecutor(1,
                        new BasicThreadFactory.Builder()
                        .namingPattern("delayJob-pull-runner-%d")
                        .daemon(true).build()
        );
        // 起始延迟500ms执行 每300ms执行一次
        executorService.scheduleWithFixedDelay(new PullJobRunnable(delayJobService), 500, 300, TimeUnit.MILLISECONDS);
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



    public static class PullJobRunnable implements Runnable {

        private DelayJobService delayJobService;

        public PullJobRunnable(DelayJobService delayJobService) {
            this.delayJobService = delayJobService;
        }


        @Override
        public void run() {

            // 这里一定要加上异常捕获若未捕获ScheduledThreadPoolExecutor会另启一个线程或停止线程池
            // 原理: https://blog.csdn.net/weixin_39837207/article/details/111298518
            try {
                //zset 中获取
                List<String> jobKeys = delayJobService.pullRangeJob();
                log.info("拉取待处理任务-->{}",JSONObject.toJSONString(jobKeys));
                if (!CollectionUtils.isEmpty(jobKeys)) {

                    for (String jobKey : jobKeys) {
                        String jobMetaData = delayJobService.getJobMetaData(jobKey);
                        log.info("拉取待处理任务--jobMetaData-》{}",jobMetaData);
                        DelayMessage delayMessage = JSONObject.parseObject(jobMetaData, DelayMessage.class);

                        if (delayMessage != null) {
                            long expireTime = delayMessage.getSendTime().getTime() + delayMessage.getDelayTime();
                            //定时任务去比较，是否有超期待处理的数据需要操作
                            if ((System.currentTimeMillis() - expireTime) >= 0) {
                                log.info("需要添加到执行队列中");
                                // 添加待执行队列中
                                delayJobService.addToBeExecutedList(jobKey);
                                // 从延时任务的zset 删除掉
                                delayJobService.removeDelayJob(jobKey);
                            }
                        } else {
                            delayJobService.removeDelayJob(jobKey);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

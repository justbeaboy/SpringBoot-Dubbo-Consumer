package com.nacos.test.controller.redisdalaymq.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nacos.test.controller.redisdalaymq.message.DelayMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@Component
@Slf4j
public class DelayJobService {


    @Autowired
    private RedisUtil redisUtil;

    /**
     * 元数据hash key 存放消息的原始数据
     */
    private static String metaDataKey = "dq-metaDataHashKey";

    /**
     * 待执行的key
     */
    private static String toBeExecuteListKey = "dq-toBeExecuteListKey";

    /**
     *
     */
    private final int rangSize = 10;

    private final long maxDurationTime = 1000 * 60L;
    /**
     * zset key存放message key 并指定score = delayTime
     */
    private static String zsetKey = "dq-zSetkey";

    /**
     * 添加延时任务
     */
    public void addDelayJob(DelayMessage delayMessage) {
        log.info("addDelayJob开始---{}", JSONObject.toJSONString(delayMessage));
        // 添加元数据
        redisUtil.putHashValue(metaDataKey, delayMessage.getMessageKey(), JSON.toJSONString(delayMessage));
        log.info("addDelayJob开始---获取到延时任务{}", redisUtil.getHashValue(metaDataKey, delayMessage.getMessageKey()));
        // 添加到zset
        long score = delayMessage.getSendTime().getTime() + delayMessage.getDelayTime();
        redisUtil.addZsetValue(zsetKey, delayMessage.getMessageKey(), score);
        log.info("addDelayJob结束---");
    }

    public void removeDelayJob(String key) {
        redisUtil.removeZsetValue(zsetKey, key);
    }

    /**
     * 拉取任务
     *
     * @return
     */
    public List<String> pullRangeJob() {

        List<String> lsts = new ArrayList<>();

        double to = Long.valueOf(System.currentTimeMillis() + maxDurationTime).doubleValue();
        log.info("拉取延时消息任务，区间右值--》{}", to);
        Set<ZSetOperations.TypedTuple<String>> sets = redisUtil
                .rangeByScoreWithScores(zsetKey, 0, to, 0, rangSize);

        if (!CollectionUtils.isEmpty(sets)) {
            for (ZSetOperations.TypedTuple<String> curr : sets) {
                if (curr.getScore() <= System.currentTimeMillis()) {
                    lsts.add(curr.getValue());
                } else {
                    break;
                }
            }
            return lsts;
        }
        return null;
    }


    /**
     * 获取任务元数据
     *
     * @param key
     * @return
     */
    public String getJobMetaData(String key) {
        return redisUtil.getHashValue(metaDataKey, key);
    }

    /**
     * 删除元数据
     *
     * @param key
     */
    public void removeJobMetaData(String key) {
        redisUtil.deleteHashByKeys(metaDataKey, key);
    }

    /**
     * 添加到待执行的队列里
     */
    public void addToBeExecutedList(String key) {
        redisUtil.pushRightList(toBeExecuteListKey, key);
    }


    /**
     * @return
     */
    public List<String> pullToBeExecutedList() {
        return redisUtil.rangeList(toBeExecuteListKey, 0, 10);
    }

    public void removeToBeExecutedList(String key){
        redisUtil.removeList(toBeExecuteListKey, key);
    }


}

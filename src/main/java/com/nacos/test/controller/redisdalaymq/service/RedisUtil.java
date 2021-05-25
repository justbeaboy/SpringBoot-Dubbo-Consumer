package com.nacos.test.controller.redisdalaymq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String getObject(String key){
       return redisTemplate.opsForValue().get(key);
    }
    public void setObject(String key,String value){
    redisTemplate.opsForValue().set(key,value);
    }

    // ================== map ===================
    /**
     * 添加hash value
     * @param key
     * @param hashKey
     * @param hashValue
     */
    public void putHashValue(String key, String hashKey, String hashValue) {
       redisTemplate.opsForHash()
                .put(key, hashKey, hashValue);
    }

    /**
     * 获取hash value
     * @param key
     * @param mapKey
     * @return
     */
    public String getHashValue(String key, String mapKey) {
        return (String) redisTemplate.opsForHash()
                .get(key, mapKey);
    }

    /***
     * 删除hash里的值
     * @param key
     * @param keys
     */
    public void deleteHashByKeys(String key, Object... keys) {
        redisTemplate.opsForHash()
                .delete(key, keys);
    }


    // ================== zset ===================
    /**
     * 拉取zset数据
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set<ZSetOperations.TypedTuple<String>> rangeByScoreWithScores(String key,
                                                                          double min,
                                                                          double max,
                                                                          int offset,
                                                                          int count) {
        return redisTemplate.opsForZSet()
                .rangeByScoreWithScores(key, min, max, offset, count);
    }

    public boolean addZsetValue(String key, String itemKey, double score) {
        return redisTemplate.opsForZSet()
                .add(key, itemKey, score);
    }


    public Long removeZsetValue(String key, String itemKey) {
        return redisTemplate.opsForZSet()
                .remove(key, itemKey);
    }

    // ================== list ===================

    public void pushRightList(String key, String item) {
       redisTemplate.opsForList()
            .rightPush(key, item);
    }

    public String popRightList(String key) {
        return redisTemplate.opsForList()
                .rightPop(key);
    }


    public void pushLeftList(String key, String item) {
        redisTemplate.opsForList()
                .leftPush(key, item);
    }

    public String popLeftList(String key) {
        return redisTemplate.opsForList().
                leftPop(key);
    }

    public List<String> rangeList(String key, int start, int size) {
        return redisTemplate.opsForList()
                .range(key, start, size);
    }

    public boolean removeList(String key, String value) {
        try {
            redisTemplate.opsForList()
                .remove(key, 1, value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


}

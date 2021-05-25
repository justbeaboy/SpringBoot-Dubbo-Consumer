package com.nacos.test.controller.redisdalaymq.biz;



import com.nacos.test.controller.redisdalaymq.message.DelayMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@Slf4j
public abstract class JobBiz implements Closeable {

    protected volatile AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 延时队列状态
     * @return
     */
    protected boolean isRunning() {
        log.info("获取延时队列状态",isRunning.get());
        return isRunning.get();
    }


    /**
     *  启动延时队列
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            run();
            log.info("延时队列启动成功");
        }
    }

    /**
     *  关闭延时队列
     */
    public void shutDown() {
        if (isRunning.compareAndSet(true, false)) {
            stop();
        }
    }

    @Override
    public void close() throws IOException {
        shutDown();
    }

    /**
     * 开启定时任务
     */
    abstract void run();

    abstract void stop();

    /**
     * 添加延时消息
     * @param delayMessage
     */
    public abstract boolean addMessage(DelayMessage delayMessage);

}

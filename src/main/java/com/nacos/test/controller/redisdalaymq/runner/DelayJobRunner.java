package com.nacos.test.controller.redisdalaymq.runner;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
public abstract class DelayJobRunner implements Closeable {

    protected volatile AtomicBoolean isRunning = new AtomicBoolean(false);

    protected ScheduledExecutorService executorService;

    public boolean isRunning() {
        return isRunning.get();
    }

    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            run();
        }
    }

    public void shutDown() {
        if (isRunning.compareAndSet(true, false)) {
            stop();
        }
    }

    @Override
    public void close() throws IOException {
        shutDown();
    }

    abstract void run();


    abstract void stop();


}

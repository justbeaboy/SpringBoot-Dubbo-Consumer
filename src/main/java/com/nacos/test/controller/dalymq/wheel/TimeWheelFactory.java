package com.nacos.test.controller.dalymq.wheel;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/12
 */
public class TimeWheelFactory {

    private static HashedWheelTimer hashedWheelTimer;

    public static HashedWheelTimer getInstance() {
        if (hashedWheelTimer == null) {
            synchronized (TimeWheelFactory.class) {
                if (hashedWheelTimer == null) {
                    hashedWheelTimer = new HashedWheelTimer(1, TimeUnit.SECONDS, 60);
                }
            }
        }
        hashedWheelTimer.start();
        return hashedWheelTimer;
    }

    public Timeout newTask(TimerTask timerTask, long delay, TimeUnit timeUnit) {
        return hashedWheelTimer.newTimeout(timerTask, delay, timeUnit);
    }

    public static void stop() {
        if (hashedWheelTimer != null) {
            hashedWheelTimer.stop();
        }
    }
}

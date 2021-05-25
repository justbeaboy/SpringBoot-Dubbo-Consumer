package com.nacos.test.controller.redisdalaymq.config;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MqConsumer {

    /**
     *  同一个消费组只有一个消费者能消费
     * @return
     */
    String group() default "";

    /**
     *
     * @return
     */
    String topic();

    /**
     *
     * @return
     */
    String tag();


}

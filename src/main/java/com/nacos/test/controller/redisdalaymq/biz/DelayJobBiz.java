package com.nacos.test.controller.redisdalaymq.biz;


import com.nacos.test.controller.redisdalaymq.message.DelayMessage;
import com.nacos.test.controller.redisdalaymq.runner.HandleJobRunner;
import com.nacos.test.controller.redisdalaymq.runner.PullJobRunner;
import com.nacos.test.controller.redisdalaymq.service.DelayJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 09:21
 */
@Component
@Slf4j
public class DelayJobBiz extends JobBiz {

    @Autowired
    private DelayJobService delayJobService;

    @Autowired
    private PullJobRunner pullJobRunner;

    @Autowired
    private HandleJobRunner handleJobRunner;

    @Override
    void run() {
        pullJobRunner.start();
        handleJobRunner.start();
    }

    @Override
    void stop() {
        pullJobRunner.shutDown();
        handleJobRunner.shutDown();
    }

    @Override
    public boolean addMessage(DelayMessage delayMessage) {
        if (isRunning()) {
            log.info("addMessage  isRunning success");
            delayJobService.addDelayJob(delayMessage);
            return true;
        }
        return false;
    }
}

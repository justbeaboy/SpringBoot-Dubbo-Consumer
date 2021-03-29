package com.nacos.test.impl;

import com.ai.mrsoftrock.rpc.nacosTest.INacosSv;
import com.ai.mrsoftrock.rpc.nacosTest.dto.NaocsTest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Mr.SoftRock
 * @Date 2021/3/1912:50
 **/
@Service
public class CunsumerProxySvImpl implements ICunsumerProxySv {

    @DubboReference(version = "1.0.0")
    INacosSv testSv;

    @Override
    public String sayHello() {
        NaocsTest query = testSv.query();
        if (Objects.isNull(query)) {
            return "返回空";
        }
        return query.getUserId() + "||||" + query.getUserName();
    }
}

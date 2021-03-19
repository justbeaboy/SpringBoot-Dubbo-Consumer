package com.nacos.impl;

import com.ai.mrsoftrock.rpc.testrpc.ITestSv;
import com.ai.mrsoftrock.rpc.testrpc.dto.Test;
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
    ITestSv testSv;

    @Override
    public String sayHello() {
        Test query = testSv.query();
        if (Objects.isNull(query)) {
            return "返回空";
        }
        return query.getUserid() + "||||" + query.getUsername();
    }
}

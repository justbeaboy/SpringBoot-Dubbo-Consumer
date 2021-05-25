package com.nacos.test.service.impl;

import com.nacos.test.service.ICunsumerProxySv;
import org.springframework.stereotype.Service;

/**
 * @author Mr.SoftRock
 * @Date 2021/3/1912:50
 **/
@Service
public class CunsumerProxySvImpl implements ICunsumerProxySv {

//    @DubboReference(version = "1.0.0")
//    INacosSv testSv;

    @Override
    public String sayHello() {
//        NaocsTest query = testSv.query();
//        if (Objects.isNull(query)) {
//            return "返回空";
//        }
//        return query.getUserId() + "||||" + query.getUserName();
        return null;
    }
}

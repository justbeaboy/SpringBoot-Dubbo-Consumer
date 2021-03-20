package com.nacos.config;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

/**
 * @author Mr.SoftRock
 * @Date 2021/3/1920:14s
 **/
@Component
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NacosPropertySource(dataId = "springboot-nacos-test",type = ConfigType.JSON,autoRefreshed = true)
public class NacosConfig {

    @NacosValue(value = "${name:name}", autoRefreshed = true)
    String name;
    @NacosValue(value = "${userId:userId}", autoRefreshed = true)
    String userId;
}

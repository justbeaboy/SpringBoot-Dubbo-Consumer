package com.nacos.test.controller.mapper.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nacos.test.controller.mapper.entity.Dict;
import com.nacos.test.controller.mapper.mapper.DictMapper;
import org.springframework.stereotype.Service;

/**
 * @author Mr.SoftRock
 * @Date 2021/7/15 17:24
 **/
@Service
public class DictDaoSvImpl extends ServiceImpl<DictMapper,Dict> implements IDictDaoSv {
}

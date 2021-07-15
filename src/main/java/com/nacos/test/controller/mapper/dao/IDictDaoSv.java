package com.nacos.test.controller.mapper.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nacos.test.controller.mapper.entity.Dict;

import java.util.List;

/**
 * @author Mr.SoftRock
 * @Date 2021/7/15 17:23
 **/
public interface IDictDaoSv extends IService<Dict> {

    List<Dict> select(int limit);
}

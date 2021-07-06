package com.nacos.test.controller.mapper.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nacos.test.controller.mapper.entity.SysSeq;
import com.nacos.test.controller.mapper.mapper.SysSeqMapper;
import org.springframework.stereotype.Service;

/**
 * @author Mr.SoftRock
 * @Date 2021/7/6 15:08
 **/
@Service
public class SysSeqDaoImpl extends ServiceImpl<SysSeqMapper, SysSeq> implements ISysSeqDaoSv {
}

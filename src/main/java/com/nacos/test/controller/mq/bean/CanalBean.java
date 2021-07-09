package com.nacos.test.controller.mq.bean;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * @author Mr.SoftRock
 * @Date 2021/7/8 20:40
 **/
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CanalBean {
    /**
     * 数据
     */
    List<DictDesc> data;
    /**
     * 数据库名称
     */
    String database;
    long es;
    /**
     * 递增，从1开始
     */
    int id;
    /**
     * 是否是DDL语句
     */
    boolean isDdl;
    /**
     * 表结构的字段类型
     */
    MysqlType mysqlType;
    /**
     * UPDATE语句，旧数据
     */
    List<DictDesc> old;
    /**
     * 主键名称
     */
    List<String> pkNames;
    /**
     * sql语句
     */
    String sql;

    SqlType sqlType;
    /**
     * 表名
     */
    String table;

    long ts;
    /**
     * (新增)INSERT、(更新)UPDATE、(删除)DELETE、(删除表)ERASE等等
     */
    String type;


}

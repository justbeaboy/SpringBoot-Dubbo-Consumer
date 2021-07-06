package com.nacos.test.controller.mapper.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;

/**
 * 字典值配置表
 *
 * @author Mr.SoftRock
 * @Date 2021/5/17 10:48
 **/
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysSeq extends Model<SysSeq> implements Serializable {
    private static final long serialVersionUID = 2100075669880364682L;

    @TableField("ID_SEGMWNT")
    @TableId(value = "ID_SEGMWNT")
    Long IdSegmwnt;

    @TableField("SEQ_NAME")
    String seqName;
    @TableField("CURRENT_VALUE")
    Long currentValue;
    @TableField("INCREMENT")
    Long increment;
    @TableField("TENAND_ID")
    String tenandId;
    @TableField("STATUS")
    String status;


    @Override
    protected Serializable pkVal() {
        return this.IdSegmwnt;
    }
}

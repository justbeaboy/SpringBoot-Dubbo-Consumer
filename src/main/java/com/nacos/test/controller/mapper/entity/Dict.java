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
 * @author Mr.SoftRock
 * @Date 2021/7/15 17:19
 **/
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Dict extends Model<Dict> implements Serializable {

    @TableId(value = "DICT_ID")
    Long dictId;

    @TableField("TYPE_CODE")
    String typeCode;

    @TableField("PARAM_CODE")
    String paramCode;

    @TableField("PARAM_VALUE")
    String paramValue;

    @TableField("PARAM_DESC")
    String paramDesc;

    @TableField("SORT")
    Integer sort;

    @TableField("REMARK")
    String remark;

    @TableField("STATUS")
    String status;

    @TableField("DELETED")
    String deleted;

    @TableField("CREATE_BY")
    Long createBy;

    @TableField("CREATE_DATE")
    Date createDate;

    @TableField("UPDATE_BY")
    Long updateBy;

    @TableField("UPDATE_DATE")
    Date updateDate;

    @Override
    protected Serializable pkVal() {
        return this.dictId;
    }
}

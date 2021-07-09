package com.nacos.test.controller.mq.bean;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

/**
 * @author Mr.SoftRock
 * @Date 2021/7/8 20:45
 **/
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class DictDesc {
    Long recordId;
    String typeCode;
    String paramCode;
    String tpeCodeName;
    String paramCodeName;
    String status;
    Long createBy;
    Date createDate;
    Long updateBy;
    Date updateDate;


}

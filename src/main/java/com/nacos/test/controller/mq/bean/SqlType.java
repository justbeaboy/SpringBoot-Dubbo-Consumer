package com.nacos.test.controller.mq.bean;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Mr.SoftRock
 * @Date 2021/7/8 20:43
 **/
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class SqlType {
     int id;
     int commodity_name;
     int commodity_price;
     int number;
     int description;
}

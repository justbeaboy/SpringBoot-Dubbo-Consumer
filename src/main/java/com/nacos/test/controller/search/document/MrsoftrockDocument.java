package com.nacos.test.controller.search.document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nacos.test.controller.utils.DateFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author Mr.SoftRock
 * @Date 2021/7/1 09:54
 **/
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "mrsoftrock")
public class MrsoftrockDocument {

    @Id
    String id;

    @Field(type = FieldType.Text)
    String name;
    @Field(type = FieldType.Text)
    String title;

    @Field(type = FieldType.Integer)
    Integer age;

//    @Field(type = FieldType.Date)
//    Date date;

}

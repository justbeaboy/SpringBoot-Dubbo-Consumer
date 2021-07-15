package com.nacos.test.controller.mapper.mapper;

import com.nacos.test.controller.mapper.entity.Dict;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Mr.SoftRock
 * @Date 2021/7/15 19:14
 **/
@Repository
public interface DictQuery {


    @Select("<script>\n" +
            "select dict_id,type_code,param_code,param_value,sort,status from dict\n" +
            "where dict_id > #{lastBatchMaxId}  order by dict_id asc limit  #{limit}\n" +
            "</script>")
    List<Dict> query(@Param("lastBatchMaxId") long lastBatchMaxId, @Param("limit") int limit);
}

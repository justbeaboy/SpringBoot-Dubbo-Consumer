package com.nacos.test.controller.constant;

/**
 * @author Mr.SoftRock
 * @Date 2021/7/15 17:48
 **/
public class ExcelConstant {

    /**
     * 每个sheet存储的记录数 100W
     */
    public static final Integer PER_SHEET_ROW_COUNT = 1000000;

    /**
     * 每次向EXCEL写入的记录数(查询每页数据大小) 2w
     * 这个数值，可以根据自己的实际情况去调整
     */
    public static final Integer PER_WRITE_ROW_COUNT = 20000;
}

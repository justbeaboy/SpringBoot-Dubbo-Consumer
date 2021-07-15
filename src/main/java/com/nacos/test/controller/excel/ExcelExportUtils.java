package com.nacos.test.controller.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 自定义Excel导出
 * 这个工具类，可以用于常规固定模板的导出，表头不能动态变更，
 * @author Mr.SoftRock
 * @Date 2021/7/14 17:34
 **/
@Slf4j
public final class ExcelExportUtils {


    /**
     * @param response     文件响应
     * @param map          业务数据
     * @param fileName     文件名称
     * @param sheetNameMap sheet名称
     */
    public static void writeExcel(HttpServletResponse response,
                                  Map<Class<?>, List<?>> map,
                                  String fileName, Map<Class<?>, String> sheetNameMap) {
        OutputStream outputStream = null;
        //处理导出excel
        try {
            outputStream = response.getOutputStream();
        } catch (IOException ex) {
            log.error("查询导出列表数据,获取输出流失败,失败原因->{}", ex.toString());
        }
        try {
            setResponse(response, fileName);
            if (CollectionUtils.isEmpty(map)) {
                return;
            }
            //可能存在多个sheet业的数据，所以需要遍历集合
            int sheetNo = 1;
            ExcelWriter excelWriter = EasyExcel.write(outputStream).excelType(ExcelTypeEnum.XLSX)
                    .registerWriteHandler(myHorizontalCellStyleStrategy())
                    .build();

            for (Map.Entry<Class<?>, List<?>> m : map.entrySet()) {
                if (Objects.isNull(m.getKey())) {
                    continue;
                }
                WriteSheet writeSheet = EasyExcel.writerSheet(sheetNo, sheetNameMap.get(m.getKey())).head(m.getKey()).build();
                excelWriter.write(m.getValue(), writeSheet);
                sheetNo++;
            }
            if (Objects.nonNull(excelWriter)) {
                excelWriter.finish();
            }
        } catch (Exception ex) {
            log.error("查询列表导出,发生错误-->{}", ex.toString());
        } finally {
            if (outputStream != null) {

                try {
                    outputStream.close();
                } catch (IOException ex) {
                    log.error("查询列表导出,关闭输出流失败，-->{}", ex.toString());
                }
            }
        }


    }

    /**
     * 组装文件名称等外部信息
     *
     * @param response
     * @param fileName
     * @return
     */
    private static void setResponse(HttpServletResponse response, String fileName) throws Exception {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "utf-8"));
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }

    public static HorizontalCellStyleStrategy myHorizontalCellStyleStrategy() {
        //表头样式策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //表头前景设置淡蓝色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setBold(true);
        headWriteFont.setFontName("宋体");
        headWriteFont.setFontHeightInPoints((short) 12);
        headWriteCellStyle.setWriteFont(headWriteFont);
        //内容样式策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        WriteFont contentWriteFont = new WriteFont();
        //内容字体大小
        contentWriteFont.setFontName("宋体");
        contentWriteFont.setFontHeightInPoints((short) 11);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        //设置自动换行
        contentWriteCellStyle.setWrapped(false);
        //设置水平靠左
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        //设置边框样式
        setBorderStyle(contentWriteCellStyle);
        //内容风格可以定义多个。
        List<WriteCellStyle> listCntWritCellSty = new ArrayList<>();
        listCntWritCellSty.add(contentWriteCellStyle);
//        WriteCellStyle contentWriteCellStyle2 = new WriteCellStyle();
//        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色。
//        // 头默认了 FillPatternType所以可以不指定。
//        contentWriteCellStyle2.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
//        // 背景绿色
//        // contentWriteCellStyle2.setFillForegroundColor(IndexedColors.GREEN.getIndex());
//        //设置垂直居中
//        contentWriteCellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
//        //设置边框样式
//        setBorderStyle(contentWriteCellStyle2);
//        listCntWritCellSty.add(contentWriteCellStyle2);
        // 水平单元格风格综合策略(表头 + 内容)
        // return  new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        return new HorizontalCellStyleStrategy(headWriteCellStyle, listCntWritCellSty);
    }

    private static void setBorderStyle(WriteCellStyle contentWriteCellStyle) {
        //设置边框样式
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        // contentWriteCellStyle.setBottomBorderColor(IndexedColors.BLUE.getIndex()); //颜色
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
    }

}
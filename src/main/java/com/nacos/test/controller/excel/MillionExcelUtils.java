package com.nacos.test.controller.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.merge.LoopMergeStrategy;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.nacos.test.controller.constant.ExcelConstant;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is a great export tool
 *
 * @author Mr.SoftRock
 * @Date 2021/7/15 17:44
 **/
@Slf4j
public final class MillionExcelUtils {

    public static void customDynamicExport(HttpServletResponse response, String fileName,
                                           List<String> titleList,
                                           Integer totalRowCount) {

        OutputStream outputStream = null;
        //处理导出excel
        try {
            outputStream = response.getOutputStream();
        } catch (IOException ex) {
            log.error("查询导出列表数据,获取输出流失败,失败原因->{}", ex.toString());
        }
        try {
            setResponse(response, fileName);

            List<List<String>> headTitleInfo = excelTitle(titleList);

            ExcelWriter excelWriter = new ExcelWriterBuilder()
                    .head(headTitleInfo)
                    .file(outputStream)
                    .excelType(ExcelTypeEnum.XLSX)
                    .autoCloseStream(true)
                    .registerWriteHandler(myHorizontalCellStyleStrategy())
                    .build();

//            Integer totalRowCount = 900000;
            Integer perSheetRowCount = ExcelConstant.PER_SHEET_ROW_COUNT;
            long lastBatchMaxId = 0L;
            int limit = 1000;
            for (;;){

            }


            //  先select 然后  自增主键分段处理
            //下面的导出步骤需要分段去处理
//            List<List<Object>> dataList = new ArrayList<>();



//            writerBuilder.head(headTitleInfo).sheet("模板").doWrite(dataList);


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

    private static List<List<String>> excelTitle(List<String> titleList) {
        List<List<String>> excelTitleList = new ArrayList<>(titleList.size());
        titleList.forEach(k -> {
            List<String> titles = new ArrayList<>(1);
            titles.add(k);
            excelTitleList.add(titles);
        });
        return excelTitleList;
    }

    private static List<LoopMergeStrategy> mergeCells(List<String> titleList, List<String> mergeTitles, Map<String, List<Object>> dataMap) {

        if (CollectionUtils.isEmpty(mergeTitles)) {
            return Collections.emptyList();
        }
        //将需要合并的项转换为map,方便获取合并的位置
        Map<String, String> mergeTitleMap = mergeTitles.stream().collect(Collectors.toMap(k -> k, k -> k));
        //获取合并的位置
        Map<String, Integer> mergeLocationMap = new HashMap<>(mergeTitles.size());
        for (int i = 0; i < titleList.size(); i++) {
            if (Objects.nonNull(mergeTitleMap.get(titleList.get(i)))) {
                mergeLocationMap.put(titleList.get(i), i);
            }
        }
        List<LoopMergeStrategy> loopMergeStrategies = new ArrayList<>();

        mergeTitles.forEach(k -> {
            //合并数据所在的列数
            Integer columnIndex = mergeLocationMap.get(k);
            List<Object> mergeDatas = dataMap.get(k);
            //需要合并的数量
            Integer currentMergeNum = 1;
            for (int i = 0; i < mergeDatas.size(); i++) {
                //是否存在合并的环节
                boolean isMerge = false;
                if (i != 0 && Objects.equals(mergeDatas.get(i), mergeDatas.get(i - 1))) {
                    isMerge = true;
                    currentMergeNum = currentMergeNum + 1;
                    if (i == mergeDatas.size() - 1) {
                        isMerge = false;
                    }
                }
                if (!isMerge && currentMergeNum > 1) {
                    //合并的行数
                    Integer eachRow = currentMergeNum;
                    loopMergeStrategies.add(new LoopMergeStrategy(eachRow, columnIndex));
                    currentMergeNum = 1;
                }
            }

        });


        return loopMergeStrategies;
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
        return new HorizontalCellStyleStrategy(headWriteCellStyle, listCntWritCellSty);
    }

    private static void setBorderStyle(WriteCellStyle contentWriteCellStyle) {
        //设置边框样式
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
    }


}

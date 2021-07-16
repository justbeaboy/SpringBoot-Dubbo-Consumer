package com.nacos.test.controller.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.merge.LoopMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.google.common.collect.Lists;
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
 * @author Mr.SoftRock
 * @Date 2021/7/14 17:39
 **/
@Slf4j
public class WriteExcelUtilsPlus {

    /**
     * 动态生成
     *
     * @param response    浏览器响应
     * @param fileName    导出文件名
     * @param titleList   动态列名
     * @param mergeTitles 需要单元格合并的动态列名
     * @param dataList    导出数据
     * @param sheetName   默认sheet名称
     */
    public static void customDynamicExport(HttpServletResponse response,
                                           String fileName,
                                           List<String> titleList,
                                           List<String> mergeTitles,
                                           List<List<Object>> dataList,
                                           String sheetName) {
        OutputStream outputStream = null;
        //处理导出excel
        try {
            outputStream = response.getOutputStream();
        } catch (IOException ex) {
            log.error("查询导出列表数据,获取输出流失败,失败原因->{}", ex.toString());
        }
        try {
            setResponse(response, fileName);
            //100w数据就分sheet
            List<List<List<Object>>> dataItemList = Lists.partition(dataList, 500000);
            List<List<String>> headTitleInfo = excelTitle(titleList);

            ExcelWriter excelWriter = new ExcelWriterBuilder()
                    .head(headTitleInfo)
                    .file(outputStream)
                    .excelType(ExcelTypeEnum.XLSX)
                    .autoCloseStream(true)
                    .registerWriteHandler(myHorizontalCellStyleStrategy())
                    .build();

            for (int a = 0; a < dataItemList.size(); a++) {
                //导出当前数据转换为Map数据结构，k->动态标题名称 v->此标题下的数据按放入顺序
                Map<String, List<Object>> dataMap = new HashMap<>(titleList.size());
                List<List<Object>> datas = dataItemList.get(a);
//
                WriteSheet writeSheet = new WriteSheet();
                writeSheet.setSheetName(sheetName + a);
                excelWriter.write(datas, writeSheet);
            }
            excelWriter.finish();

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
        return titleList.stream().map(Collections::singletonList).collect(Collectors.toList());
    }

    /**
     * 单元格合并处理
     *
     * @param titleList   动态表头
     * @param mergeTitles 需要执行单元格合并的表头
     * @param dataMap     按照顺序处理好的数据
     * @return
     */
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
            int currentMergeNum = 1;
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
                    int eachRow = currentMergeNum;
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

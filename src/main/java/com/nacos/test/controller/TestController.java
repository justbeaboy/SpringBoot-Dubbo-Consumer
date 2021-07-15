package com.nacos.test.controller;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import com.nacos.test.config.NacosConfig;
import com.nacos.test.controller.dalymq.delaymq.DelayMqProducer;
import com.nacos.test.controller.excel.MillionExcelUtils;
import com.nacos.test.controller.excel.WriteExcelUtils;
import com.nacos.test.controller.mapper.dao.IDictDaoSv;
import com.nacos.test.controller.mapper.entity.Dict;
import com.nacos.test.controller.mapper.mapper.DictQuery;
import com.nacos.test.controller.redisdalaymq.message.DelayMessage;
import com.nacos.test.controller.redisdalaymq.rocketmq.MessageProducer;
import com.nacos.test.controller.redisdalaymq.util.Test;
import com.nacos.test.service.ICunsumerProxySv;
import com.nacos.test.service.IMqtestSv;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.SheetBuilder;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Mr.SoftRock
 * @Date 2021/3/1913:08
 **/
@RestController
@Slf4j
public class TestController {


    @Autowired
    ICunsumerProxySv cunsumerProxySv;

    @Autowired
    NacosConfig nacosConfig;

    @Autowired
    IMqtestSv mqtestSv;

    @Autowired
    MessageProducer messageProducer;
    @Autowired
    IDictDaoSv dictDaoSv;

    @Autowired
    DictQuery dictQuery;


    @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public String hello() {
        String s = cunsumerProxySv.sayHello();
        System.out.println("+++++++" + s);
        return "hello===>" + cunsumerProxySv.sayHello();
    }

    @GetMapping(value = "/hello1", produces = MediaType.APPLICATION_JSON_VALUE)
    public String hello1() {
        return "hello===>" + nacosConfig.getUserId() + "ssss" + nacosConfig.getName();
    }


    @GetMapping(value = "/hello2", produces = MediaType.APPLICATION_JSON_VALUE)
    public void hello2() {
        mqtestSv.send();

    }

    @GetMapping(value = "/hello3", produces = MediaType.APPLICATION_JSON_VALUE)
    public void hello3() {
        int i = 5;
        for (int i1 = 0; i1 < i; i1++) {
            send(i1);
        }

    }

    @GetMapping(value = "/export")
    public void export(HttpServletResponse response) {

        List<List<Object>> dataList = new ArrayList<>();

        List<com.nacos.test.controller.redisdalaymq.util.Test> tests = new ArrayList<>();

        tests.add(com.nacos.test.controller.redisdalaymq.util.Test.builder().name("张三").age(10).address("北京").build());
        tests.add(com.nacos.test.controller.redisdalaymq.util.Test.builder().name("李四").age(20).address("北京").build());
        tests.add(com.nacos.test.controller.redisdalaymq.util.Test.builder().name("王五").age(20).address("上海").build());
        tests.add(com.nacos.test.controller.redisdalaymq.util.Test.builder().name("赵四").age(10).address("铁岭").build());
//        for (Test test : tests) {
//            List<Object> objects = new ArrayList<>();
//            objects.add(test.getName());
//            objects.add(test.getAge());
//            objects.add(test.getAddress());
//            dataList.add(objects);
//        }

        for (int i = 0; i < 10; i++) {
            List<Object> objects = new ArrayList<>();
            objects.add("姓名：" + i);
            objects.add("年龄：" + i);
            objects.add("地址：" + i);
            dataList.add(objects);
        }

        List<String> titleList = new ArrayList<>(2);
        titleList.add("姓名");
        titleList.add("年龄");
        titleList.add("地址");

        List<String> mergeTitles = new ArrayList<>(2);
        mergeTitles.add("年龄");
        mergeTitles.add("地址");

//        WriteExcelUtils.customDynamicExport(response, "测试导出", titleList, Collections.emptyList(), dataList);
//        MillionExcelUtils.customDynamicExport(response, "测试导出", titleList, Collections.emptyList(), dataList);

    }

    @GetMapping(value = "/millionExport")
    public void millionExport(HttpServletResponse response) {


        List<String> titleList = new ArrayList<>(2);
        titleList.add("ID");
        titleList.add("type_code");
        titleList.add("param_code");
        titleList.add("param_value");
        titleList.add("param_desc");
        titleList.add("sort");
        titleList.add("status");

        OutputStream outputStream = null;
        //处理导出excel
        try {
            outputStream = response.getOutputStream();
        } catch (IOException ex) {
            log.error("查询导出列表数据,获取输出流失败,失败原因->{}", ex.toString());
        }
        try {
            List<List<String>> headTitleInfo = excelTitle(titleList);

            ExcelWriter excelWriter = new ExcelWriterBuilder()
                    .head(headTitleInfo)
                    .file(outputStream)
                    .excelType(ExcelTypeEnum.XLSX)
                    .autoCloseStream(true)
                    .registerWriteHandler(myHorizontalCellStyleStrategy())
                    .build();
            WriteSheet writeSheet = new WriteSheet();
            writeSheet.setSheetName("模版");

            long lastBatchMaxId = 0L;
            int limit = 100000;
            for (; ; ) {
                List<Dict> dicts = dictQuery.query(lastBatchMaxId, limit);
                if (CollectionUtils.isEmpty(dicts)) {
                    excelWriter.finish();
                    break;
                } else {
                    lastBatchMaxId = dicts.stream().map(Dict::getDictId).max(Long::compareTo).orElse(Long.MAX_VALUE);
                    excelWriter.write(dicts, writeSheet);
                }

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

    private void send(int n) {
        DelayMessage delayMessage = new DelayMessage();
        delayMessage.setTopic("mrsoftrocktest");
        delayMessage.setTag("tag");
        delayMessage.setMessageKey(IdWorker.get32UUID());

        JSONObject body = new JSONObject();
        body.put("orderNo", "123456789+hello+88mesoftrock1" + n);
        body.put("status", "已经过期5" + n);
        delayMessage.setMessageBody(body.toJSONString());
        //先测试1分钟延迟
        delayMessage.setDelayTime(n * 60 * 1000L);
        messageProducer.sendDelayMessage(delayMessage);
    }

    private void sendDelayMq(int i) {
        //相当于代理topic要获取到的group分组
        DelayMqProducer producer = new DelayMqProducer("guava-group");
//        DelayMqProducer producer = new DelayMqProducer();
        try {

            producer.setNamesrvAddr("127.0.0.1:9876");
            producer.setRetryTimesWhenSendFailed(3);
            producer.start();
            Map<String, String> content = new HashMap<>();
            content.put("name", "mrsoftrock");
            content.put("message", "hello mrsoftrock------119" + i);
            //业务topic
            Message message = new Message("guava_hello_topic", null, JSON.toJSONBytes(content));
            SendResult sendResult = producer.sendDelay(message, DateUtils.addSeconds(new Date(), 90));
            System.out.println("发送返回结果：" + JSON.toJSONString(sendResult));
            System.out.println("消息发送时间：" + String.format("%tF %<tT", new Date()));
        } catch (Exception e) {
            log.info("发送延时消息失败" + e);
        } finally {
            System.out.println("-----");
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
        return new HorizontalCellStyleStrategy(headWriteCellStyle, listCntWritCellSty);
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

    private static void setBorderStyle(WriteCellStyle contentWriteCellStyle) {
        //设置边框样式
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println("自增id输出-->{}" + i + "===>" + IdWorker.getId());
        }

        //1415574208741638146
        //1415574324001128450
        //1415578079618678786

        //1415574208741638155
    }
}

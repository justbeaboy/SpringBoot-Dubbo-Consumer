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
import com.nacos.test.controller.excel.WriteExcelUtilsPlus;
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

        long start = SystemClock.now();
        List<List<Object>> dataList = new ArrayList<>();
//
//        List<com.nacos.test.controller.redisdalaymq.util.Test> tests = new ArrayList<>();
//
//        tests.add(com.nacos.test.controller.redisdalaymq.util.Test.builder().name("??????").age(10).address("??????").build());
//        tests.add(com.nacos.test.controller.redisdalaymq.util.Test.builder().name("??????").age(20).address("??????").build());
//        tests.add(com.nacos.test.controller.redisdalaymq.util.Test.builder().name("??????").age(20).address("??????").build());
//        tests.add(com.nacos.test.controller.redisdalaymq.util.Test.builder().name("??????").age(10).address("??????").build());
//        for (Test test : tests) {
//            List<Object> objects = new ArrayList<>();
//            objects.add(test.getName());
//            objects.add(test.getAge());
//            objects.add(test.getAddress());
//            dataList.add(objects);
//        }


//        for (int i = 0; i < 10; i++) {
//            List<Object> objects = new ArrayList<>();
//            objects.add("?????????" + i);
//            objects.add("?????????" + i);
//            objects.add("?????????" + i);
//            dataList.add(objects);
//        }


        List<String> titleList = new ArrayList<>(2);
        titleList.add("ID");
        titleList.add("type_code");
        titleList.add("param_code");
        titleList.add("param_value");
        titleList.add("param_desc");
        titleList.add("sort");
        titleList.add("status");

        List<String> mergeTitles = new ArrayList<>(2);
        mergeTitles.add("??????");
        mergeTitles.add("??????");

        List<Dict> dicts = dictDaoSv.list();
        dicts.forEach(d -> {
            List<Object> objects = new ArrayList<>();
            objects.add(d.getDictId());
            objects.add(d.getTypeCode());
            objects.add(d.getParamCode());
            objects.add(d.getParamValue());
            objects.add(d.getParamDesc());
            objects.add(d.getSort());
            objects.add(d.getStatus());

            dataList.add(objects);
        });

//        WriteExcelUtils.customDynamicExport(response, "????????????", titleList, Collections.emptyList(), dataList);
        WriteExcelUtilsPlus.customDynamicExport(response,"????????????100w",titleList,Collections.emptyList(), dataList,"sheet???");
        log.info("??????100w????????????===???{}", (SystemClock.now() - start) / 1000);
    }

    @GetMapping(value = "/millionExport")
    public void millionExport(HttpServletResponse response) {

        long start = SystemClock.now();

        List<String> titleList = new ArrayList<>(2);
        titleList.add("ID");
        titleList.add("type_code");
        titleList.add("param_code");
        titleList.add("param_value");
        titleList.add("param_desc");
        titleList.add("sort");
        titleList.add("status");

        OutputStream outputStream = null;
        //????????????excel
        try {
            outputStream = response.getOutputStream();
        } catch (IOException ex) {
            log.error("????????????????????????,?????????????????????,????????????->{}", ex.toString());
        }
        try {
            setResponse(response, "ceshidaochu");

            List<List<String>> headTitleInfo = excelTitle(titleList);

            ExcelWriter excelWriter = new ExcelWriterBuilder()
                    .head(headTitleInfo)
                    .file(outputStream)
                    .excelType(ExcelTypeEnum.XLSX)
                    .autoCloseStream(true)
                    .registerWriteHandler(myHorizontalCellStyleStrategy())
                    .build();
            WriteSheet writeSheet = new WriteSheet();
            writeSheet.setSheetName("??????");

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
            log.error("??????????????????,????????????-->{}", ex.toString());
        } finally {
            if (outputStream != null) {

                try {
                    outputStream.close();
                } catch (IOException ex) {
                    log.error("??????????????????,????????????????????????-->{}", ex.toString());
                }
            }
        }
        log.info("??????100w????????????===???{}", (SystemClock.now() - start) / 1000);

    }

    private void send(int n) {
        DelayMessage delayMessage = new DelayMessage();
        delayMessage.setTopic("mrsoftrocktest");
        delayMessage.setTag("tag");
        delayMessage.setMessageKey(IdWorker.get32UUID());

        JSONObject body = new JSONObject();
        body.put("orderNo", "123456789+hello+88mesoftrock1" + n);
        body.put("status", "????????????5" + n);
        delayMessage.setMessageBody(body.toJSONString());
        //?????????1????????????
        delayMessage.setDelayTime(n * 60 * 1000L);
        messageProducer.sendDelayMessage(delayMessage);
    }

    private void sendDelayMq(int i) {
        //???????????????topic???????????????group??????
        DelayMqProducer producer = new DelayMqProducer("guava-group");
//        DelayMqProducer producer = new DelayMqProducer();
        try {

            producer.setNamesrvAddr("127.0.0.1:9876");
            producer.setRetryTimesWhenSendFailed(3);
            producer.start();
            Map<String, String> content = new HashMap<>();
            content.put("name", "mrsoftrock");
            content.put("message", "hello mrsoftrock------119" + i);
            //??????topic
            Message message = new Message("guava_hello_topic", null, JSON.toJSONBytes(content));
            SendResult sendResult = producer.sendDelay(message, DateUtils.addSeconds(new Date(), 90));
            System.out.println("?????????????????????" + JSON.toJSONString(sendResult));
            System.out.println("?????????????????????" + String.format("%tF %<tT", new Date()));
        } catch (Exception e) {
            log.info("????????????????????????" + e);
        } finally {
            System.out.println("-----");
        }
    }

    /**
     * ?????????????????????????????????
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
        //??????????????????
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //????????????????????????
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //???????????????????????????
        headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setBold(true);
        headWriteFont.setFontName("??????");
        headWriteFont.setFontHeightInPoints((short) 12);
        headWriteCellStyle.setWriteFont(headWriteFont);
        //??????????????????
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        WriteFont contentWriteFont = new WriteFont();
        //??????????????????
        contentWriteFont.setFontName("??????");
        contentWriteFont.setFontHeightInPoints((short) 11);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        //??????????????????
        contentWriteCellStyle.setWrapped(false);
        //??????????????????
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        //??????????????????
        setBorderStyle(contentWriteCellStyle);
        //?????????????????????????????????
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
        //??????????????????
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println("??????id??????-->{}" + i + "===>" + IdWorker.getId());
        }

        //1415574208741638146
        //1415574324001128450
        //1415578079618678786

        //1415574208741638155
    }
}

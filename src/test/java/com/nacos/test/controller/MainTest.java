package com.nacos.test.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.google.common.base.Stopwatch;
import com.nacos.test.controller.mapper.dao.IDictDaoSv;
import com.nacos.test.controller.mapper.dao.ISysSeqDaoSv;
import com.nacos.test.controller.mapper.entity.Dict;
import com.nacos.test.controller.mapper.entity.SysSeq;
import com.nacos.test.controller.redisdalaymq.service.RedisUtil;
import com.nacos.test.controller.search.document.MrsoftrockDocument;
import com.nacos.test.controller.search.repository.MrsoftrockDocRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Mr.SoftRock
 * @Date 2021/5/13 14:47
 **/
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class MainTest {
    @Autowired
    private RedisUtil redisUtil;

    @Value("${rocketmq.name-server}")
    private String nameServerAddress;

    @Autowired
    MrsoftrockDocRepository mrsoftrockDocRepository;

    @Autowired
    ElasticsearchRestTemplate template;

    @Autowired
    ISysSeqDaoSv sysSeqDaoSv;

    @Autowired
    IDictDaoSv dictDaoSv;


    @Test
    public void redisTest() {
//        String str = "{\"delayTime\":120,\"messageBody\":\"{\\\"orderNo\\\":\\\"123456789\\\",\\\"status\\\":\\\"????????????\\\"}\",\"messageKey\":\"f62761a1e5710893ad4b75f1395980f4\",\"sendTime\":1620888152072,\"tag\":\"tag\",\"topic\":\"mrsoftrocktest\"}";
//        // ???????????????
//        DelayMessage delayMessage = JSONObject.parseObject(str, DelayMessage.class);
//        redisUtil.putHashValue("dq-metaDataHashKey", delayMessage.getMessageKey(), JSON.toJSONString(delayMessage));
//        redisUtil.setObject("k6", "v6");
//        System.out.println(redisUtil.getObject("k6"));
        System.out.println(IdWorker.getId());

    }

    @Test
    public void qrCode() throws IOException {
        String txt = "D???12???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\n" +
                "BUT!!!\n" +
                "???????????????????????????????????????????????????????????????show?????????????????????????????????????????????\n" +
                "??????????????????????????????????????????????????????????????????????????????????????????\n" +
                "??????????????????????????????????????????????????????????????????\n" +
                "???????????????\n";
//        QrCodeUtil.generate(txt, 300, 300, FileUtil.file("/Users/mr.softrock/Documents/Mr.SoftRock/????????????/I_advise_you_to_be_kind.jpg"));
//        System.out.println( );
//        byte[] decode = Base64.getDecoder().decode(txt);
//        ByteArrayInputStream bais = new ByteArrayInputStream(decode);
//        BufferedImage bi1 = ImageIO.read(bais);
//        try {
//            File w2 = new File("/Users/mr.softrock/Documents/Mr.SoftRock/????????????/qrCode.jpg");//?????????jpg,png,gif??????
//            ImageIO.write(bi1, "jpg", w2);//???????????????????????????????????????????????????
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally{
//            bais.close();
//        }
    }

    @Test
    public void esTest() {
        MrsoftrockDocument document = new MrsoftrockDocument();
        //????????????
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("name", "??????"));
//        boolQueryBuilder.should(QueryBuilders.termQuery("deptName.keyword", "????????????"));

        NativeSearchQueryBuilder nativeQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder);
        NativeSearchQuery searchQuery = nativeQueryBuilder.build();

        SearchHits<MrsoftrockDocument> search = template.search(searchQuery, MrsoftrockDocument.class);
        final List<SearchHit<MrsoftrockDocument>> collect = search.stream().collect(Collectors.toList());
        System.out.println(JSONObject.toJSONString(collect));

    }

    @Test
    public void lambdaTest() {
        List<Long> list = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);
        List<String> strList = Arrays.asList("a", "b", "c", "d", "e", "f", "g");

        Map<Boolean, List<Long>> map = list.stream().collect(Collectors.partitioningBy(x -> x > 3L));
        System.out.print("map:" + JSONObject.toJSONString(map));

        List<Long> skipList = list.stream().filter(x -> x > 3L).skip(1).collect(Collectors.toList());
        System.out.print("skipList:" + JSONObject.toJSONString(skipList));

        List<String> words = Arrays.asList("Hello", "Mr.SoftRock");
        List<String> uniqueCharacters = words.stream().map(x -> x.split("")).flatMap(Arrays::stream).collect(Collectors.toList());
        System.out.println("uniqueCharacters:" + JSONObject.toJSONString(uniqueCharacters));
//        int sum
    }

    @Test
    public void canalTest() {
        List<SysSeq> sysSeqs = sysSeqDaoSv.lambdaQuery().list();
        System.out.println(JSONObject.toJSON(sysSeqs));
    }

    @Test
    public void excelTest() {
        //????????????100w?????????
//        List<Future<?>> futures = new ArrayList<>();
        final Stopwatch stopwatch = Stopwatch.createStarted();
        List<CompletableFuture> futureList = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            List<Dict> dicts = new ArrayList<>(1000);
            for (int j = 0; j < 900; j++) {
                Dict dict = new Dict();
                dict.setDictId(IdWorker.getId());
                dict.setTypeCode("test");
                dict.setParamCode("test");
                dict.setParamValue("1");
                dict.setParamDesc(String.valueOf(j));
                dict.setSort(0);
                dict.setRemark("????????????");
                dict.setStatus("1");
                dicts.add(dict);
            }
            dictDaoSv.saveBatch(dicts);
//            CompletableFuture future = CompletableFuture.runAsync(() -> {
//
//                    }
//
//            );
//            futureList.add(future);
        }
//        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
        System.out.println("?????????????????????????????????{}" + stopwatch.elapsed(TimeUnit.SECONDS));
    }

}

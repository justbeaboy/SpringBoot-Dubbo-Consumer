package com.nacos.test.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.nacos.test.controller.redisdalaymq.message.DelayMessage;
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
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

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

    @Test
    public void redisTest() {
//        String str = "{\"delayTime\":120,\"messageBody\":\"{\\\"orderNo\\\":\\\"123456789\\\",\\\"status\\\":\\\"已经过期\\\"}\",\"messageKey\":\"f62761a1e5710893ad4b75f1395980f4\",\"sendTime\":1620888152072,\"tag\":\"tag\",\"topic\":\"mrsoftrocktest\"}";
//        // 添加元数据
//        DelayMessage delayMessage = JSONObject.parseObject(str, DelayMessage.class);
//        redisUtil.putHashValue("dq-metaDataHashKey", delayMessage.getMessageKey(), JSON.toJSONString(delayMessage));
//        redisUtil.setObject("k6", "v6");
//        System.out.println(redisUtil.getObject("k6"));
        System.out.println(IdWorker.getId());

    }

    @Test
    public void qrCode() throws IOException {
        String txt = "D栋12层的一些同事，每天跟有毒似的，放眼一整个工作日，你也没怎么表现出你是多么的热爱工作，频繁的做着渔夫的行为。\n" +
                "BUT!!!\n" +
                "每天就趁你中午仅有的那一点点休息时间，开始show自己疯狂的工作欲，讨论，闲聊。\n" +
                "好像，自己真的感受不到，来自其他同事那渴望休息的绝望的目光。\n" +
                "跪求，就中午那一点点的休息时间，还给我们吧。\n" +
                "我劝你善良\n";
        QrCodeUtil.generate(txt, 300, 300, FileUtil.file("/Users/mr.softrock/Documents/Mr.SoftRock/个人文件/I_advise_you_to_be_kind.jpg"));
//        System.out.println( );
//        byte[] decode = Base64.getDecoder().decode(txt);
//        ByteArrayInputStream bais = new ByteArrayInputStream(decode);
//        BufferedImage bi1 = ImageIO.read(bais);
//        try {
//            File w2 = new File("/Users/mr.softrock/Documents/Mr.SoftRock/个人文件/qrCode.jpg");//可以是jpg,png,gif格式
//            ImageIO.write(bi1, "jpg", w2);//不管输出什么格式图片，此处不需改动
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally{
//            bais.close();
//        }
    }

    @Test
    public void esTest(){
        MrsoftrockDocument document = new MrsoftrockDocument();
        //查询条件
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("name", "中国"));
//        boolQueryBuilder.should(QueryBuilders.termQuery("deptName.keyword", "测试部门"));
//        mrsoftrockDocRepository.f
        NativeSearchQueryBuilder nativeQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder);
        NativeSearchQuery searchQuery = nativeQueryBuilder.build();

        SearchHits<MrsoftrockDocument> search = template.search(searchQuery, MrsoftrockDocument.class);
        System.out.println(search.get());
//        Iterable<MrsoftrockDocument> search = mrsoftrockDocRepository.search()
//                  .search(boolQueryBuilder);
        //如果需要排序，可以考虑查询完了使用java 来排序

    }

}

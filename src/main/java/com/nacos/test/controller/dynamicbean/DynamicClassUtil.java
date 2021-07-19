package com.nacos.test.controller.dynamicbean;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import com.google.common.collect.Maps;
import com.nacos.test.controller.redisdalaymq.util.Test;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.beans.PropertyDescriptor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.SoftRock
 * @Date 2021/7/19 10:51
 **/
public class DynamicClassUtil {


    public static Object getObject(Object dest, Map<String, Object> addProperties) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(dest);
        Map<String, Class<?>> propertyMap = Maps.newHashMap();
        for (PropertyDescriptor d : descriptors) {
            System.out.println("d.getname--->" + d.getName());
            if (!"class".equalsIgnoreCase(d.getName())) {
                propertyMap.put(d.getName(), d.getPropertyType());
            }
        }
        addProperties.forEach((k, v) -> {
            String sclass = v.getClass().toString();
            System.out.println("sclass-->" + sclass);
            if ("class java.util.Date".equals(sclass)) {//对日期进行处理
                propertyMap.put(k, Long.class);
            } else {
                propertyMap.put(k, v.getClass());
            }

        });
        DynamicBean dynamicBean = new DynamicBean(dest.getClass(), propertyMap);
        propertyMap.forEach((k, v) -> {
            try {
                if (!addProperties.containsKey(k)) {
                    dynamicBean.setValue(k, propertyUtilsBean.getNestedProperty(dest, k));
                }
            } catch (Exception e) {
                System.out.println("动态添加字段出错-->" + e);
            }
        });
        addProperties.forEach((k, v) -> {
            try {
                String sclass = v.getClass().toString();
                //动态添加的字段为date类型需要进行处理
                if ("class java.util.Date".equals(sclass)) {
                    Date date = (Date) v;
                    dynamicBean.setValue(k, date.getTime());
                } else {
                    dynamicBean.setValue(k, v);
                }
            } catch (Exception e) {
                System.out.println("动态添加字段出错-->" + e);

            }
        });
        return dynamicBean.getObject();
    }


    public static void main(String[] args) {
        Test test = new Test();
        test.setName("张三");

        System.out.println("Test：" + JSON.toJSONString(test));


        Map<String, Object> propertiesMap = new HashMap<String, Object>();
        propertiesMap.put("email", "111.qq.com");

        propertiesMap.put("date", new Date(SystemClock.now()));
        propertiesMap.put("duble", 1d);
        Test obj = (Test) DynamicClassUtil.getObject(test, propertiesMap);
        System.out.println("动态为Test添加属性之后，Test：" + JSON.toJSONString(obj) + "-----" + obj.getClass());
//        List<Test1> test1s = new ArrayList<>();
//        Test1 test1 = new Test1();
//        test1.setName("test1 张三");
//        test1s.add(test1);
//        Test1 test12 = new Test1();
//        test12.setName("test1 张三");
//        test1s.add(test12);

//
//        try {
//            HashMap addMap = new HashMap();
//            HashMap addValMap = new HashMap();
//            addMap.put("Test1", Class.forName("java.util.List"));
//            addValMap.put("test1s", test1s);
//            Object obj2 = new ClassUtil().dynamicClass(test, addMap, addValMap);
//
//            System.out.println(JSON.toJSONString(obj2));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

}

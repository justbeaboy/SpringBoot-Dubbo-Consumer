package com.nacos.test.controller.dynamicbean;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;

import java.util.Map;


/**
 * @author Mr.SoftRock
 * @Date 2021/7/19 10:49
 **/
public class DynamicBean {
    /**
     * 动态生成的类
     */
    private Object object;
    /**
     * 存放属性名称以及属性的类型
     */
    private BeanMap beanMap;


    public DynamicBean(Class<?> superclass, Map<String, Class<?>> propertyMap) {
        this.object = generateBean(superclass, propertyMap);
        this.beanMap = BeanMap.create(this.object);
    }



    /**
     * 根据属性生成对象
     */
    private Object generateBean(Class<?> superclass, Map<String, Class<?>> propertyMap) {
        BeanGenerator generator = new BeanGenerator();
        if (null != superclass) {
            generator.setSuperclass(superclass);
        }
        BeanGenerator.addProperties(generator, propertyMap);
        return generator.create();
    }

    /**
     * 给bean属性赋值
     *
     * @param property 属性名
     * @param value    值
     */
    public void setValue(Object property, Object value) {
        beanMap.put(property, value);
    }

    /**
     * 通过属性名得到属性值
     *
     * @param property 属性名
     * @return 值
     */
    public Object getValue(String property) {
        return beanMap.get(property);
    }

    /**
     * 得到该实体bean对象
     *
     * @return
     */
    public Object getObject() {
        return this.object;

    }

}

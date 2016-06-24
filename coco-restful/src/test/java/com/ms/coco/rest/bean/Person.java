package com.ms.coco.rest.bean;

/**
* @author wanglin/netboy
* @version 创建时间：2016年6月13日 下午9:03:13
* @func 
*/
public class Person {
    private String id;
    private String name;
    private Integer age;

    public Person(String id, String name, Integer age) {
        super();
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }



}

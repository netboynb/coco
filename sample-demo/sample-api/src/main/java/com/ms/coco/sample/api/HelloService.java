package com.ms.coco.sample.api;

public interface HelloService {

    String hello(String name);

    String hello(Person person);

    Person add(Person person);

    Person addWithException(Person person) throws Exception;

    Integer parseIntStr(String str);

    Integer parseIntStr();
}

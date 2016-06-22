package com.ms.coco.sample.api;

import java.util.List;

public interface HelloService {

    String hello(String name);

    String hello(Person person);

    Person add(Person person);

    Person addWithException(Person person) throws Exception;

    List<Person> fetchList(Integer num);

    Integer parseIntStr(String str);

    Integer parseIntStr();
}

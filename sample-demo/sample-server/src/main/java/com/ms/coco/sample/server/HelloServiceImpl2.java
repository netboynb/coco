package com.ms.coco.sample.server;

import java.util.concurrent.TimeoutException;

import com.ms.coco.sample.api.HelloService;
import com.ms.coco.sample.api.Person;
import com.ms.coco.server.RpcService;

@RpcService(value = HelloService.class, version = "sample.hello2")
public class HelloServiceImpl2 implements HelloService {

    @Override
    public String hello(String name) {
        return ("Hello! " + name);
    }

    @Override
    public String hello(Person person) {
        return ("Hello! " + person.getFirstName() + " " + person.getLastName());
    }

    @Override
    public Person addWithException(Person person) throws Exception {
        if (true) {
            throw new TimeoutException("2 biz timeout");
        }
        return person;
    }

    @Override
    public Integer parseIntStr(String str) {
        return Integer.parseInt(str);
    }

    @Override
    public Person add(Person person) {
        return person;
    }

    @Override
    public Integer parseIntStr() {
        return Integer.parseInt("ll");
    }

}

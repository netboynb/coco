package com.ms.coco.rpc.server;

import java.util.concurrent.TimeoutException;

import com.ms.coco.sample.api.HelloService;
import com.ms.coco.sample.api.Person;
import com.ms.coco.server.service.RpcService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    String content = "###World#启用熔断  达到阀值后启用熔断 丢掉所有的请求 一个 sleepWindowInMilliseconds 周期后 尝试放进一个请求，若成功则放进后续的请求 否则循环###";
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
        person.setContent(content);
        if (true) {
            throw new TimeoutException("这个是业务端异常   biz timeout");
        }
        return person;
    }

    @Override
    public Integer parseIntStr(String str) {
        return Integer.parseInt(str);
    }

    @Override
    public Integer parseIntStr() {
        return Integer.parseInt("ll");
    }

    @Override
    public Person add(Person person) {
        person.setContent(content);
        return person;
    }
}

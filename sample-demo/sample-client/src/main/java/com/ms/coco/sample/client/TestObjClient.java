package com.ms.coco.sample.client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ms.coco.client.RpcProxy;
import com.ms.coco.exception.RpcFrameworkException;
import com.ms.coco.sample.api.HelloService;
import com.ms.coco.sample.api.Person;

public class TestObjClient {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);

        HelloService helloService = rpcProxy.create(HelloService.class);


        long start = System.currentTimeMillis();
        int loopCount = 1000000;
        for (Long i = 0L; i < loopCount; i++) {
            try {
                long s = System.currentTimeMillis();
                Person person = helloService.add(new Person(i, "林", "王"));
                // Person person = helloService.addWithException(new Person(i, "林", "王"));
                // String name = helloService.hello("hello");
                // Integer count = helloService.parseIntStr(i + "");
                // count = helloService.parseIntStr(null);
                // count = helloService.parseIntStr("");
                long use = System.currentTimeMillis() - s;
                if (i % 10000 == 0) {
                    System.out.println(" id = " + person.getId() + " use= " + use + "  ms " + person.getContent());
                }

            } catch (Exception e) {
                if (e instanceof RpcFrameworkException) {
                    RpcFrameworkException rpcFrameworkException = (RpcFrameworkException) e;
                    System.out.println("rpcErrorCode=" + rpcFrameworkException.getErrorCode());
                }
                System.out.println("++++++" + e.getMessage());
            }
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("loop: " + loopCount);
        System.out.println("time: " + time + "ms");
        System.out.println("rt: " + ((double) time / loopCount));
        System.out.println("tps: " + (double) loopCount / ((double) time / 1000));

        System.exit(0);
    }
}

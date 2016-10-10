package com.ms.coco.sample.client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ms.coco.client.RpcProxy;
import com.ms.coco.sample.api.HelloService;

public class HelloDemo {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);
        while (true) {
            HelloService helloService = rpcProxy.create(HelloService.class);
            String result = helloService.hello("World");
            System.out.println(result);
            Thread.sleep(1000);
        }
        // System.exit(0);
    }
}

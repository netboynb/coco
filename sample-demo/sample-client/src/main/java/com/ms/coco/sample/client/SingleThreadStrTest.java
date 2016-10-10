package com.ms.coco.sample.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ms.coco.client.RpcProxy;
import com.ms.coco.sample.api.HelloService;

public class SingleThreadStrTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleThreadStrTest.class);
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);
        String content =
                "###World#启用熔断  达到阀值后启用熔断 丢掉所有的请求 一个 sleepWindowInMilliseconds 周期后 尝试放进一个请求，若成功则放进后续的请求 否则循环###";
        int loopCount = 1000000;

        long start = System.currentTimeMillis();

        HelloService helloService = rpcProxy.create(HelloService.class);
        int num = 0;
        for (int i = 0; i < loopCount; i++) {
            try {
                long s = System.currentTimeMillis();
                String result = helloService.hello(content);
                // int temp = helloService.parseIntStr("test");
                long use = System.currentTimeMillis() - s;
                System.out.println(num + " = " + use + " ms " + result);
            } catch (Exception e) {
                LOGGER.error("++++++" + e.toString());
            }

            num++;
        }

        long time = System.currentTimeMillis() - start;
        System.out.println("loop: " + loopCount);
        System.out.println("time: " + time + "ms");
        System.out.println("rt: " + (double) time / loopCount);
        System.out.println("tps: " + (double) loopCount / ((double) time / 1000));

        System.exit(0);
    }
}

package com.ms.coco.sample.client.rest;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.ms.coco.sample.api.HelloService;

/**
* @author wanglin/netboy
* @version 创建时间：2016年6月23日 下午5:42:58
* @func 
*/
public class ResteasyProxyClient {

    public static void main(String[] args) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target("http://localhost:8089/rest/hello/v1");

        HelloService helloService = target.proxy(HelloService.class);


        int num = 0;
        int loopCount = 100000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < loopCount; i++) {
            try {
                long s = System.currentTimeMillis();
                String result = helloService.hello("World");
                // int temp = helloService.parseIntStr("test");
                long use = System.currentTimeMillis() - s;
                System.out.println(num + " = " + use + " ms " + result);
            } catch (Exception e) {
                System.err.println("++++++" + e.toString());
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

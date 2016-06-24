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

        HelloService simple = target.proxy(HelloService.class);
        String result = simple.hello("World");
        System.out.println(result);
    }

}

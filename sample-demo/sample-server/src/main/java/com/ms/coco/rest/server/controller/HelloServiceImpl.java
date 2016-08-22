package com.ms.coco.rest.server.controller;

import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Controller;

import com.google.common.collect.Lists;
import com.ms.coco.sample.api.HelloService;
import com.ms.coco.sample.api.Person;
import com.ms.coco.server.service.RpcService;

@Controller
@Path("/hello/v1")
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    String content = "###World#启用熔断  达到阀值后启用熔断 丢掉所有的请求 一个 sleepWindowInMilliseconds 周期后 尝试放进一个请求，若成功则放进后续的请求 否则循环###";

    /**
     * 
     * TODO: http://localhost:8082/rest/hello/v1/echo?name=netboy
     */
    @GET
    @Path("/echo")
    @Produces("application/json")
    @Override
    public String hello(@QueryParam("name") String name) {
        
        return "{\"result\":\"hello world\"}";
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public String hello(Person person) {
        return ("Hello! " + person.getFirstName() + " " + person.getLastName());
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Person addWithException(Person person) throws Exception {
        person.setContent(content);
        if (true) {
            throw new TimeoutException("这个是业务端异常   biz timeout");
        }
        return person;
    }

    /**
     * 
     * TODO: http://localhost:8082/rest/hello/v1/int?key=223
     */
    @GET
    @Path("/int")
    @Produces("application/json")
    @Override
    public Integer parseIntStr(@QueryParam("key") String str) {
        return Integer.parseInt(str);
    }

    @GET
    @Path("/parseIntStr")
    @Produces("application/json")
    @Override
    public Integer parseIntStr() {
        return Integer.parseInt("ll");
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Person add(Person person) {
        person.setContent(content);
        return person;
    }

    // http://localhost:8082/rest/hello/v1/list?num=3
    @GET
    @Path("/list")
    @Produces("application/json")
    @Override
    public List<Person> fetchList(@QueryParam("num") Integer num) {
        List<Person> list = Lists.newArrayList();
        for (long i = 0; i < num; i++) {
            list.add(new Person(i, "aa", "bb"));
        }
        return list;
    }
}

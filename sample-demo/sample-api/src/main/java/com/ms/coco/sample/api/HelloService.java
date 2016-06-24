package com.ms.coco.sample.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

public interface HelloService {

    /**
     * 
     * TODO: http://localhost:8082/rest/hello/v1/echo?name=netboy
     */
    @GET
    @Path("/echo")
    @Produces("application/json")
    public String hello(@QueryParam("name") String name);

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String hello(Person person);

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Person addWithException(Person person) throws Exception;

    /**
     * 
     * TODO: http://localhost:8082/rest/hello/v1/int?key=223
     */
    @GET
    @Path("/int")
    @Produces("application/json")
    public Integer parseIntStr(@QueryParam("key") String str);

    @GET
    @Path("/parseIntStr")
    @Produces("application/json")
    public Integer parseIntStr();

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Person add(Person person);

    // http://localhost:8082/rest/hello/v1/list?num=3
    @GET
    @Path("/list")
    @Produces("application/json")
    public List<Person> fetchList(@QueryParam("num") Integer num);
}

package com.ms.coco.sample.client.rest;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年6月23日 下午5:24:03
 * @func more detail: https://docs.jboss.org/resteasy/docs/3.0-beta-3/userguide/html/
 *       RESTEasy_Client_Framework.html
 */
public class RestClientDemo {
    public static void main(String[] args) throws Exception {
        // Client client = ClientBuilder.newClient();
        // Client client = ClientBuilder.newBuilder().build();
        // WebTarget target = client.target("http://localhost:8082/rest/hello/v1/echo?name=netboy");
        // Response response = target.request().get();
        // String value = response.readEntity(String.class);
        // response.close();

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target("http://localhost:8082/rest/hello/v1/echo?name=netboy");
        Response response = target.request().get();
        String value = response.readEntity(String.class);
        System.out.println(value);
        response.close();

    }
}

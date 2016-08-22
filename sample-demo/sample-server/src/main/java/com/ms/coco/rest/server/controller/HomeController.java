package com.ms.coco.rest.server.controller;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.springframework.stereotype.Controller;

import com.carkey.base.model.Proto;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.ms.coco.rest.bean.Article;
import com.ms.coco.rest.bean.Helloworld;
import com.ms.coco.rest.bean.Person;


@Controller
@Path("/home")
public class HomeController {
    private Gson gson = new Gson();

    @POST
	@Path("/world")
    @Produces({MediaType.APPLICATION_JSON})
    public String helloworld(String msg) throws Exception {
        Map<String, Object> map = Maps.newHashMap();
        Proto proto = new Proto();
        proto.setCode(0);
        proto.setData(msg);
        proto.setMsg(msg);
        map.put("result", gson.toJson(proto));
        return gson.toJson(map);
	}

    /**
     * 
     * TODO: http://localhost:8082/rest/home/book/222
     */
	@GET
    @Path("/book/{isbn}")
    @Produces("application/json")
    public String getBook(@PathParam("isbn") String id) {
        // search my database and get a string representation and return it
        return id;
    }

    /**
     * 
     * TODO: http://localhost:8082/rest/home/book2/222?name=kkll&age=22
     */
    @GET
    @Path("/book2/{isbn}")
    @Produces("application/json")
    public Person addBookGet(@PathParam("isbn") String id, @QueryParam("name") String name,
            @QueryParam("age") Integer age) {
        return new Person(id, name, age);
    }

    @PUT
    @Path("/book/{isbn}")
    @Produces("application/json")
    public Helloworld addBook(@PathParam("isbn") String id, @QueryParam("name") String name) {
        return new Helloworld(id, name);
    }

    @DELETE
    @Path("/book/{id}")
    @Produces("application/json")
    public void removeBook(@PathParam("id") String id) {
        return;
    }

    @GET
	@Path("/auth")
	@Produces("application/json")
	public Helloworld auth(@Context SecurityContext context) {
		return new Helloworld(context.getUserPrincipal().getName());
	}


	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Article save(Article article) {
		return article;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Article> save(
			@QueryParam("multi") boolean isMulti,
			List<Article> articles) {
		return articles;
	}

}

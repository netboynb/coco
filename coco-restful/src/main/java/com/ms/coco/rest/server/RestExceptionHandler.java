package com.ms.coco.rest.server;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

/**
* @author wanglin/netboy
* @version 创建时间：2016年6月23日 下午2:03:34
* @func 
*/

@Component
@Provider
public class RestExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        String msg = e.getMessage();
        StringBuilder response = new StringBuilder("<response>");
        response.append("<status>failed</status>");
        response.append("<message>" + msg + "</message>");
        response.append("</response>");

        return Response.serverError().entity(response.toString()).type("application/json").build();
    }

}

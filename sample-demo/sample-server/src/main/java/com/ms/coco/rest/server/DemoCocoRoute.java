package com.ms.coco.rest.server;

import org.restexpress.RestExpress;

import com.ms.coco.echo.controller.EchoController;
import com.ms.coco.echo.controller.StatusController;
import com.ms.coco.echo.controller.SuccessController;
import com.ms.coco.route.CocoRoute;

import io.netty.handler.codec.http.HttpMethod;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年6月8日 下午8:17:16
 * @func
 */
public class DemoCocoRoute implements CocoRoute {

    @Override
    public void defineRoutes(RestExpress server) {
        server.uri("/echo/{delay_ms}", new EchoController()).noSerialization()
            .action("readAll", HttpMethod.POST)
            .method(HttpMethod.GET,HttpMethod.DELETE,HttpMethod.PUT)
            .name("coco.echo.route");

        server.uri("/success/{delay_ms}.{format}", new SuccessController())
        .action("readAll", HttpMethod.POST)
        .method(HttpMethod.GET,HttpMethod.DELETE,HttpMethod.PUT)
        .name("coco.success.route");;

        server.uri("/status/{delay_ms}/{http_response_code}.{format}", new StatusController());
    }

    @Override
    public void init() {

    }

}

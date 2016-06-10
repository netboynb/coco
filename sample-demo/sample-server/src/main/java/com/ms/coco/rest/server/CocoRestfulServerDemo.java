package com.ms.coco.rest.server;

import com.ms.coco.CocoRestServer;
import com.ms.coco.common.CocoMessageObserver;
import com.ms.coco.common.LastModifiedHeaderPostprocessor;
import com.ms.coco.conf.DefaultCocoConf;
import com.ms.coco.echo.DemoCocoRoute;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年6月8日 下午9:32:42
 * @func y演示单独的restful 服务，不启用rpc服务
 */
public class CocoRestfulServerDemo {

    public static void main(String[] args) {
        CocoRestServer server = new CocoRestServer();
        server.setCocoConf(new DefaultCocoConf());
        server.setCocoRoute(new DemoCocoRoute());
        server.setMessageObserver(new CocoMessageObserver());
        server.setPostprocessor(new LastModifiedHeaderPostprocessor());
        server.setPort(8089);
        server.init();
        server.start();
    }

}

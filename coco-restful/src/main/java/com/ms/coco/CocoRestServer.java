package com.ms.coco;

import org.restexpress.RestExpress;
import org.restexpress.pipeline.MessageObserver;
import org.restexpress.pipeline.Postprocessor;

import com.ms.coco.common.CocoMapExceptions;
import com.ms.coco.common.CocoMessageObserver;
import com.ms.coco.common.LastModifiedHeaderPostprocessor;
import com.ms.coco.conf.CocoConf;
import com.ms.coco.route.CocoRoute;
import com.ms.coco.serialization.SerializationProvider;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年6月8日 下午7:12:08
 * @func
 */
public class CocoRestServer {
    private CocoConf cocoConf;
    private CocoRoute cocoRoute;
    private CocoMapExceptions cocoMapExceptions;
    private MessageObserver messageObserver;
    private Postprocessor postprocessor;
    private Integer port = 8089;
    public void init() {
        if (cocoConf == null) {

        } else {
            cocoConf.init();
        }

        if (cocoRoute == null) {
            throw new RuntimeException("CocoRoute not implement,you must define the route rule");
        } else {
            cocoRoute.init();
        }

        if (cocoMapExceptions != null) {
            cocoMapExceptions.init();
        }

    }
    public void start(){
        RestExpress.setDefaultSerializationProvider(new SerializationProvider());

        if (cocoConf.getPort() != null) {
            port = cocoConf.getPort();
        }
        RestExpress server = new RestExpress().setName(cocoConf.getServiceName()).setPort(port);
        if (messageObserver != null) {
            server.addMessageObserver(messageObserver);
        } else {
            server.addMessageObserver(new CocoMessageObserver());
        }
        if (postprocessor != null) {
            server.addPostprocessor(postprocessor);
        } else {
            server.addPostprocessor(new LastModifiedHeaderPostprocessor());
        }
        cocoRoute.defineRoutes(server);

        int coreSize = Runtime.getRuntime().availableProcessors();
        if (cocoConf.getWorkerCount() > 0) {
            server.setIoThreadCount(cocoConf.getWorkerCount());
        } else {
            server.setIoThreadCount(coreSize * 2);
        }

        if (cocoConf.getExecutorThreadCount() > 0) {
            server.setExecutorThreadCount(cocoConf.getExecutorThreadCount());
        } else {
            server.setExecutorThreadCount(coreSize * 2 + 2);
        }
        if (cocoMapExceptions != null) {
            cocoMapExceptions.doMap(server);
        }
        server.bind();
        server.awaitShutdown();
    }

    public void setCocoConf(CocoConf cocoConf) {
        this.cocoConf = cocoConf;
    }

    public void setCocoRoute(CocoRoute cocoRoute) {
        this.cocoRoute = cocoRoute;
    }

    public void setCocoMapExceptions(CocoMapExceptions cocoMapExceptions) {
        this.cocoMapExceptions = cocoMapExceptions;
    }

    public void setMessageObserver(MessageObserver messageObserver) {
        this.messageObserver = messageObserver;
    }

    public void setPostprocessor(Postprocessor postprocessor) {
        this.postprocessor = postprocessor;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

}

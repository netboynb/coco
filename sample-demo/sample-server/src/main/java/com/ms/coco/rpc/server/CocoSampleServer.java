package com.ms.coco.rpc.server;

import org.apache.curator.test.TestingServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * TODO: 根据配置 rpc 、restful 服务
 * 
 * @author netboy
 */
public class CocoSampleServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CocoSampleServer.class);

    public static void main(String[] args) throws Exception {
        System.out.println("start ms test zookeeper");
        TestingServer zkServer = new TestingServer(2182);
        zkServer.start();
        LOGGER.debug("start server");
        System.out.println("start rpc sample server");
        new ClassPathXmlApplicationContext("spring.xml");
    }
}

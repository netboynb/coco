package com.ms.coco.sample.server;

import org.apache.curator.test.TestingServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

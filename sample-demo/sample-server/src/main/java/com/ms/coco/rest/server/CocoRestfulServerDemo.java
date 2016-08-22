package com.ms.coco.rest.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年6月8日 下午9:32:42
 * @func y演示单独的restful 服务，不启用rpc服务
 */
public class CocoRestfulServerDemo {

    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("spring-single-restful.xml");
        CocoRestServer server = new CocoRestServer();
        server.setPort(9082).setRootResourcePath("/rest").setAc(ac);
        server.start();
    }

}

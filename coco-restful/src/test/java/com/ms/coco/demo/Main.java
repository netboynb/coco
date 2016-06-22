package com.ms.coco.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import com.ms.coco.controller.HomeController;
import com.ms.coco.server.CocoRestServer;

public class Main {

	public static void main(String[] args)
			throws Exception {

		ApplicationContext ac = new ClassPathXmlApplicationContext("root-context.xml");
		Assert.notNull(ac);
		Assert.notNull(ac.getBean(HomeController.class));

        CocoRestServer restNetty = ac.getBean(CocoRestServer.class);

		restNetty.start();

	}
}

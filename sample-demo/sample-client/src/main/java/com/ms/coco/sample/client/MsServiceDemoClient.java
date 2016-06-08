package com.ms.coco.sample.client;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ms.coco.client.RpcProxy;
import com.ms.coco.sample.api.HelloService;
import com.ms.coco.sample.api.Person;

/**
* @author wanglin/netboy
* @version 创建时间：2016年6月1日 上午10:37:33
* @func 
*/
public class MsServiceDemoClient {
    private HelloService helloService;
    private HelloService helloService2;
    @Before
    public void before() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);
        helloService = rpcProxy.create(HelloService.class);
        helloService2 = rpcProxy.create(HelloService.class, "sample.hello2");
    }

    @Test
    public void testStr(){
        String result = helloService.hello("World");
        System.out.println("helloService=" + result);

        String result2 = helloService2.hello("世界");
        System.out.println("helloService2=" + result2);
    }

    @Test
    public void testAddWithException() {
        try {
            Person person = helloService.addWithException(new Person(22L, "林", "王"));
            System.out.println(person.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseNot() {
        try {
            helloService.parseIntStr();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testparseIntStr() {
        Integer num;
        for (int i = 0; i < 3; i++) {
            num = i % 3;
            try {
                if (num == 0) {
                    num = helloService.parseIntStr(null);
                } else if (num == 1) {
                    num = helloService.parseIntStr(i + "");
                } else {
                    num = helloService.parseIntStr("test");
                }
                System.out.println(num);
            } catch (Exception e) {
                Throwable temp = e.getCause();
                System.out.println(temp == null ? null : temp.getMessage());
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

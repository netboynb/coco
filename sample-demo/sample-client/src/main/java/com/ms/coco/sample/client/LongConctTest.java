package com.ms.coco.sample.client;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;
import com.ms.coco.client.RpcProxy;
import com.ms.coco.sample.api.HelloService;

/**
* @author wanglin/netboy
* @version 创建时间：2016年10月9日 上午10:06:22
* @func 
*/
public class LongConctTest {

    public class Task implements Callable<Long> {
        private int num;
        private CountDownLatch latch;
        RpcProxy rpcProxy;
        long loopCount = 10000;

        public Task(final int num, CountDownLatch latch, RpcProxy rpcProxy) {
            this.num = num;
            this.latch = latch;
            this.rpcProxy = rpcProxy;
        }

        @Override
        public Long call() {
            HelloService helloService = rpcProxy.create(HelloService.class);
            long start = System.currentTimeMillis();
            for (int i = 0; i < loopCount; i++) {
                String result = helloService.hello(
                        "###World#启用熔断  达到阀值后启用熔断 丢掉所有的请求 一个 sleepWindowInMilliseconds 周期后 尝试放进一个请求，若成功则放进后续的请求 否则循环###");
                System.out.println(num + "-" + i + " = " + result);
            }

            long time = System.currentTimeMillis() - start;
            System.out.println(num + "-loop: " + loopCount);
            System.out.println(num + "-time: " + time + "ms");
            System.out.println(num + "-rt: " + ((double) time / loopCount));
            System.out.println(num + "-tps: " + (double) loopCount / ((double) time / 1000));
            latch.countDown();
            return time;
        }

    }
    public static class Msg {
        long num;
        long usetime;
        double qps;
        double tps;

        public Msg(Long num, long usetime, double qps, double tps) {
            super();
            this.num = num;
            this.usetime = usetime;
            this.qps = qps;
            this.tps = tps;
        }

        public Msg() {
            super();
        }

        @Override
        public String toString() {
            return "thread num=" + num + ", use time =" + usetime + ", qps=" + qps + ",tps=" + tps;
        }
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        final RpcProxy rpcProxy = context.getBean(RpcProxy.class);
        final HelloService helloService = rpcProxy.create(HelloService.class);
        int threadNum = 1000;
        final int loopCount = 2000000;
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        final CountDownLatch latch = new CountDownLatch(threadNum);
        final AtomicLong threadCount = new AtomicLong(0);
        try {
            java.util.List<Future<Msg>> resultList = Lists.newArrayList();

            for (int j = 0; j < threadNum; j++) {
                Future<Msg> future = executor.submit(new Callable<Msg>() {
                    @Override
                    public Msg call() throws InterruptedException {
                        String result = helloService.hello(" world");
                        System.out.println(threadCount.incrementAndGet() + "=" + result);
                        Thread.sleep(loopCount);
                        latch.countDown();
                        return null;
                    }
                });
                resultList.add(future);
            }
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        System.exit(0);
    }

}

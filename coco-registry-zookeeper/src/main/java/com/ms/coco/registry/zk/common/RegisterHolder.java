package com.ms.coco.registry.zk.common;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.curator.framework.CuratorFramework;

import com.google.common.collect.Maps;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年6月11日 下午11:27:00
 * @func
 */
public class RegisterHolder {
    private volatile static Map<String, CuratorFramework> curatorMap;
    private volatile static RegisterHolder registerHolder;
    private static ReentrantLock lock;

    private RegisterHolder() {}

    public static CuratorFramework getClient(String registerUrl) {
        if (registerHolder == null) {
            synchronized (RegisterHolder.class) {
                if (registerHolder == null) {
                    registerHolder = new RegisterHolder();
                    curatorMap = Maps.newHashMap();
                    lock = new ReentrantLock();
                }
            }
        }
        CuratorFramework client = null;
        lock.lock();
        try {
            String tempUrl = registerUrl.trim();
            if (!curatorMap.containsKey(tempUrl)) {
                ZkClientFactory zookeeperClientFactory = new ZkClientFactory().setAllAndGetClientFactory(tempUrl);
                client = zookeeperClientFactory.newCuratorFramework();
                client.start();
                curatorMap.put(tempUrl, client);
            } else {
                client = curatorMap.get(tempUrl);
            }
        } finally {
            lock.unlock();
        }
        return client;
    }
}

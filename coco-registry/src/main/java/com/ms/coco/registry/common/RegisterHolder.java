package com.ms.coco.registry.common;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;

import com.google.common.collect.Maps;
import com.ms.coco.registry.utils.ZkClientFactory;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月25日 下午9:45:37
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
                CuratorFrameworkState state = client.getState();
                if (state != CuratorFrameworkState.STARTED) {
                    client.start();
                }
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

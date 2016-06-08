package com.ms.coco.util;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月25日 下午4:27:47
 * @func 单实例模式 根据线程池名字 获取全局同一个线程池
 */
public class ThreadPoolHolder {
    private static volatile Map<String, ExecutorService> map;
    private static volatile ThreadPoolHolder threadPoolHolder;
    private static ReentrantLock lock;

    private ThreadPoolHolder() {}

    public static ExecutorService getFixedPool(String poolName, Integer min, Integer max, Integer queueSize,
            Long aliveTimeInsecond) {
        if (threadPoolHolder == null) {
            synchronized (ThreadPoolHolder.class) {
                if (threadPoolHolder == null) {
                    threadPoolHolder = new ThreadPoolHolder();
                    map = Maps.newHashMap();
                    lock = new ReentrantLock();
                }
            }
        }

        ExecutorService service = map.get(poolName);
        if (service == null) {
            lock.lock();
            if (service == null) {
                try {
                    int min2 = Runtime.getRuntime().availableProcessors();
                    int max2 = min2 * 2 + 2;
                    if (min == null || min < min2) {
                        min = min2;
                    }
                    if (max == null || max < max2) {
                        max = max2;
                    }
                    queueSize = queueSize == null ? 100 : queueSize;
                    aliveTimeInsecond = aliveTimeInsecond == null ? 100 : aliveTimeInsecond;
                    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(poolName + "-%d").build();
                    service = new ThreadPoolExecutor(min, max, aliveTimeInsecond, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>(queueSize), threadFactory, new CallerRunsPolicy());
                    map.put(poolName, service);
                } finally {
                    lock.unlock();
                }
            }

        }
        return service;
    }

    public static ExecutorService getStandardFixedPool(String poolName, Integer min, Integer max, Integer queueSize,
            Long aliveTimeInsecond) {
        if (threadPoolHolder == null) {
            synchronized (ThreadPoolHolder.class) {
                if (threadPoolHolder == null) {
                    threadPoolHolder = new ThreadPoolHolder();
                    map = Maps.newHashMap();
                    lock = new ReentrantLock();
                }
            }
        }

        ExecutorService service = map.get(poolName);
        if (service == null) {
            lock.lock();
            if (service == null) {
                try {
                    int min2 = Runtime.getRuntime().availableProcessors();
                    int max2 = min2 * 2 + 2;
                    if (min == null || min < min2) {
                        min = min2;
                    }
                    if (max == null || max < max2) {
                        max = max2;
                    }
                    queueSize = queueSize == null ? 100 : queueSize;
                    aliveTimeInsecond = aliveTimeInsecond == null ? 100 : aliveTimeInsecond;
                    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(poolName + "-%d").build();
                    
                    service = new StandardThreadExecutor(min, max, aliveTimeInsecond, TimeUnit.SECONDS, queueSize,
                            threadFactory);
                    map.put(poolName, service);
                } finally {
                    lock.unlock();
                }
            }

        }
        return service;
    }


    public static ExecutorService getScheduledPool(String poolName, Integer corePoolSize, boolean isDaemon) {
        if (threadPoolHolder == null) {
            synchronized (ThreadPoolHolder.class) {
                if (threadPoolHolder == null) {
                    threadPoolHolder = new ThreadPoolHolder();
                    map = Maps.newHashMap();
                    lock = new ReentrantLock();
                }
            }
        }
        ExecutorService service = map.get(poolName);
        if (service == null) {
            lock.lock();
            if (service == null) {
                try {
                    int min = Runtime.getRuntime().availableProcessors();
                    corePoolSize = (corePoolSize == null || corePoolSize < min) ? min * 2 : corePoolSize;
                    ThreadFactory threadFactory =
                            new ThreadFactoryBuilder().setNameFormat(poolName + "-%d").setDaemon(isDaemon).build();
                    service = Executors.newScheduledThreadPool(corePoolSize, threadFactory);
                    map.put(poolName, service);
                } finally {
                    lock.unlock();
                }
            }
        }
        return service;
    }
}

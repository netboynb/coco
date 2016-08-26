package com.ms.coco.registry.count;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午11:02:17
 * @func 针对指定hostkey请求结果状态计数，达到阀值触发回调接口 com.coco.framework.count.CountListener.callBack(String)
 */
public class RetryCountEntry {
    private static final Logger logger = LoggerFactory.getLogger(RetryCountEntry.class);
    private CountListener countListener;
    private Map<String, AtomicInteger> hostCountMap = Maps.newConcurrentMap();
    private int maxRetry = 5;

    public RetryCountEntry(CountListener countListener) {
        this.countListener = countListener;
    }

    public void successProcess(String hostkey) {
        process(true, hostkey);
    }

    public void failProcess(String hostkey) {
        process(false, hostkey);
    }

    private void process(boolean successReq, String hostkey) {
        if (hostCountMap.containsKey(hostkey) && successReq) {
            successHostkey(hostkey);
        } else if (!successReq) {
            failHostkey(hostkey);
        }
    }

    /**
     * contain the hostkey,the count should be -1 util to 0,if < 0,the key will be removed
     * 
     * @param hostkey
     */
    private synchronized void successHostkey(String hostKey) {

        // may be had removed ,so should check it again
        if (hostCountMap.containsKey(hostKey)) {
            AtomicInteger countAtomicInteger = hostCountMap.get(hostKey);
            if (countAtomicInteger.decrementAndGet() < 0) {
                // countAtomicInteger.set(0);
                logger.info("[{}] remove from HostKeyCountMap ", hostKey);
                hostCountMap.remove(hostKey);
            }
        }
    }

    /**
     * contain the hostKey,the count should be +1 util to ${MAX_RETRY},then will callback and reset
     * to 0
     * 
     * @param hostKey
     */
    private synchronized void failHostkey(String hostKey) {

        AtomicInteger countAtomicInteger = hostCountMap.get(hostKey);
        if (countAtomicInteger == null) {
            countAtomicInteger = new AtomicInteger(0);
            hostCountMap.put(hostKey, countAtomicInteger);
        }
        if (countAtomicInteger.incrementAndGet() >= maxRetry) {
            countListener.callBack(hostKey);
            countAtomicInteger.set(0);
        }
    }

    public void close() {
        hostCountMap.clear();
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }
}

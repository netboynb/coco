package com.ms.coco.registry.count;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午11:02:17
 * @func
 */
public class RetryListener implements CountListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetryListener.class);

    @Override
    public void callBack(String hostKey) {
        LOGGER.info("[{}] add to black list", hostKey);
    }

}

package com.ms.coco.registry.common;

import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @author wanglin/netboy
* @version 创建时间：2016年7月6日 下午8:09:51
* @func 
*/
public class EventFilterDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventFilterDispatcher.class);

    public void filter(TreeCacheEvent event) {
        LOGGER.info("event type={}", event.getType());
        switch (event.getType()) {
            case NODE_ADDED:

                LOGGER.info("child[path={}, date={}] added", event.getData().getPath(),
                        new String(event.getData().getData()));
                break;
            case NODE_UPDATED:
                LOGGER.info("child[path={}, date={}] updated", event.getData().getPath(),
                        new String(event.getData().getData()));
                break;
            case NODE_REMOVED:
                LOGGER.info("child[path={}, date={}] updated", event.getData().getPath(),
                        new String(event.getData().getData()));
                break;
            case CONNECTION_SUSPENDED:
                break;
            case CONNECTION_RECONNECTED:
                break;
            case CONNECTION_LOST:
                break;
            case INITIALIZED:
                break;
        }
    }
}

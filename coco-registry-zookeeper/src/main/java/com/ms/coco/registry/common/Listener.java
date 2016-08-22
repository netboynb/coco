package com.ms.coco.registry.common;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;

/**
* @author wanglin/netboy
* @version 创建时间：2016年7月6日 下午8:33:19
* @func 
*/
public interface Listener {
    void refresh(Type type, String path, String data);
}

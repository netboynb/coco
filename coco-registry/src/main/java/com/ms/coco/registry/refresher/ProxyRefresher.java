package com.ms.coco.registry.refresher;

import java.util.concurrent.ExecutorService;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import com.ms.coco.registry.common.CocoUtils;
import com.ms.coco.registry.common.NodeEnum;
import com.ms.coco.registry.entry.impl.ProxyEntry;
import com.ms.coco.registry.model.Node;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午3:01:54
 * @func
 */
public class ProxyRefresher extends Refresher {

    public ProxyRefresher(String nameSpace, CuratorFramework curator, ExecutorService eventExecutor,
            ProxyEntry proxyEntry) {
        super(nameSpace, curator, eventExecutor);
        this.notifyEntry = proxyEntry;
    }

    @Override
    public String rootPath() {
        return CocoUtils.buildPath(nameSpace, NodeEnum.PROXY_NODE.value());
    }

    @Override
    protected void doNodeAdded(CuratorFramework curator, TreeCacheEvent event) {
        Node node = parseTreeCacheEvent(event);
        notifyEntry.addEvent(node);
    }

    @Override
    protected void doNodeUpdated(CuratorFramework curator, TreeCacheEvent event) {
        Node node = parseTreeCacheEvent(event);
        notifyEntry.updateEvent(node);
    }

    @Override
    protected void doNodeRemoved(CuratorFramework curatorFramework, TreeCacheEvent event) {
        Node node = parseTreeCacheEvent(event);
        notifyEntry.removeEvent(node);
    }
}

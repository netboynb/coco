package com.ms.coco.refresher;

import java.util.concurrent.ExecutorService;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import com.ms.coco.common.CocoUtils;
import com.ms.coco.common.NodeEnum;
import com.ms.coco.entry.impl.BalanceEntry;
import com.ms.coco.model.Node;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午11:02:17
 * @func
 */
public class BalanceRefresher extends Refresher {

    public BalanceRefresher(String nameSpace, CuratorFramework curator, ExecutorService eventExecutor,
            BalanceEntry balanceEntry) {
        super(nameSpace, curator, eventExecutor);
        this.notifyEntry = balanceEntry;
    }

    @Override
    public String rootPath() {
        return CocoUtils.buildPath(nameSpace, NodeEnum.BALANCE_NODE.toString());
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

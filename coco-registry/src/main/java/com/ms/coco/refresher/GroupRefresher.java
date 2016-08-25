package com.ms.coco.refresher;

import java.util.concurrent.ExecutorService;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import com.ms.coco.common.CocoUtils;
import com.ms.coco.common.NodeEnum;
import com.ms.coco.entry.impl.GroupEntry;
import com.ms.coco.model.Node;

/**
* @author wanglin/netboy
* @version 创建时间：2016年8月11日 下午3:01:16
* @func 
*/
public class GroupRefresher extends Refresher {

    public GroupRefresher(String nameSpace, CuratorFramework curator, ExecutorService eventExecutor,
            GroupEntry groupEntry) {
        super(nameSpace, curator, eventExecutor);
        this.notifyEntry = groupEntry;
    }

    @Override
    public String rootPath() {
        return CocoUtils.buildPath(nameSpace, NodeEnum.GROUP_NODE.value());
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

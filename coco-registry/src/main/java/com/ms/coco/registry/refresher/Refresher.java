package com.ms.coco.registry.refresher;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.ms.coco.registry.common.CocoUtils;
import com.ms.coco.registry.entry.NotifyService;
import com.ms.coco.registry.model.Node;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午2:38:58
 * @func
 */
public abstract class Refresher implements TreeCacheListener {
    private static final Logger logger = LoggerFactory.getLogger(Refresher.class);
    protected NotifyService notifyEntry;
    protected String nameSpace;
    protected TreeCache treeCache;
    protected CuratorFramework curator;
    protected ExecutorService eventExecutor;


    public Refresher(String nameSpace, CuratorFramework curator, ExecutorService eventExecutor) {
        super();
        this.nameSpace = nameSpace;
        this.curator = curator;
        this.eventExecutor = eventExecutor;
    }

    public void start() throws Exception {
        this.treeCache = TreeCache.newBuilder(curator, rootPath())
                            .setExecutor(eventExecutor)
                            .setCacheData(true)
                            .setCreateParentNodes(true)
                            .setDataIsCompressed(false)
                            .setMaxDepth(10).build();
        treeCache.start();
        startListen();
    }

    private Refresher startListen() {
        treeCache.getListenable().addListener(this);
        logger.info("[{}] started PathChildEvent={} listener", curator.getNamespace(), rootPath());
        return this;
    }

    public void close() throws IOException {
        if (treeCache != null) {
            treeCache.close();
        }
    }

    protected String toChildPath(ChildData childData) {
        String fullPath = childData.getPath();
        return ZKPaths.getNodeFromPath(fullPath);
    }

    protected String toJsonString(ChildData childData) {
        byte[] bytes = childData.getData();

        return bytes == null ? null : new String(childData.getData(), Charsets.UTF_8);
    }

    protected Node parseTreeCacheEvent(TreeCacheEvent event) {
        Node node = new Node();
        ChildData childData = event.getData();
        String fullPath = childData.getPath();
        String nodeName = null;
        if(StringUtils.isNotBlank(fullPath)){
            List<String> list =CocoUtils.splitStr(fullPath, "/");
            nodeName = list.get(list.size()-1);
        }
        return node.setData(toJsonString(childData)).setFullPath(childData.getPath()).setNodeName(nodeName);
    }

    protected void logParseFail(Refresher refresher, CuratorFramework curator, String path, String json, Exception e) {
        logger.warn("Ignore_{}_Parse_Fail:{namespace={}, path={}, childData={}, E={}}", refresher.getClass().getSimpleName(),
                curator.getNamespace(), path, json, e.getMessage());
    }

    protected void logNodeChanged(CuratorFramework curator, TreeCacheEvent event) {
        logger.info("[{}] {} info={}", curator.getNamespace(), rootPath(), event.toString());
    }

    @Override
    public void childEvent(CuratorFramework client, TreeCacheEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("childEvent info={}", event.toString());
        }
        switch (event.getType()) {
            case NODE_ADDED:
                logNodeChanged(client, event);
                doNodeAdded(client, event);
                break;
            case NODE_UPDATED:
                logNodeChanged(client, event);
                doNodeUpdated(client, event);
                break;
            case NODE_REMOVED:
                logNodeChanged(client, event);
                doNodeRemoved(client, event);
                break;
            case CONNECTION_SUSPENDED:
                logNodeChanged(client, event);
                doCntSuspended(client, event);
                break;
            case CONNECTION_RECONNECTED:
                logNodeChanged(client, event);
                doCntReconnected(client, event);
                break;
            case CONNECTION_LOST:
                logNodeChanged(client, event);
                doCntLost(client, event);
                break;
            case INITIALIZED:
                logNodeChanged(client, event);
                doInitialized(client, event);
                break;
        }
    }

    public abstract String rootPath();

    protected abstract void doNodeRemoved(CuratorFramework curatorFramework, TreeCacheEvent event);

    protected abstract void doNodeUpdated(CuratorFramework curator, TreeCacheEvent event);

    protected abstract void doNodeAdded(CuratorFramework curator, TreeCacheEvent event);

    protected void doCntSuspended(CuratorFramework curator, TreeCacheEvent event) {
        logger.warn("[CntSuspended]  event not implement yet");
    }

    protected void doCntReconnected(CuratorFramework curator, TreeCacheEvent event) {
        logger.warn("[CntReconnected]  event not implement yet");
    }

    protected void doCntLost(CuratorFramework curator, TreeCacheEvent event) {
        logger.warn("[CntLost]  event not implement yet");
    }

    protected void doInitialized(CuratorFramework curator, TreeCacheEvent event) {
        logger.warn("[Initialized]  event not implement yet");
    }
}

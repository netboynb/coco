package com.ms.coco.registry.simple;

import java.util.List;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.ms.coco.registry.common.RegisterHolder;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年08月15日 下午8:50:46
 * @func 配置中心 通用监听zk节点变更
 */
public class SimpleModeCenter implements PathChildrenCacheListener {
    private static final Logger logger = LoggerFactory.getLogger(SimpleModeCenter.class);
    protected PathChildrenCache pathChildrenCache;
    private String nameSpace;
    private String zkurl;
    private CuratorFramework zkClient;
    private Set<String> itemSet = Sets.newConcurrentHashSet();

    public SimpleModeCenter(String nameSpace, String zkurl) {
        this.nameSpace = nameSpace;
        this.zkurl = zkurl;
    }


    public void start() {
        Preconditions.checkNotNull(nameSpace, "nameSpace can not be null");
        Preconditions.checkNotNull(zkurl, "zkurl can not be null");
        zkClient = RegisterHolder.getClient(zkurl);
        pathChildrenCache = new PathChildrenCache(zkClient, "/" + nameSpace, true);
        pathChildrenCache.getListenable().addListener(this);
        try {
            pathChildrenCache.start();
            initDataFromZk();
        } catch (Exception e) {
            // zookeeper cluster 不可用时,load the conf from localfile
            if (e instanceof ConnectionLossException) {
                logger.error(" ConnectionLossException has happen ,start to laod the local confFile to mem");
            }
        }

    }

    /**
     * 
     * TODO: 该方法会发生多个线程同时调用 需要线程安全
     *
     * @throws Exception
     */
    private synchronized void initDataFromZk() throws Exception {
        Set<String> tempSet = Sets.newHashSet();
        List<String> childPathList = zkClient.getChildren().forPath("/" + nameSpace);
        tempSet.addAll(childPathList);
        // 防止存在 内存中存在的key 在zookeeper中不存在
        itemSet = tempSet;
    }

    public Set<String> getItemSet() {
        return itemSet;
    }

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("path={} event={}", event.getData().getPath(), event.getType());
        }
        switch (event.getType()) {
            case CHILD_ADDED:
                logChildChanged(client, event);
                addOrUpdate(event);
                break;
            case CHILD_UPDATED:
                logChildChanged(client, event);
                addOrUpdate(event);
                break;
            case CHILD_REMOVED:
                logChildChanged(client, event);
                String key = ZKPaths.getNodeFromPath(event.getData().getPath());
                itemSet.remove(key);
                break;
            case CONNECTION_SUSPENDED:
                logChildChanged(client, event);
                break;
            case CONNECTION_RECONNECTED:
                logChildChanged(client, event);
                initDataFromZk();
                break;
            case CONNECTION_LOST:
                logChildChanged(client, event);
                break;
            case INITIALIZED:
                logChildChanged(client, event);
                break;
        }
    }

    void addOrUpdate(PathChildrenCacheEvent event) {
        String key = ZKPaths.getNodeFromPath(event.getData().getPath());
        itemSet.add(key);
    }

    protected void logChildChanged(CuratorFramework curator, PathChildrenCacheEvent event) {
        logger.info("[{}]  event={} path={}", nameSpace, event.getType(), event.getData().getPath());
    }
}

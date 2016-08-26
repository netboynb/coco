package com.ms.coco.registry.entry.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ms.coco.registry.common.CocoUtils;
import com.ms.coco.registry.entry.ChildGroupService;
import com.ms.coco.registry.entry.NotifyService;
import com.ms.coco.registry.model.Node;
import com.ms.coco.registry.model.ServerNode;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月15日 上午1:25:04
 * @func
 */
public class ChildGroupEntry implements ChildGroupService, NotifyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChildGroupEntry.class);
    private ReentrantLock lock = new ReentrantLock();
    private boolean isOpened = false;
    private String groupName;
    private Map<String, ServerNode> serverMaps = Maps.newHashMap();
    private ImmutableList<ServerNode> immutableList = null;
    private GroupEntry parentGroupEntry;

    public ChildGroupEntry(GroupEntry parentGroupEntry) {
        super();
        this.parentGroupEntry = parentGroupEntry;
    }

    @Override
    public void addEvent(Node node) {
        String data = node.getData();
        ServerNode serverNode = null;
        try {
            serverNode = CocoUtils.GSON.fromJson(data, ServerNode.class);
            serverNode.hostKey();
        } catch (Exception e) {
            LOGGER.error("[child_group_host] parse serverNode's jsonString=[{}] error,info={}", data, e.toString());
            return;
        }
        // deal leaf node event
        try {
            lock.lock();
            serverMaps.put(node.getNodeName(), serverNode);
            refresh();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateEvent(Node node) {
        String data = node.getData();
        ServerNode serverNode = null;
        try {
            serverNode = CocoUtils.GSON.fromJson(data, ServerNode.class);
        } catch (Exception e) {
            LOGGER.error("[child_group_host] parse serverNode's jsonString=[{}] error,info={}", data, e.toString());
            return;
        }
        // deal leaf node event
        try {
            lock.lock();
            serverMaps.put(node.getNodeName(), serverNode);
            refresh();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeEvent(Node node) {
        // deal leaf node event
        try {
            lock.lock();
            serverMaps.remove(node.getNodeName());
            refresh();
        } finally {
            lock.unlock();
        }
    }

    public void setOpened(boolean isOpened) {
        try {
            lock.lock();
            this.isOpened = isOpened;
            refresh();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<ServerNode> getAllReaders() {
        // return all server node
        return ImmutableList.copyOf(serverMaps.values());
    }

    @Override
    public List<ServerNode> getAvailableReaders() {
        // just return the available server node's immutable list
        return immutableList;
    }

    @Override
    public boolean groupStatus() {
        return isOpened;
    }

    /**
     * 
     * TODO: refresh the immutable list
     *
     */
    @Override
    public void refresh() {
        parentGroupEntry.refresh();
    }

    // groupEntry's refresh call the method
    public void selfRefresh() {
        List<ServerNode> list = Lists.newArrayList();
        if (isOpened) {
            serverMaps.values().parallelStream().forEach(item -> {
                if (item.serviceAvailable()) {
                    list.add(item);
                }
            });
        }
        immutableList = ImmutableList.copyOf(list);
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public boolean isOpened() {
        return isOpened;
    }
}

package com.ms.coco.entry.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.ms.coco.common.CocoEnum;
import com.ms.coco.common.CocoUtils;
import com.ms.coco.common.NodeEnum;
import com.ms.coco.entry.GroupService;
import com.ms.coco.entry.NotifyService;
import com.ms.coco.model.LeafGroup;
import com.ms.coco.model.Node;
import com.ms.coco.model.ServerNode;
import com.google.common.collect.Maps;

/**
* @author wanglin/netboy
* @version 创建时间：2016年8月10日 下午8:15:42
* @func 
*/
public class GroupEntry implements GroupService, NotifyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupEntry.class);
    private Map<String, ChildGroupEntry> groupsMap = Maps.newConcurrentMap();
    private ImmutableMap<String, List<ServerNode>> allReaderImmuableMap = null;
    private ImmutableMap<String, List<ServerNode>> availableReaderImmuableMap = null;

    @Override
    public Map<String, List<ServerNode>> getAllReaders() {
        return allReaderImmuableMap;
    }

    @Override
    public Map<String, List<ServerNode>> getAvailableReaders() {
        return availableReaderImmuableMap;
    }

    @Override
    public void addEvent(Node node) {
        String pathName = node.getNodeName();
        String fullPath = node.getFullPath();
        if (ignoreEvent(pathName, "add")) {
            return;
        }
        LeafGroup leafGroup = parseLeaf(pathName, fullPath);
        if (null == leafGroup) {
            return;
        } else if (!leafGroup.isLeaf()) {
            // the group node
            ChildGroupEntry newChildGroupEntry = new ChildGroupEntry();
            ChildGroupEntry childGroupEntry = groupsMap.putIfAbsent(leafGroup.getGroupname(), newChildGroupEntry);
            childGroupEntry = childGroupEntry == null ? newChildGroupEntry : childGroupEntry;
            // just set group name ,the group's status just keep default
            childGroupEntry.setGroupName(leafGroup.getGroupname());
        } else {
            // the group's leaf node
            ChildGroupEntry newChildGroupEntry = new ChildGroupEntry();
            ChildGroupEntry childGroupEntry = groupsMap.putIfAbsent(leafGroup.getGroupname(), newChildGroupEntry);
            childGroupEntry = childGroupEntry == null ? newChildGroupEntry : childGroupEntry;
            childGroupEntry.addEvent(node);
        }
        refresh();
    }

    @Override
    public void updateEvent(Node node) {
        String pathName = node.getNodeName();
        String fullPath = node.getFullPath();
        if (ignoreEvent(pathName, "update")) {
            return;
        }
        LeafGroup leafGroup = parseLeaf(pathName, fullPath);
        if (null == leafGroup) {
            return;
        } else if (!leafGroup.isLeaf()) {
            // the group node
            ChildGroupEntry newChildGroupEntry = new ChildGroupEntry();
            ChildGroupEntry childGroupEntry = groupsMap.putIfAbsent(leafGroup.getGroupname(), newChildGroupEntry);
            childGroupEntry = childGroupEntry == null ? newChildGroupEntry : childGroupEntry;
            // just update group status
            childGroupEntry.setOpened(parseGroupStatus(node));
        } else {
            // the group's leaf node
            ChildGroupEntry newChildGroupEntry = new ChildGroupEntry();
            ChildGroupEntry childGroupEntry = groupsMap.putIfAbsent(leafGroup.getGroupname(), newChildGroupEntry);
            childGroupEntry = childGroupEntry == null ? newChildGroupEntry : childGroupEntry;
            childGroupEntry.addEvent(node);
        }
        refresh();
    }

    @Override
    public void removeEvent(Node node) {
        String pathName = node.getNodeName();
        String fullPath = node.getFullPath();
        if (ignoreEvent(pathName, "remove")) {
            return;
        }
        LeafGroup leafGroup = parseLeaf(pathName, fullPath);
        if (null == leafGroup) {
            return;
        } else if (!leafGroup.isLeaf()) {
            // the group node, just remove the child group
            groupsMap.remove(pathName);
        } else {
            // the group's leaf node
            ChildGroupEntry childGroupEntry = groupsMap.get(pathName);
            childGroupEntry.removeEvent(node);
        }
        refresh();
    }

    @Override
    public void refresh() {
        Builder allBuilder = ImmutableMap.builder();
        Builder availableBuilder = ImmutableMap.builder();
        for(Entry<String, ChildGroupEntry> entry: groupsMap.entrySet()){
            allBuilder.put(entry.getKey(), entry.getValue().getAllReaders());
            availableBuilder.put(entry.getKey(), entry.getValue().getAvailableReaders());
        }
       allReaderImmuableMap = allBuilder.build();
       availableReaderImmuableMap = availableBuilder.build();
    }

    /**
     * 
     * TODO: if the path is group_node return true,else return false;
     *
     * @param pathName
     * @param eventType
     * @return
     */
    private boolean ignoreEvent(String pathName, String eventType) {
        if (pathName.equals(NodeEnum.GROUP_NODE.value())) {
            LOGGER.warn("[group_node] ignore " + eventType
                    + " event,pathName ={},the groupName must be suffix with '_group' ",
                    pathName);
            return true;
        }
        return false;
    }

    /**
     * 
     * TODO: parse group_node's event ,and get the leafGroup object, dirty data will return null
     * 
     * @param nodeName
     * @param fullPathName
     * @return
     */
    private LeafGroup parseLeaf(String nodeName, String fullPathName) {
        LeafGroup leafGroup = new LeafGroup();
        if (nodeName.endsWith(CocoEnum.GROUP_SUFFIX.value())) {
            // child group node
            leafGroup.setLeaf(false).setGroupname(nodeName);
        } else {
            // group's leaf node
            List<String> nodes = CocoUtils.splitStr(fullPathName, "/");
            if (null == nodes || nodes.size() < 4) {
                LOGGER.error("[group_node] parse node error,fullPathName ={}", fullPathName);
                return null;
            }
            leafGroup.setLeaf(true).setGroupname(nodes.get(2)).setLeafName(nodeName);
        }
        return leafGroup;
    }

    /**
     * 
     * TODO: parse the group's status
     *
     * @param node
     * @return default return false
     */
    private boolean parseGroupStatus(Node node) {
        String data = node.getData();
        if (StringUtils.isBlank(data)) {
            return false;
        }
        if (data.trim().equals("true")) {
            return true;
        } else {
            return false;
        }
    }
}

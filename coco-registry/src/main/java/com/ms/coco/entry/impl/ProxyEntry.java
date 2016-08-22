package com.ms.coco.entry.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.ms.coco.common.CocoUtils;
import com.ms.coco.common.NodeEnum;
import com.ms.coco.entry.NotifyService;
import com.ms.coco.entry.ProxyService;
import com.ms.coco.model.GroupProxy;
import com.ms.coco.model.IpProxy;
import com.ms.coco.model.Node;

/**
* @author wanglin/netboy
* @version 创建时间：2016年8月10日 下午8:17:10
* @func 
*/
public class ProxyEntry implements ProxyService, NotifyService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyEntry.class);

    private ImmutableList<GroupProxy> immutableGroupProxyList = null;
    private ImmutableList<IpProxy> immutableIpProxyList = null;
    private boolean isOpened = false;

    @Override
    public List<GroupProxy> getGroupProxy() {
        return immutableGroupProxyList;
    }

    @Override
    public List<IpProxy> getIpProxy() {
        return immutableIpProxyList;
    }

    @Override
    public boolean proxyStatus() {
        return isOpened;
    }

    @Override
    public void addEvent(Node node) {
        updateEvent(node);
    }

    @Override
    public void updateEvent(Node node) {
        String nodeName = node.getNodeName();
        String data = node.getData();
        NodeEnum nodeEnum = NodeEnum.getEnum(nodeName);
        switch (nodeEnum) {
            case PROXY_NODE:
                // just update the status of proxy_node
                updateParseStatus(data);
                break;
            case GROUP_PROXY_NODE:
                updateParseGroupEvent(data);
                break;
            case IP_PROXY_NODE:
                updateParseIpEvent(data);
                break;
            default:
                LOGGER.error("[proxy_node] unkown event ,nodeName={}", nodeName);
                break;
        }
    }

    @Override
    public void removeEvent(Node node) {
        String nodeName = node.getNodeName();
        NodeEnum nodeEnum = NodeEnum.getEnum(nodeName);
        switch (nodeEnum) {
            case PROXY_NODE:
                // just reset all to null
                immutableGroupProxyList = null;
                immutableIpProxyList = null;
                LOGGER.error("[proxy_node] base node had removed");
                break;
            case GROUP_PROXY_NODE:
                // just reset group_proxy to null
                immutableGroupProxyList = null;
                LOGGER.error("[proxy_node-group_proxy_node]  node had removed");
                break;
            case IP_PROXY_NODE:
                // just reset ip_proxy to null
                immutableIpProxyList = null;
                LOGGER.error("[proxy_node-ip_proxy_node]  node had removed");
                break;
            default:
                LOGGER.error("[proxy_node] unkown event ,nodeName={}", nodeName);
                break;
        }
    }

    /**
     * 
     * TODO: convert the status event to boolean value
     *
     * @param data
     */
    private void updateParseStatus(String data) {
        if (StringUtils.isBlank(data)) {
            isOpened = false;
        } else {
            if (data.trim().equals("true")) {
                isOpened = true;
            } else {
                isOpened = false;
            }
        }
    }

    /**
     * 
     * TODO: get the groupProxy update info,refresh the memory info
     *
     * @param data
     */
    private void updateParseGroupEvent(String data) {

        if (StringUtils.isBlank(data)) {
            List<GroupProxy> list = Lists.newArrayList();
            immutableGroupProxyList = ImmutableList.copyOf(list);
        } else {
            try {
                Type collectionType = new TypeToken<List<GroupProxy>>() {}.getType();
                List<GroupProxy> list = CocoUtils.GSON.fromJson(data, collectionType);
                immutableGroupProxyList = ImmutableList.copyOf(list);
            } catch (Exception e) {
                LOGGER.error("[proxy_node-group] parse data error,info={}", e.toString());
            }

        }
        LOGGER.info("[proxy_node-group] updateOrAdd event,data={}", data);
    }

    private void updateParseIpEvent(String data) {
        if (StringUtils.isBlank(data)) {
            List<IpProxy> list = Lists.newArrayList();
            immutableIpProxyList = ImmutableList.copyOf(list);
        } else {
            try {
                Type collectionType = new TypeToken<List<IpProxy>>() {}.getType();
                List<IpProxy> list = CocoUtils.GSON.fromJson(data, collectionType);
                immutableIpProxyList = ImmutableList.copyOf(list);
            } catch (Exception e) {
                LOGGER.error("[proxy_node-ip] parse data error,info={}", e.toString());
            }

        }
        LOGGER.info("[proxy_node-ip] updateOrAdd event,data={}", data);
    }

    @Override
    public void refresh() {}

}

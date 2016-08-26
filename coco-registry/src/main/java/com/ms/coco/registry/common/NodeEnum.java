package com.ms.coco.registry.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月10日 下午11:31:39
 * @func
 */
public enum NodeEnum {
    DEFAULT("default"),
    SERVICE_NODE("service_node"),
    GROUP_NODE("group_node"),
    PROXY_NODE("proxy_node"),
    BALANCE_NODE("balance_node"),
    CLIENT_NODE("client_node"),
    GROUP_PROXY_NODE("group_proxy_node"),
    IP_PROXY_NODE("ip_proxy_node");
    
    private static Map<String, NodeEnum> map;

    static {
        map = new HashMap<>(values().length);
        for (NodeEnum nodeEnum : values()) {
            map.put(nodeEnum.nodeName, nodeEnum);
        }
    }

    private String nodeName;
    private NodeEnum(String name) {
        this.nodeName = name;
    }

    public static NodeEnum getEnum(String nodeName) {
        NodeEnum nodeEnum = map.get(nodeName);
        return nodeEnum == null ? DEFAULT : nodeEnum;
    }

    public String value() {
        return nodeName;
    }
}

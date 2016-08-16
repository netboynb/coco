package com.ms.coco.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月12日 上午01:26:19
 * @func
 */
public enum CocoEnum {
    DEFAULT("default"),
    SLB_DEFAULT("slb_default"),
    SLB_AVAILABLE("slb_available"),
    PROXY_LINK_CHAR("-"),
    GROUP_SUFFIX("_group");
    
    private static Map<String, CocoEnum> map;

    static {
        map = new HashMap<>(values().length);
        for (CocoEnum nodeEnum : values()) {
            map.put(nodeEnum.nodeName, nodeEnum);
        }
    }

    private String nodeName;
    private CocoEnum(String name) {
        this.nodeName = name;
    }

    public static CocoEnum getEnum(String nodeName) {
        CocoEnum nodeEnum = map.get(nodeName);
        return nodeEnum == null ? DEFAULT : nodeEnum;
    }

    public String value() {
        return nodeName;
    }

    @Override
    public String toString() {
        return nodeName;
    }
}

package com.ms.coco.registry.common;

import java.util.Map;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午7:57:55
 * @func
 */
public enum StatusEnum {
    GROUP_DEAD(-2),
    GROUP_INVALID(-1),
    GROUP_UNREADY(0),
    GROUP_AVAILABLE(1);
    
    private Integer status;
    private static Map<String, StatusEnum> map;
    
    private StatusEnum(Integer status){
        this.status = status;
    }

    static {
        for (StatusEnum enum1 : StatusEnum.values()) {
            map.put(enum1.status + "", enum1);
        }
    }

    public static StatusEnum getEnum(String name) {
        StatusEnum result = map.get(name);
        return result == null ? GROUP_UNREADY : result;
    }

}

package com.ms.coco.num;

import java.util.Map;

import com.google.common.collect.Maps;

/**
* @author wanglin/netboy
* @version 创建时间：2016年10月5日 下午2:59:08
* @func 
*/
public enum MsgType {
    ERROR(-1), PING(0), REQ(1), ACK(2);
    private Integer type;
    private static Map<Integer, MsgType> map = Maps.newHashMap();

    private MsgType(Integer type) {
        this.type = type;
    }

    static {
        for (MsgType reqType : MsgType.values()) {
            map.put(reqType.type, reqType);
        }
    }

    public MsgType geReqType(Integer type) {
        MsgType result = map.get(type);
        return result == null ? ERROR : result;
    }

    public Integer intValue() {
        return type;
    }
}

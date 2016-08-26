package com.ms.coco.registry.model;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月12日 下午12:57:56
 * @func
 */
public class IpProxy {
    private String fromServer;
    private String toServer;
    private Integer reqRate;

    public IpProxy() {
        super();
    }

    public IpProxy(String fromServer, String toServer, Integer reqRate) {
        super();
        this.fromServer = fromServer;
        this.toServer = toServer;
        this.reqRate = reqRate;
    }

    public String getFromServer() {
        return fromServer;
    }

    public IpProxy setFromServer(String fromServer) {
        this.fromServer = fromServer;
        return this;
    }

    public String getToServer() {
        return toServer;
    }

    public IpProxy setToServer(String toServer) {
        this.toServer = toServer;
        return this;
    }

    public Integer getReqRate() {
        return reqRate;
    }

    public IpProxy setReqRate(Integer reqRate) {
        this.reqRate = reqRate;
        return this;
    }
}

package com.ms.coco.model;

import com.google.common.base.Objects;
import com.ms.coco.common.CocoUtils;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午11:02:17
 * @func
 */
public class ServerNode {

    public enum ServerStatus {
        Invalid, Unready, Available, Dead
    }

    private String host;
    private Integer port;
    // the server's weight
    private Integer weight = 10;
    // create by host and port
    private String key;
    private ServerStatus serverStatus = ServerStatus.Invalid;

    /** for gson create */
    protected ServerNode() {}

    public ServerNode(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ServerNode(String host, int port, ServerStatus serverStatus) {
        this(host, port);
        this.serverStatus = serverStatus;
    }

    public ServerNode(String host, int port, ServerStatus serverStatus, int weight) {
        this(host, port);
        this.serverStatus = serverStatus;
        this.weight = weight;
    }

    public void copyServicePramTo(ServerNode targetServerNode) {
        if (targetServerNode != null) {
            targetServerNode.serverStatus = serverStatus;
            targetServerNode.weight = weight;
        }
    }

    public boolean heartbreak() {
        return true;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String hostKey() {
        if (key == null) {
            key = host + ":" + port;
        }
        return key;
    }

    public String getHttp() {
        return "http://" + host + ":" + port;
    }

    public ServerStatus getServiceStatus() {
        return serverStatus;
    }

    public void setServiceStatus(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    public boolean serviceAvailable() {
        return serverStatus == ServerStatus.Available;
    }


    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ServerNode that = (ServerNode) o;

        if (port != that.port)
            return false;
        if (host != null ? !host.equals(that.host) : that.host != null)
            return false;
        return serverStatus == that.serverStatus;

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(host, port, serverStatus);
    }

    public static ServerNode jsonToNode(String json) {
        return CocoUtils.GSON.fromJson(json, ServerNode.class);
    }
}

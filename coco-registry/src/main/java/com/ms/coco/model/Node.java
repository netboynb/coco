package com.ms.coco.model;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月12日 上午1:35:19
 * @func
 */
public class Node {
    String fullPath;
    String nodeName;
    String data;

    public Node() {
        super();
    }

    public Node(String fullPath, String nodeName, String data) {
        super();
        this.fullPath = fullPath;
        this.nodeName = nodeName;
        this.data = data;
    }

    public String getFullPath() {
        return fullPath.trim();
    }

    public Node setFullPath(String fullPath) {
        this.fullPath = fullPath;
        return this;
    }

    public String getNodeName() {
        return nodeName.trim();
    }

    public Node setNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public String getData() {
        return data.trim();
    }

    public Node setData(String data) {
        this.data = data;
        return this;
    }

}

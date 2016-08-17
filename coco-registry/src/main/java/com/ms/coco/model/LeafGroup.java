package com.ms.coco.model;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月15日 上午1:21:47
 * @func
 */
public class LeafGroup {
    boolean isLeaf;
    String leafName;
    String groupname;

    public LeafGroup(boolean isLeaf, String leafName, String groupname) {
        super();
        this.isLeaf = isLeaf;
        this.leafName = leafName;
        this.groupname = groupname;
    }

    public LeafGroup() {
        super();
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public LeafGroup setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
        return this;
    }

    public String getLeafName() {
        return leafName;
    }

    public LeafGroup setLeafName(String leafName) {
        this.leafName = leafName;
        return this;
    }

    public String getGroupname() {
        return groupname;
    }

    public LeafGroup setGroupname(String groupname) {
        this.groupname = groupname;
        return this;
    }


}

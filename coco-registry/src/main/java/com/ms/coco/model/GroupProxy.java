package com.ms.coco.model;

import com.google.common.base.Objects;
import com.ms.coco.common.CocoEnum;
import com.ms.coco.common.CocoUtils;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月12日 下午12:53:48
 * @func
 */
public class GroupProxy {
    private String fromGroup;
    private String toGroup;
    private Integer reqRate;
    private String key;

    public GroupProxy() {
        super();
    }

    public GroupProxy(String fromGroup, String toGroup, Integer reqRate) {
        super();
        this.fromGroup = fromGroup;
        this.toGroup = toGroup;
        this.reqRate = reqRate;
    }

    public String key() {
        if (null == key) {
            key = fromGroup + CocoEnum.PROXY_LINK_CHAR + toGroup;
        }
        return key;
    }

    public String getFromGroup() {
        return fromGroup;
    }

    public GroupProxy setFromGroup(String fromGroup) {
        this.fromGroup = fromGroup;
        return this;
    }

    public String getToGroup() {
        return toGroup;
    }

    public GroupProxy setToGroup(String toGroup) {
        this.toGroup = toGroup;
        return this;
    }

    public Integer getReqRate() {
        return reqRate;
    }

    public GroupProxy setReqRate(Integer reqRate) {
        this.reqRate = reqRate;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        GroupProxy that = (GroupProxy) o;
        if (fromGroup != that.fromGroup)
            return false;
        return toGroup == that.toGroup;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fromGroup, toGroup);
    }

    @Override
    public String toString() {
        return CocoUtils.GSON.toJson(this);
    }
}

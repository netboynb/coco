package com.ms.coco.registry.balance;

import java.util.List;

import com.ms.coco.registry.model.GroupProxy;
import com.ms.coco.registry.model.IpProxy;
import com.ms.coco.registry.model.ServerNode;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午11:53:21
 * @func
 */
public interface BaseBalance {

    ServerNode select(List<ServerNode> list, GroupProxy groupProxy, IpProxy ipProxy);
}

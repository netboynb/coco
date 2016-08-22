package com.ms.coco.balance;

import java.util.List;

import com.ms.coco.model.GroupProxy;
import com.ms.coco.model.IpProxy;
import com.ms.coco.model.ServerNode;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午11:53:21
 * @func
 */
public interface BaseBalance {

    ServerNode select(List<ServerNode> list, GroupProxy groupProxy, IpProxy ipProxy);
}

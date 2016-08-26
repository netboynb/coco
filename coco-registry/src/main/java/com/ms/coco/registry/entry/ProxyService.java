package com.ms.coco.registry.entry;

import java.util.List;

import com.ms.coco.registry.model.GroupProxy;
import com.ms.coco.registry.model.IpProxy;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午7:31:46
 * @func
 */
public interface ProxyService {

    List<GroupProxy> getGroupProxy();

    List<IpProxy> getIpProxy();

    boolean proxyStatus();
}

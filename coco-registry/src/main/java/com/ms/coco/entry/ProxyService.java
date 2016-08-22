package com.ms.coco.entry;

import java.util.List;

import com.ms.coco.model.GroupProxy;
import com.ms.coco.model.IpProxy;

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

package com.ms.coco.registry;

import com.ms.coco.registry.entry.BalanceService;
import com.ms.coco.registry.entry.GroupService;
import com.ms.coco.registry.entry.ProxyService;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月15日 下午7:32:07
 * @func
 */
public interface CenterService extends GroupService, BalanceService, ProxyService {
    void init();

    void start() throws Exception;

    void close() throws Exception;

}

package com.ms.coco;

import java.io.IOException;

import com.ms.coco.entry.BalanceService;
import com.ms.coco.entry.GroupService;
import com.ms.coco.entry.ProxyService;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月15日 下午7:32:07
 * @func
 */
public interface CenterService extends GroupService, BalanceService, ProxyService {
    void init();

    void start() throws Exception;

    void close() throws IOException;

}

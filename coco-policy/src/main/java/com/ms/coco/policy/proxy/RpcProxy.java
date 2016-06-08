package com.ms.coco.policy.proxy;

import java.util.List;

/**
* @author wanglin/netboy
* @version 创建时间：2016年6月7日 上午10:00:30
* @func 
*/
public interface RpcProxy {
    List<String> groupProxy(String sourceGroup);

    List<String> ipProxy(String ipkey);

}

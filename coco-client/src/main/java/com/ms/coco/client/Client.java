package com.ms.coco.client;

import com.ms.coco.bean.RpcRequest;
import com.ms.coco.bean.RpcResponse;

/**
* @author wanglin/netboy
* @version 创建时间：2016年5月25日 下午12:44:52
* @func 
*/
public interface Client {
    public RpcResponse send(RpcRequest request);

    void setService(String serviceName);

    void setIpKey(String key);
}

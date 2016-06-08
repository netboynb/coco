package com.ms.coco.policy.balance;

import com.ms.coco.bean.RpcRequest;

/**
* @author wanglin/netboy
* @version 创建时间：2016年6月5日 下午9:52:11
* @func 
*/
public interface LoadBalance {
    void onRefreshPoxy();

    void onRefreshWeight();

    String select(RpcRequest request);

}

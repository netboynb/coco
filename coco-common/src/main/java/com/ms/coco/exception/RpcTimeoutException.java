package com.ms.coco.exception;

/**
* @author wanglin/netboy
* @version 创建时间：2016年5月27日 下午5:13:56
* @func 
*/
public class RpcTimeoutException extends RpcFrameworkException {
    private static final long serialVersionUID = 1L;

    public RpcTimeoutException() {
        super(RpcErrorMsgConstant.SERVICE_TIMEOUT);
    }

    public RpcTimeoutException(String errorMsg) {
        super(RpcErrorMsgConstant.SERVICE_TIMEOUT, errorMsg);
    }

}

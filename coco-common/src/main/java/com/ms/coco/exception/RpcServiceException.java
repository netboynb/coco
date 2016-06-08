package com.ms.coco.exception;

/**
* @author wanglin/netboy
* @version 创建时间：2016年5月31日 下午2:53:49
* @func 
*/
public class RpcServiceException extends RpcFrameworkException {
    private static final long serialVersionUID = 5408950840063989513L;

    public RpcServiceException() {
        super(RpcErrorMsgConstant.SERVICE_DEFAULT_ERROR);
    }

    public RpcServiceException(String errorMsg) {
        super(RpcErrorMsgConstant.SERVICE_DEFAULT_ERROR, errorMsg);
    }

    public RpcServiceException(Throwable cause) {
        super(RpcErrorMsgConstant.SERVICE_DEFAULT_ERROR, cause);
    }

    public RpcServiceException(String message, Throwable cause) {
        super(message, RpcErrorMsgConstant.SERVICE_DEFAULT_ERROR, cause);
    }

    public RpcServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

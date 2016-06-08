
package com.ms.coco.exception;

/**
 * wrapper biz exception.
 * 
 * 
 */
public class RpcBizException extends RpcFrameworkException {
    private static final long serialVersionUID = -3491276058323309898L;

    public RpcBizException() {
        super(RpcErrorMsgConstant.BIZ_DEFAULT_EXCEPTION);
    }

    public RpcBizException(RpcErrorMsg rpcErrorMsg) {
        super(rpcErrorMsg);
    }

    public RpcBizException(String message) {
        super(RpcErrorMsgConstant.BIZ_DEFAULT_EXCEPTION, message);
    }

    public RpcBizException(String message, RpcErrorMsg rpcErrorMsg) {
        super(rpcErrorMsg, message);
    }

    public RpcBizException(String message, Throwable cause) {
        super(message, RpcErrorMsgConstant.BIZ_DEFAULT_EXCEPTION, cause);
    }

    public RpcBizException(String message, Throwable cause, RpcErrorMsg rpcErrorMsg) {
        super(message, rpcErrorMsg, cause);
    }

    public RpcBizException(Throwable cause) {
        super(RpcErrorMsgConstant.BIZ_DEFAULT_EXCEPTION, cause);
    }

    public RpcBizException(Throwable cause, RpcErrorMsg rpcErrorMsg) {
        super(rpcErrorMsg, cause);
    }
}

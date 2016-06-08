package com.ms.coco.exception;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年5月29日 下午8:47:29
 * @func
 */
public class RpcFrameworkException extends RuntimeException {
    private static final long serialVersionUID = -396402552761854042L;
    private RpcErrorMsg rpcErrorMsg = RpcErrorMsgConstant.SERVICE_DEFAULT_ERROR;
    private String errorMsg;
    private String methodClass;
    public RpcFrameworkException() {
        super();
    }

    public RpcFrameworkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RpcFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcFrameworkException(RpcErrorMsg rpcErrorMsg, Throwable cause) {
        super(cause);
        this.rpcErrorMsg = rpcErrorMsg;
    }
    public RpcFrameworkException(String message, RpcErrorMsg rpcErrorMsg, Throwable cause) {
        super(message, cause);
        this.rpcErrorMsg = rpcErrorMsg;
    }

    public RpcFrameworkException(String message) {
        super(message);
    }

    public RpcFrameworkException(Throwable cause) {
        super(cause);
    }

    public RpcFrameworkException(RpcErrorMsg rpcErrorMsg, String errorMsg) {
        super();
        this.rpcErrorMsg = rpcErrorMsg;
        this.errorMsg = errorMsg;
    }

    public RpcFrameworkException(RpcErrorMsg rpcErrorMsg) {
        super();
        this.rpcErrorMsg = rpcErrorMsg;
    }

    @Override
    public String getMessage() {
        if (rpcErrorMsg == null) {
            return super.getMessage();
        }

        String message;

        if (errorMsg != null && !"".equals(errorMsg)) {
            message = errorMsg;
        } else {
            message = rpcErrorMsg.getMessage();
        }

        return "rid=" + rpcErrorMsg.getRid() + ", error_message: " + message + ", status: " + rpcErrorMsg.getStatus()
                + ", error_code: "
                + rpcErrorMsg.getErrorcode() + "\n methodClass=" + methodClass;
    }

    public void addMethodClass(String methodClass) {
        this.methodClass = methodClass;
    }
    public int getStatus() {
        return rpcErrorMsg != null ? rpcErrorMsg.getStatus() : 0;
    }

    public int getErrorCode() {
        return rpcErrorMsg != null ? rpcErrorMsg.getErrorcode() : 0;
    }

    public RpcErrorMsg getRpcErrorMsg() {
        return rpcErrorMsg;
    }

    public RpcFrameworkException setRid(long rid) {
        if (rpcErrorMsg != null) {
            rpcErrorMsg.setRid(rid);
        }
        return this;
    }
}

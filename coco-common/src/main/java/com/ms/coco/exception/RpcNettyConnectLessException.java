
package com.ms.coco.exception;

/**
 * 
 * TODO: DOCUMENT ME!
 * 
 * @author netboy
 */
public class RpcNettyConnectLessException extends RpcFrameworkException {
	private static final long serialVersionUID = 1L;

    public RpcNettyConnectLessException() {
        super(RpcErrorMsgConstant.SERVICE_CONNECT_LOST);
	}

    public RpcNettyConnectLessException(String msg) {
        super(RpcErrorMsgConstant.SERVICE_CONNECT_LOST, msg);
    }

    public RpcNettyConnectLessException(String message, Throwable cause) {
        super(message, RpcErrorMsgConstant.SERVICE_CONNECT_LOST, cause);
    }

}

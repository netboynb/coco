package com.ms.coco.netty.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.coco.bean.RpcResponse;

import io.netty.util.concurrent.Promise;

/**
 * 
 * TODO: DOCUMENT ME!
 * 
 * @author netboy
 */
public class ResponsePromise {
	private static final Logger logger = LoggerFactory.getLogger(ResponsePromise.class);

	private long rid;
    private Promise<RpcResponse> promise;

	public ResponsePromise(long rid) {
		this.rid = rid;
	}

    public void setResponsePromise(Promise<RpcResponse> promise) {
        this.promise = promise;
	}

	public long getRid() {
		return rid;
	}

	void setRid(long rid) {
		this.rid = rid;
	}

    public void receiveResponse(RpcResponse rpcResponse) {
		assert promise != null;

        promise.setSuccess(rpcResponse);
	}

	public void setFailure(Throwable cause) {
		promise.setFailure(cause);
	}

    public RpcResponse waitResult(long timeout) {
		assert promise != null;

		promise.awaitUninterruptibly(timeout);

		if (promise.isDone()) {
			if (promise.isSuccess()) {
				return promise.getNow();
			} else {
				logger.warn("rid=[" + rid + "] response not success", promise.cause());
			}
		}
		return null;
	}
}

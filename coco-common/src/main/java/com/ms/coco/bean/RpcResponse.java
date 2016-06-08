package com.ms.coco.bean;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.ms.coco.exception.RpcFrameworkException;
import com.ms.coco.util.RpcUtils;

/**
 * 封装 RPC 响应
 *
 * @author wanglin/netboy
 * @since 1.0.0
 */
public class RpcResponse {

    private Long rid;
    private RpcFrameworkException exception;
    private Object result;
    private RpcUtils.ExcepEvn excepEvn;

    public RpcResponse() {
        super();
    }

    public RpcResponse(Long rid, RpcFrameworkException exception) {
        super();
        this.rid = rid;
        this.exception = exception;
    }

    public RpcResponse(RpcFrameworkException exception, Object result) {
        super();
        this.exception = exception;
        this.result = result;
    }

    public boolean hasException() {
        return exception != null;
    }

    public Long getRid() {
        return rid;
    }

    public RpcResponse setRid(Long rid) {
        this.rid = rid;
        return this;
    }

    public boolean hashException() {
        return exception != null ? true : false;
    }
    public RpcFrameworkException getException() {
        return exception;
    }

    public RpcResponse setException(RpcFrameworkException exception) {
        this.exception = exception;
        return this;
    }

    public Object getResult() {
        return result;
    }

    public RpcResponse setResult(Object result) {
        this.result = result;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rid, result, excepEvn);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RpcResponse) {
            RpcResponse that = (RpcResponse) obj;
            return Objects.equal(rid, that.rid) && Objects.equal(result, that.result);
        }
        return false;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("rid", rid)
                .add("result",result)
                .add("excepEvn",excepEvn)
                .add("exception",exception)
                .toString();
    }

}

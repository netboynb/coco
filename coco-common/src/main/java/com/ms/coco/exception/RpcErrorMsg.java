package com.ms.coco.exception;

import java.io.Serializable;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年5月30日 下午8:35:04
 * @func
 */
public class RpcErrorMsg implements Serializable {
    private static final long serialVersionUID = 4909459500370103048L;
    private Long rid;
    private Integer status;
    private Integer errorcode;
    private String message;

    public RpcErrorMsg(Long rid, Integer status, Integer errorcode, String message) {
        super();
        this.status = status;
        this.errorcode = errorcode;
        this.message = message;
        this.rid = rid;
    }

    public RpcErrorMsg(Integer status, Integer errorcode, String message) {
        this(null, status, errorcode, message);
    }

    public Long getRid() {
        return rid;
    }

    public RpcErrorMsg setRid(Long rid) {
        this.rid = rid;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public RpcErrorMsg setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Integer getErrorcode() {
        return errorcode;
    }

    public RpcErrorMsg setErrorcode(Integer errorcode) {
        this.errorcode = errorcode;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public RpcErrorMsg setMessage(String message) {
        this.message = message;
        return this;
    }

}

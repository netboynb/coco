package com.ms.coco.bean;

import com.coco.utils.web.BaseDO;
import com.ms.coco.num.MsgType;

/**
 * 封装 RPC 请求
 *
 * @author wanglin/netboy
 * @since 1.0.0
 */
public class RpcRequest extends BaseDO {

    private Long rid;
    private String interfaceName;
    private MsgType msgType = MsgType.REQ;
    private String version;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private String route;
    private String hashValue;
    private String ipKey;
    private String serviceName;
    private Long timeout;

    public static RpcRequest newReq() {
        return new RpcRequest();
    }

    public static RpcRequest newPing() {
        return new RpcRequest().setMsgType(MsgType.PING);
    }
    public RpcRequest() {
        super();
    }

    public RpcRequest(Long rid, MsgType msgType, String version, String ipKey, String serviceName, Long timeout) {
        super();
        this.rid = rid;
        this.msgType = msgType;
        this.version = version;
        this.ipKey = ipKey;
        this.serviceName = serviceName;
        this.timeout = timeout;
    }


    public RpcRequest(Long rid, String interfaceName, MsgType reqType, String version, String methodName,
            Class<?>[] parameterTypes, Object[] parameters, String route, String hashValue, String ipKey,
            String serviceName, Long timeout) {
        super();
        this.rid = rid;
        this.interfaceName = interfaceName;
        this.msgType = reqType;
        this.version = version;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.route = route;
        this.hashValue = hashValue;
        this.ipKey = ipKey;
        this.serviceName = serviceName;
        this.timeout = timeout;
    }


    public MsgType getMsgType() {
        return msgType;
    }

    public RpcRequest setMsgType(MsgType msgType) {
        this.msgType = msgType;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public RpcRequest setVersion(String version) {
        this.version = version;
        return this;
    }

    public RpcRequest setTimeout(Long timeout) {
        this.timeout = timeout;
        return this;
    }

    public Long getRid() {
        return rid;
    }

    public RpcRequest setRid(Long rid) {
        this.rid = rid;
        return this;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public RpcRequest setInterfaceName(String className) {
        this.interfaceName = className;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public RpcRequest setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public RpcRequest setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public RpcRequest setParameters(Object[] parameters) {
        this.parameters = parameters;
        return this;
    }

    public String getRoute() {
        return route;
    }

    public RpcRequest setRoute(String route) {
        this.route = route;
        return this;
    }

    public String getHashValue() {
        return hashValue;
    }

    public RpcRequest setHashValue(String hashValue) {
        this.hashValue = hashValue;
        return this;
    }

    public String getIpKey() {
        return ipKey;
    }

    public RpcRequest setIpKey(String ipKey) {
        this.ipKey = ipKey;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public RpcRequest setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public Long getTimeout() {
        return timeout;
    }

    public RpcRequest setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

}

package com.ms.coco.bean;

/**
 * 封装 RPC 请求
 *
 * @author wanglin/netboy
 * @since 1.0.0
 */
public class RpcRequest {

    private Long rid;
    private String interfaceName;
    private String serviceVersion;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private String route;
    private String hashValue;
    private String ipKey;
    private String serviceName;
    private Long timeout;


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

    public void setInterfaceName(String className) {
        this.interfaceName = className;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
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

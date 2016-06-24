package com.ms.coco.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.coco.bean.RpcRequest;
import com.ms.coco.bean.RpcResponse;
import com.ms.coco.exception.RpcBizException;
import com.ms.coco.exception.RpcFrameworkException;
import com.ms.coco.exception.RpcServiceException;
import com.ms.coco.netty.client.RpcNettyClient;
import com.ms.coco.registry.ServiceDiscovery;
import com.ms.coco.util.StringUtil;

/**
 * RPC 代理（用于创建 RPC 服务代理）
 *
 * @author wanglin/netboy
 * @since 1.0.0
 */
public class RpcProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    private String serviceAddress;

    private ServiceDiscovery serviceDiscovery;
    private Map<String, Client> clientMap = new ConcurrentHashMap<>();
    private Long reqTimeout = 3000L;
    public RpcProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass, "");
    }

    public synchronized Client getClient(String key) {
        Client client = clientMap.get(key);
        if (client != null) {
            return client;
        }
        // 从 RPC 服务地址中解析主机名与端口号
        String[] array = StringUtil.split(serviceAddress, ":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);
        // 创建 RPC 客户端对象并发送 RPC 请求
        Client client1 = new RpcNettyClient(host, port);
        clientMap.put(key, client1);
        return client1;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass, final String serviceVersion) {
        // 创建动态代理对象
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
                        if (method == null) {
                            throw new RpcBizException("invoke method can not be null ");
                        }
                        RpcRequest request = new RpcRequest();
                        request.setInterfaceName(method.getDeclaringClass().getName());
                        request.setServiceVersion(serviceVersion);
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        request.setTimeout(reqTimeout);
                        // 获取 RPC 服务地址
                        String serviceName = interfaceClass.getName();
                        if (serviceDiscovery != null) {
                            if (StringUtil.isNotEmpty(serviceVersion)) {
                                serviceName += "-" + serviceVersion;
                            }
                            serviceAddress = serviceDiscovery.discover(serviceName);
                        }
                        if (StringUtil.isEmpty(serviceAddress)) {
                            throw new RpcFrameworkException("server address is empty");
                        }
                        Client client = getClient(serviceName);
                        long time = System.currentTimeMillis();
                        RpcResponse response = null;

                        response = client.send(request);

                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("time: {}ms", System.currentTimeMillis() - time);
                        }

                        // deal the exception
                        if (response.hasException()) {
                            RpcFrameworkException rpcFrameworkException = response.getException();
                            rpcFrameworkException.addMethodClass(method.toString());

                            if (rpcFrameworkException instanceof RpcBizException) {
                                throw new RpcBizException(rpcFrameworkException);
                            } else {
                                throw new RpcServiceException(rpcFrameworkException);
                            }
                        }
                        return response.getResult();
                    }
                }
        );
    }

    public Long getReqTimeout() {
        return reqTimeout;
    }

    public void setReqTimeout(Long reqTimeout) {
        this.reqTimeout = reqTimeout;
    }

}

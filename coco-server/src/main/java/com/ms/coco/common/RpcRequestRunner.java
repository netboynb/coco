package com.ms.coco.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.coco.bean.RpcRequest;
import com.ms.coco.bean.RpcResponse;
import com.ms.coco.exception.RpcBizException;

import io.netty.channel.ChannelHandlerContext;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年5月26日 下午1:37:02
 * @func
 */
public class RpcRequestRunner implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcRequestRunner.class);
    private final ChannelHandlerContext ctx;
    private final RpcRequest rpcRequest;
    private Object serviceBean;
    private RpcResponse response;

    public RpcRequestRunner(ChannelHandlerContext ctx, RpcRequest request, RpcResponse response, Object serviceBean) {
        this.ctx = ctx;
        this.rpcRequest = request;
        this.serviceBean = serviceBean;
        this.response = response;
    }

    @Override
    public void run() {
        try {
            Object result = handleRequest(rpcRequest);
            response.setResult(result);
        } catch (Throwable t) {
            LOGGER.error("Exception caught when method invoke: " + t.getCause());
            response.setException(new RpcBizException("provider has encountered a fatal error!", t.getCause())
                    .setRid(rpcRequest.getRid()));
        } finally {
            // write response to channel
            writeResponse(response);
        }

    }

    protected Object handleRequest(RpcRequest request) throws Exception {
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        // use cglib go to invoke
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }

    protected void writeResponse(RpcResponse rpcResponse) {
        ctx.writeAndFlush(rpcResponse);
    }
}

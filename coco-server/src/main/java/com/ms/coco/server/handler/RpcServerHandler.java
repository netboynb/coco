package com.ms.coco.server.handler;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.coco.bean.RpcRequest;
import com.ms.coco.bean.RpcResponse;
import com.ms.coco.common.RpcRequestRunner;
import com.ms.coco.common.ThreadPoolInfo;
import com.ms.coco.exception.RpcErrorMsgConstant;
import com.ms.coco.exception.RpcFrameworkException;
import com.ms.coco.util.StringUtil;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * RPC 服务端处理器（用于处理 RPC 请求）
 *
 * @author wanglin/netboy
 * @since 1.0.0
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);

    private final Map<String, Object> handlerMap;
    private ExecutorService workerExecutorService;
    private ThreadPoolInfo threadPoolInfo;

    public RpcServerHandler(Map<String, Object> handlerMap, ExecutorService workerExecutorService) {
        super(true);
        this.handlerMap = handlerMap;
        this.workerExecutorService = workerExecutorService;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        // 创建并初始化 RPC 响应对象
        RpcResponse response = new RpcResponse().setRid(request.getRid());
        // 获取服务对象
        String serviceName = request.getInterfaceName();
        String serviceVersion = request.getServiceVersion();
        if (StringUtil.isNotEmpty(serviceVersion)) {
            serviceName += "-" + serviceVersion;
        }

        Object serviceBean = handlerMap.get(serviceName);
        if (serviceBean == null) {
            response.setException(
                    new RpcFrameworkException(String.format("can not find service bean by key: %s", serviceName))
                            .setRid(request.getRid()));
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return;
        }

        try {
            RpcRequestRunner bizTask = new RpcRequestRunner(ctx, request, response, serviceBean);
            workerExecutorService.submit(bizTask);
        } catch (RejectedExecutionException e) {
            LOGGER.error("rid={},netty server threadPool is busy,no thread left,threadPoolInfo={}", request.getRid(),
                    (threadPoolInfo == null ? null : threadPoolInfo.toString()));
            response.setException(new RpcFrameworkException("netty server threadPool is busy,no thread left",
                    RpcErrorMsgConstant.SERVICE_REJECT, e).setRid(request.getRid()));
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 拦截链路空闲事件并处理心跳
        if (evt instanceof IdleStateEvent) {
            // 心跳处理
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }

    public ThreadPoolInfo getThreadPoolInfo() {
        return threadPoolInfo;
    }

    public RpcServerHandler setThreadPoolInfo(ThreadPoolInfo threadPoolInfo) {
        this.threadPoolInfo = threadPoolInfo;
        return this;
    }

}

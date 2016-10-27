package com.ms.coco.server.handler;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.coco.bean.RpcRequest;
import com.ms.coco.bean.RpcResponse;
import com.ms.coco.common.RpcRequestTask;
import com.ms.coco.common.ThreadPoolInfo;
import com.ms.coco.exception.RpcErrorMsgConstant;
import com.ms.coco.exception.RpcFrameworkException;
import com.ms.coco.num.MsgType;
import com.ms.coco.util.StringUtil;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

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
        if (null == request) {
            LOGGER.error("request = null,we nothing todo ");
            return;
        }
        MsgType msgType = request.getMsgType();
        switch (msgType) {
            case PING:
                dealPing(ctx, request);
                break;
            case REQ:
                dealReq(ctx, request);
                break;
            case ACK:
                dealAck(ctx, request);
                break;
            default:
                break;
        }

    }

    private void dealPing(ChannelHandlerContext ctx, RpcRequest request) {
        // TODO deal with ping info
        String ipKey = request.getIpKey();
        String serviceName = request.getServiceName();
        String version = request.getVersion();

        LOGGER.info("ping info={}", request.toString());
    }

    private void dealReq(ChannelHandlerContext ctx, RpcRequest request) {
        // 创建并初始化 RPC 响应对象
        RpcResponse response = new RpcResponse().setRid(request.getRid());
        // 获取服务对象
        String serviceName = request.getInterfaceName();
        String serviceVersion = request.getVersion();
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
            RpcRequestTask bizTask = new RpcRequestTask(ctx, request, response, serviceBean);
            workerExecutorService.submit(bizTask);
        } catch (RejectedExecutionException e) {
            LOGGER.error("rid={},netty server threadPool is busy,no thread left,threadPoolInfo={}", request.getRid(),
                    (threadPoolInfo == null ? null : threadPoolInfo.toString()));
            response.setException(new RpcFrameworkException("netty server threadPool is busy,no thread left",
                    RpcErrorMsgConstant.SERVICE_REJECT, e).setRid(request.getRid()));
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void dealAck(ChannelHandlerContext ctx, RpcRequest request) {

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

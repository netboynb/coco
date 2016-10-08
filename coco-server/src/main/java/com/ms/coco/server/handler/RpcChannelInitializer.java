package com.ms.coco.server.handler;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.ms.coco.bean.RpcRequest;
import com.ms.coco.bean.RpcResponse;
import com.ms.coco.codec.RpcDecoder;
import com.ms.coco.codec.RpcEncoder;
import com.ms.coco.common.ThreadPoolInfo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

/**
* @author wanglin/netboy
* @version 创建时间：2016年5月25日 下午3:36:00
* @func 
*/
public class RpcChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final int MB = 1024 * 1024;
    private ExecutorService workerExecutorService;
    private ThreadPoolInfo threadPoolInfo;
    /**
     * 存放 服务名 与 服务对象 之间的映射关系
     */
    private Map<String, Object> handlerMap;

    public RpcChannelInitializer(Map<String, Object> handlerMap, ExecutorService workerExecutorService) {
        super();
        this.handlerMap = handlerMap;
        this.workerExecutorService = workerExecutorService;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("frame-decoder", new LengthFieldBasedFrameDecoder(200 * MB, 0, 4, 0, 4));
        pipeline.addLast("frame-encoder", new LengthFieldPrepender(4));
        pipeline.addLast(new RpcDecoder(RpcRequest.class)); // 解码 RPC 请求
        pipeline.addLast(new RpcEncoder(RpcResponse.class)); // 编码 RPC 响应
        // deal rpc request
        pipeline.addLast(new RpcServerHandler(handlerMap, workerExecutorService).setThreadPoolInfo(threadPoolInfo));
        // add heart ping,保证15s后先触发all_idle,在35s后就会触发IdleState.READER_IDLE（未读操作状态），此时服务器就会将通道关闭
        pipeline.addLast("ping-idle", new IdleStateHandler(15, 25, 35, TimeUnit.SECONDS));
        pipeline.addLast("heartBeat", new HeartBeatHandler());
    }

    public ThreadPoolInfo getThreadPoolInfo() {
        return threadPoolInfo;
    }

    public RpcChannelInitializer setThreadPoolInfo(ThreadPoolInfo threadPoolInfo) {
        this.threadPoolInfo = threadPoolInfo;
        return this;
    }

}

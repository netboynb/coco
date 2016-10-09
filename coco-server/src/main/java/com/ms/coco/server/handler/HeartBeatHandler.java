package com.ms.coco.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.coco.bean.RpcRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年10月8日 下午4:05:48
 * @func
 */
public class HeartBeatHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {}

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        // 拦截链路空闲事件并处理心跳
        if (evt instanceof IdleStateEvent) {
            // 心跳处理
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                // 未进行读操作, 服务器端主动关闭连接
                LOGGER.warn("READER_IDLE,client maybe not exist,we will close the chnnel");
                if (ctx.channel().isOpen()) {
                    ctx.close();
                }
                ctx.close();
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                // 未进行写操作
                LOGGER.info("WRITER_IDLE, long time not write something to client");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                // 未进行读写
                LOGGER.info("ALL_IDLE, long time not to write or read");
                // 发送心跳消息
                // MsgHandleService.getInstance().sendMsgUtil.sendHeartMessage(ctx);
            }
        }
    }
}

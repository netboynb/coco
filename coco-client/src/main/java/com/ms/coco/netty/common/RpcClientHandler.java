/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ms.coco.netty.common;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.coco.bean.RpcRequest;
import com.ms.coco.bean.RpcResponse;
import com.ms.coco.exception.RpcFrameworkException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

/**
 * 
 * TODO: DOCUMENT ME!
 * 
 * @author netboy
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    protected static final String CLIENT_HANDLER_NAME = "rpc_handler";
	private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);
	protected final ResponsePromiseContainer responsePromiseContainer;

	public RpcClientHandler(ResponsePromiseContainer responsePromiseContainer) {
        super(true);
		if (responsePromiseContainer == null) {
			throw new IllegalArgumentException("ResponsePromise maps can't be null!");
		}
		this.responsePromiseContainer = responsePromiseContainer;
	}

	public static RpcClientHandler getRpcClientHandler(Channel channel) {
		RpcClientHandler clientHandler = (RpcClientHandler) channel.pipeline().get(CLIENT_HANDLER_NAME);
		assert clientHandler != null;
		return clientHandler;
	}

	@Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        ResponsePromise responsePromise = responsePromiseContainer.removeResponsePromise(rpcResponse.getRid());
		if (responsePromise != null) {
			logger.debug("receive response={}", rpcResponse);
			responsePromise.receiveResponse(rpcResponse);
		} else {
			SocketAddress remoteAddress = ctx.channel().remoteAddress();
            logger.warn("miss rid='{}' at {} receive response", rpcResponse.getRid(), remoteAddress);
			responsePromise
                    .setFailure(new RpcFrameworkException("receive not registered response, rid=" + rpcResponse.getRid()
                            + ", server=" + remoteAddress).setRid(rpcResponse.getRid()));
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Unexpected exception from downstream. close channel=" + ctx.channel(), cause);
		ctx.close();
	}

    private Promise<RpcResponse> newPromise(Channel channel) {
        return channel.eventLoop().next().<RpcResponse>newPromise();
	}

    public ChannelFuture writeRequest(Channel channel, RpcRequest rpcRequest, final ResponsePromise responsePromise) {
		responsePromise.setResponsePromise(newPromise(channel));
        ChannelFuture writeFuture = channel.writeAndFlush(rpcRequest);
		return writeFuture;
	}

    public void writeRequst(Channel channel, RpcRequest rpcRequest) {
        channel.writeAndFlush(rpcRequest);
    }

	public void removeResponsePromise(long rid) {
		responsePromiseContainer.removeResponsePromise(rid);
	}
}

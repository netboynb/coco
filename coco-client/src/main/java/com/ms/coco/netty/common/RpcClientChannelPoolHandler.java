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
import com.ms.coco.codec.RpcDecoder;
import com.ms.coco.codec.RpcEncoder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * 
 * TODO: DOCUMENT ME!
 * 
 * @author netboy
 */
public class RpcClientChannelPoolHandler implements ChannelPoolHandler {
	private static final Logger logger = LoggerFactory.getLogger(RpcClientChannelPoolHandler.class);

	private static final int MB = 1024 * 1024;

	protected final HandlerConfig handlerConfig;
	protected final SocketAddress socketAddress;

	public RpcClientChannelPoolHandler(HandlerConfig handlerConfig, SocketAddress socketAddress) {
		this.handlerConfig = handlerConfig;
		this.socketAddress = socketAddress;
	}

	@Override
	public void channelReleased(Channel ch) throws Exception {
		logger.debug("channel={} Released", ch);
	}

	@Override
	public void channelAcquired(Channel ch) throws Exception {
		logger.debug("channel={} Acquired", ch);
	}

	@Override
	public void channelCreated(Channel ch) throws Exception {
		initChannel(ch);
        if (logger.isDebugEnabled()) {
            logger.debug("connect [{}] success channel={}", socketAddress, ch);
        }
	}

	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast("frame-decoder", new LengthFieldBasedFrameDecoder(handlerConfig.getMaxFrameLengthMB() * MB, 0, 4, 0, 4));
		pipeline.addLast("frame-encoder", new LengthFieldPrepender(4));

        pipeline.addLast("pb-decoder", new RpcDecoder(RpcResponse.class)); // 解码 RPC 响应
        pipeline.addLast("pb-encoder", new RpcEncoder(RpcRequest.class)); // 编码 RPC 请求

		pipeline.addLast(RpcClientHandler.CLIENT_HANDLER_NAME, new RpcClientHandler(getResponsePromiseContainer()));
	}

	public ResponsePromiseContainer getResponsePromiseContainer() {
		return handlerConfig.getResponsePromiseContainer();
	}
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ms.coco.netty.client;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coco.utils.threads.CocoThreadUtils;
import com.ms.coco.bean.RpcRequest;
import com.ms.coco.bean.RpcResponse;
import com.ms.coco.client.Client;
import com.ms.coco.exception.RpcNettyConnectLessException;
import com.ms.coco.exception.RpcTimeoutException;
import com.ms.coco.netty.common.ConnectionPool;
import com.ms.coco.netty.common.ConnectionPoolContext;
import com.ms.coco.netty.common.HandlerConfig;
import com.ms.coco.netty.common.NettyUtil;
import com.ms.coco.netty.common.ResponsePromise;
import com.ms.coco.netty.common.RpcClientHandler;
import com.ms.coco.netty.common.SimpleConnectionPool;
import com.ms.coco.num.MsgType;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * 
 * TODO: netty rpc client
 * 
 * @author netboy
 */
public class RpcNettyClient implements Client {
    private static final Logger logger = LoggerFactory.getLogger(RpcNettyClient.class);

    private final String serverUrl;
    // default timeout
    private int defaultTimeout = 3000;
    private ConnectionPool connectionPool;
    private String serviceName;
    private String ipPortKey;
    private Integer heartBeatRate;
    private AtomicLong pingCount = new AtomicLong(0);

    public RpcNettyClient(String host, int port, Integer heartBeatRate) {
        this(new SimpleConnectionPool(NettyUtil.DEFAULT_BOOTSTRAP, new InetSocketAddress(host, port)));
        this.heartBeatRate = heartBeatRate;
    }

    public RpcNettyClient(String host, int port, HandlerConfig handlerConfig) {
        this(new SimpleConnectionPool(NettyUtil.DEFAULT_BOOTSTRAP, handlerConfig, new InetSocketAddress(host, port)));
    }

    public RpcNettyClient(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.serverUrl = "netty://" + connectionPool.channelHost() + ":" + connectionPool.channelPort();
        this.ipPortKey = connectionPool.channelHost() + ":" + connectionPool.channelPort();
    }

    @Override
    public RpcResponse send(RpcRequest request) {
        RpcResponse rpcResponse = null;
        // create rid and ResponsePromise
        ConnectionPoolContext poolContext = connectionPool.poolContext();
        ResponsePromise responsePromise = poolContext.createResponsePromise();
        Long rid = responsePromise.getRid();

        RpcClientHandler rpcClientHandler = null;
        request.setRid(rid);
        request.setIpKey(ipPortKey);
        Channel channel = null;
        ChannelFuture writeFuture = null;
        // send netty request
        try {
            channel = connectionPool.acquireConnect();
            rpcClientHandler = RpcClientHandler.getRpcClientHandler(channel);
            // send request
            writeFuture = rpcClientHandler.writeRequest(channel, request, responsePromise);
        } catch (RpcNettyConnectLessException e) {
            rpcResponse = new RpcResponse(rid, e);
            return rpcResponse;
        } finally {
            if (channel != null) {
                connectionPool.releaseConnect(channel);
            }
        }

        boolean writeDone = writeFuture.awaitUninterruptibly(defaultTimeout);
        if (!writeDone) {
            rpcResponse = new RpcResponse(rid,
                    new RpcTimeoutException(rid + defaultTimeout + "write request timeout").setRid(rid));
            return rpcResponse;
        }
        Long reqTimeout = request.getTimeout();
        if (reqTimeout != null) {
            rpcResponse = responsePromise.waitResult(reqTimeout);
        } else {
            rpcResponse = responsePromise.waitResult(defaultTimeout);
        }

        if (rpcResponse == null) {
            rpcResponse = new RpcResponse(rid, new RpcTimeoutException("request timeout").setRid(rid));
            return rpcResponse;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("rid={}, [{}] response size={}", rid, serverUrl());
        }

        return rpcResponse;
    }

    public String serverUrl() {
        return serverUrl;
    }

    public void shutdown() {
        connectionPool.close();
    }


    public void setDefaultTimeout(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    @Override
    public void setService(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void setIpKey(String key) {
        this.ipPortKey = key;
    }

    @Override
    public void startHeartBeat() {
        CocoThreadUtils.getInstance().submitPingTask(new Runnable() {
            @Override
            public void run() {
                long id = pingCount.incrementAndGet();
                try {
                    RpcRequest ping = RpcRequest.newPing().setRid(id).setIpKey(ipPortKey).setMsgType(MsgType.PING);
                    Channel channel = connectionPool.acquireConnect();
                    channel.writeAndFlush(ping);
                    logger.info("id={},ping service={},ipPort={}", id, serviceName, ipPortKey);
                } catch (Exception e) {
                    logger.error("id={},ipPort={},schedule send ping error,info={}", id, ipPortKey, e.toString());
                }

            }
        }, 5, heartBeatRate, TimeUnit.SECONDS);
    }
}

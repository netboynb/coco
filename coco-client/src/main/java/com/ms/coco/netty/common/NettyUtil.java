
package com.ms.coco.netty.common;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 
 * TODO: DOCUMENT ME!
 * 
 * @author netboy
 */
public class NettyUtil {

	public static final Bootstrap DEFAULT_BOOTSTRAP;

	static {
        int threadNum = 5;
		Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup(threadNum));
		bootstrap.channel(NioSocketChannel.class);
        // bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		DEFAULT_BOOTSTRAP = bootstrap;
	}

}

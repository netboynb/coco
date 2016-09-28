package com.ms.coco.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.ms.coco.common.ThreadPoolInfo;
import com.ms.coco.exception.RpcServiceException;
import com.ms.coco.registry.inteface.ServiceRegistry;
import com.ms.coco.rest.server.CocoRestServer;
import com.ms.coco.server.handler.RpcChannelInitializer;
import com.ms.coco.server.service.RpcService;
import com.ms.coco.util.StringUtil;
import com.ms.coco.util.ThreadPoolHolder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * RPC 服务器（用于发布 RPC 服务）
 *
 * @author wanglin/netboy
 * @since 1.0.0
 */
@Component
public class CocoServer implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(CocoServer.class);
    @Autowired
    private CocoRestServer restfulServer;
    @Autowired
    private ServiceRegistry serviceRegistry;

    private Integer rpcPort = 8089;
    private String localIp;
    private int ioWorkerNum = Runtime.getRuntime().availableProcessors() * 2;
    private ThreadPoolInfo threadPoolInfo = new ThreadPoolInfo();
    private boolean useRpc = true;
    private boolean useRestFul = true;

    /**
     * 存放 服务名 与 服务对象 之间的映射关系
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    public CocoServer(Integer rpcPort) {
        this.rpcPort = rpcPort;
    }

    public CocoServer(Integer rpcPort, ServiceRegistry serviceRegistry) {
        this.rpcPort = rpcPort;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        if (useRpc) {
            // 扫描带有 RpcService 注解的类并初始化 handlerMap 对象
            Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
            if (MapUtils.isNotEmpty(serviceBeanMap)) {
                for (Object serviceBean : serviceBeanMap.values()) {
                    RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                    String serviceName = rpcService.value().getName();
                    String serviceVersion = rpcService.version();
                    if (StringUtil.isNotEmpty(serviceVersion)) {
                        serviceName += "-" + serviceVersion;
                    }
                    handlerMap.put(serviceName, serviceBean);
                }
            }
        }
        if (useRestFul) {
            if (restfulServer == null) {
                throw new RuntimeException("restful server bean not be set ,please check it ");
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (useRpc) {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup(ioWorkerNum);
            try {
                // init and start rpc netty server
                LOGGER.info("rpc-netty-server will be started");
                ExecutorService workerExecutorService =
                        ThreadPoolHolder.getStandardFixedPool(threadPoolInfo.getThreadPoolName(),
                                threadPoolInfo.getPoolMinSize(), threadPoolInfo.getPoolmaxSize(),
                                threadPoolInfo.getPoolQueueSize(), threadPoolInfo.getPoolThreadAliveTimeInsecond());
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup);
                bootstrap.channel(NioServerSocketChannel.class);
                bootstrap.childHandler(
                        new RpcChannelInitializer(handlerMap, workerExecutorService).setThreadPoolInfo(threadPoolInfo));
                bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
                bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.option(ChannelOption.TCP_NODELAY, true);
                // use ByteBuf pool
                bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

                localIp = getLocalHostIp();
                ChannelFuture future = bootstrap.bind(localIp, rpcPort).sync();
                LOGGER.info(" end start rpc-netty server ");
                // start restful server
                if (useRestFul) {
                    restfulServer.start();
                }
                String serviceAddress = localIp + ":" + rpcPort;
                serviceRegistry(serviceAddress);
                LOGGER.info("rpc-netty-server started on ip={}, port {}", localIp, rpcPort);
                // black for RPC netty server close
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                throw new RpcServiceException("start rpc-netty-server error");
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
        if (useRestFul) {
            restfulServer.start();
        }

    }

    private void serviceRegistry(String serviceAddress) {
        // 注册 RPC 服务地址
        if (serviceRegistry != null) {
            for (String interfaceName : handlerMap.keySet()) {
                serviceRegistry.register(interfaceName, serviceAddress);
                LOGGER.debug("register service: {} => {}", interfaceName, serviceAddress);
            }
        }
    }

    private String getLocalHostIp() throws UnknownHostException {
        if (localIp != null) {
            // 用于处理多网卡的主机
            return localIp;
        }
        /** 返回本地主机。 */
        InetAddress addr = InetAddress.getLocalHost();
        /** 返回 IP 地址字符串（以文本表现形式） */
        String ip = addr.getHostAddress();
        return ip;
    }

    public int getIoWorkerNum() {
        return ioWorkerNum;
    }

    public void setIoWorkerNum(int ioWorkerNum) {
        this.ioWorkerNum = ioWorkerNum;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public Map<String, Object> getHandlerMap() {
        return handlerMap;
    }

    public void setHandlerMap(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public ThreadPoolInfo getThreadPoolInfo() {
        return threadPoolInfo;
    }

    public void setThreadPoolInfo(ThreadPoolInfo threadPoolInfo) {
        this.threadPoolInfo = threadPoolInfo;
    }

    public void setUseRpc(boolean useRpc) {
        this.useRpc = useRpc;
    }

    public void setUseRestFul(boolean useRestFul) {
        this.useRestFul = useRestFul;
    }

    public void setRpcPort(Integer rpcPort) {
        this.rpcPort = rpcPort;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public void setRestfulServer(CocoRestServer restfulServer) {
        this.restfulServer = restfulServer;
    }

}

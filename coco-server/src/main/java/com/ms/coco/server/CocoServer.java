package com.ms.coco.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ms.coco.CocoRestServer;
import com.ms.coco.common.ThreadPoolInfo;
import com.ms.coco.exception.RpcServiceException;
import com.ms.coco.registry.ServiceRegistry;
import com.ms.coco.server.handler.RpcChannelInitializer;
import com.ms.coco.server.service.RpcService;
import com.ms.coco.util.StringUtil;
import com.ms.coco.util.ThreadPoolHolder;

import io.netty.bootstrap.ServerBootstrap;
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
public class CocoServer implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(CocoServer.class);

    private String serviceAddress;
    private int ioWorkerNum = Runtime.getRuntime().availableProcessors() * 2;
    private ServiceRegistry serviceRegistry;
    private ThreadPoolInfo threadPoolInfo = new ThreadPoolInfo();
    private boolean useRpc = true;
    private boolean useRestFul = true;
    private CocoRestServer restfulServer;

    /**
     * 存放 服务名 与 服务对象 之间的映射关系
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    public CocoServer(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public CocoServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
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
            restfulServer.init();
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (useRpc) {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup(ioWorkerNum);
            try {
                ExecutorService workerExecutorService =
                        ThreadPoolHolder.getStandardFixedPool(threadPoolInfo.getThreadPoolName(),
                                threadPoolInfo.getPoolMinSize(), threadPoolInfo.getPoolmaxSize(),
                                threadPoolInfo.getPoolQueueSize(), threadPoolInfo.getPoolThreadAliveTimeInsecond());

                // 创建并初始化 Netty 服务端 Bootstrap 对象
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup);
                bootstrap.channel(NioServerSocketChannel.class);
                bootstrap.childHandler(
                        new RpcChannelInitializer(handlerMap, workerExecutorService).setThreadPoolInfo(threadPoolInfo));
                bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
                bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

                // 获取 RPC 服务器的 IP 地址与端口号
                String[] addressArray = StringUtil.split(serviceAddress, ":");
                String ip = addressArray[0];
                int port = Integer.parseInt(addressArray[1]);
                // 启动 RPC 服务器
                ChannelFuture future = bootstrap.bind(ip, port).sync();

                // start restful server
                if (useRestFul) {
                    restfulServer.start();
                }

                serviceRegistry();
                LOGGER.debug("server started on port {}", port);
                // 关闭 RPC 服务器
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                throw new RpcServiceException("init netty server error");
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
        if (useRestFul) {
            restfulServer.start();
        }

    }

    private void serviceRegistry() {
        // 注册 RPC 服务地址
        if (serviceRegistry != null) {
            for (String interfaceName : handlerMap.keySet()) {
                serviceRegistry.register(interfaceName, serviceAddress);
                LOGGER.debug("register service: {} => {}", interfaceName, serviceAddress);
            }
        }
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
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

    public void setRestfulServer(CocoRestServer restfulServer) {
        this.restfulServer = restfulServer;
    }

}

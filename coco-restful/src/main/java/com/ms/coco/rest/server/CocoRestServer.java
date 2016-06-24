package com.ms.coco.rest.server;

import java.util.Collection;

import javax.annotation.PreDestroy;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

@Component
public class CocoRestServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CocoRestServer.class);
	@Autowired
	ApplicationContext				ac;

    private String rootResourcePath = "/resteasy";
    private int port = 8082;
    private int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
    private int executorThreadCount = 16;

    private NettyJaxrsServer netty;

	public void start() {

		ResteasyDeployment dp = new ResteasyDeployment();

		Collection<Object> providers = ac.getBeansWithAnnotation(Provider.class).values();
		Collection<Object> controllers = ac.getBeansWithAnnotation(Controller.class).values();

		Assert.notEmpty(controllers);

		// extract providers
		if (providers != null) {
			dp.getProviders().addAll(providers);
		}
		// extract only controller annotated beans
		dp.getResources().addAll(controllers);
		
        netty = new NettyJaxrsServer();
		netty.setDeployment(dp);
        netty.setIoWorkerCount(ioWorkerCount);
        netty.setExecutorThreadCount(executorThreadCount);
		netty.setPort(port);
        netty.setRootResourcePath(rootResourcePath);
		netty.setSecurityDomain(null);
		netty.start();
        LOGGER.info("rest-netty-server started , port {}", port);
	}

	@PreDestroy
	public void cleanUp() {
		netty.stop();
	}

	public String getRootResourcePath() {
		return rootResourcePath;
	}

	public int getPort() {
		return port;
	}

    public int getIoWorkerCount() {
        return ioWorkerCount;
    }

    public CocoRestServer setIoWorkerCount(int ioWorkerCount) {
        this.ioWorkerCount = ioWorkerCount;
        return this;
    }

    public int getExecutorThreadCount() {
        return executorThreadCount;
    }

    public CocoRestServer setExecutorThreadCount(int executorThreadCount) {
        this.executorThreadCount = executorThreadCount;
        return this;
    }


    public CocoRestServer setRootResourcePath(String rootResourcePath) {
        this.rootResourcePath = rootResourcePath;
        return this;
    }

    public CocoRestServer setPort(int port) {
        this.port = port;
        return this;
    }

    public CocoRestServer setAc(ApplicationContext ac) {
        this.ac = ac;
        return this;
    }

}

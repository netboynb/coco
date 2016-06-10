package com.ms.coco.conf;

import java.util.Properties;

import org.restexpress.Format;
import org.restexpress.RestExpress;

import io.netty.bootstrap.Bootstrap;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年6月8日 下午8:55:48
 * @func
 */
public class DefaultCocoConf implements CocoConf {

    private static final String NAME_PROPERTY = "coco.restful.server.serviceName";
    private static final String PORT_PROPERTY = "coco.restful.server.port";
    private static final String DEFAULT_FORMAT_PROPERTY = "coco.restful.server.defaultFormat";
    private static final String WORKER_COUNT_PROPERTY = "coco.restful.server.workerCount";
    private static final String EXECUTOR_THREAD_COUNT_PROPERTY = "coco.restful.server.executorThreadCount";

    private static final int DEFAULT_WORKER_COUNT = 0;
    private static final int DEFAULT_EXECUTOR_THREAD_COUNT = 0;

    private int port;
    private String serviceName;
    private String defaultFormat;
    private int workerCount;
    private int executorThreadCount;
    private String fileName = "cocoServerConf.properties";
    private boolean usePropertyConf = true;

    protected void fillValues(Properties p) {
        this.serviceName = p.getProperty(NAME_PROPERTY, RestExpress.DEFAULT_NAME);
        this.port = Integer.parseInt(p.getProperty(PORT_PROPERTY, String.valueOf(RestExpress.DEFAULT_PORT)));
        this.defaultFormat = p.getProperty(DEFAULT_FORMAT_PROPERTY, Format.JSON);
        this.workerCount = Integer.parseInt(p.getProperty(WORKER_COUNT_PROPERTY, String.valueOf(DEFAULT_WORKER_COUNT)));
        this.executorThreadCount = Integer
                .parseInt(p.getProperty(EXECUTOR_THREAD_COUNT_PROPERTY, String.valueOf(DEFAULT_EXECUTOR_THREAD_COUNT)));
    }

    @Override
    public void init() {
        if (usePropertyConf) {
            Properties prop = new Properties();

            try {
                prop.load(Bootstrap.class.getClassLoader().getResourceAsStream(fileName));
            } catch (Exception e) {
                throw new RuntimeException("load " + fileName + " error", e);
            }
            fillValues(prop);
        }
    }


    public String getDefaultFormat() {
        return defaultFormat;
    }

    public Integer getPort() {
        return port;
    }


    public Integer getWorkerCount() {
        return workerCount;
    }

    public Integer getExecutorThreadCount() {
        return executorThreadCount;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setDefaultFormat(String defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public void setExecutorThreadCount(int executorThreadCount) {
        this.executorThreadCount = executorThreadCount;
    }

    public boolean isUsePropertyConf() {
        return usePropertyConf;
    }

    public void setUsePropertyConf(boolean usePropertyConf) {
        this.usePropertyConf = usePropertyConf;
    }

}

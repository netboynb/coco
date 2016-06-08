package com.ms.coco.conf;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年6月8日 下午7:32:16
 * @func
 */
public interface CocoConf {
    void init();

    String getServiceName();

    Integer getPort();

    Integer getWorkerCount();

    Integer getExecutorThreadCount();
}

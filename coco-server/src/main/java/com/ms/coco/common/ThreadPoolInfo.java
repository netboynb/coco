package com.ms.coco.common;

import com.google.common.base.MoreObjects;

/**
* @author wanglin/netboy
* @version 创建时间：2016年6月3日 上午10:32:48
* @func 
*/
public class ThreadPoolInfo {
    String threadPoolName = "rpc-business-pool";
    Integer poolMinSize;
    Integer poolmaxSize = 20;
    Integer poolQueueSize;
    Long poolThreadAliveTimeInsecond;

    public ThreadPoolInfo(String threadPoolName, Integer poolMinSize, Integer poolmaxSize, Integer poolQueueSize,
            Long poolThreadAliveTimeInsecond) {
        super();
        this.threadPoolName = threadPoolName;
        this.poolMinSize = poolMinSize;
        this.poolmaxSize = poolmaxSize;
        this.poolQueueSize = poolQueueSize;
        this.poolThreadAliveTimeInsecond = poolThreadAliveTimeInsecond;
    }

    public ThreadPoolInfo() {
        super();
    }

    public String toString() {
        return MoreObjects.toStringHelper(ThreadPoolInfo.class).add("threadPoolName", threadPoolName)
                .add("poolMinSize", poolMinSize).add("poolmaxSize", poolmaxSize).add("poolQueueSize", poolQueueSize)
                .add("poolThreadAliveTimeInsecond", poolThreadAliveTimeInsecond).toString();
    }

    public String getThreadPoolName() {
        return threadPoolName;
    }

    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }

    public Integer getPoolMinSize() {
        return poolMinSize;
    }

    public void setPoolMinSize(Integer poolMinSize) {
        this.poolMinSize = poolMinSize;
    }

    public Integer getPoolmaxSize() {
        return poolmaxSize;
    }

    public void setPoolmaxSize(Integer poolmaxSize) {
        this.poolmaxSize = poolmaxSize;
    }

    public Integer getPoolQueueSize() {
        return poolQueueSize;
    }

    public void setPoolQueueSize(Integer poolQueueSize) {
        this.poolQueueSize = poolQueueSize;
    }

    public Long getPoolThreadAliveTimeInsecond() {
        return poolThreadAliveTimeInsecond;
    }

    public void setPoolThreadAliveTimeInsecond(Long poolThreadAliveTimeInsecond) {
        this.poolThreadAliveTimeInsecond = poolThreadAliveTimeInsecond;
    }

}

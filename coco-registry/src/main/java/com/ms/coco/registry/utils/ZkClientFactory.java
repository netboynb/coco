package com.ms.coco.registry.utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年08月15日 下午8:30:46
 * @func 单实例模式 根据线程池名字 获取全局同一个线程池
 */
public class ZkClientFactory {
    /**
     * 空的 json
     */
    protected static final byte[] DEFAULT_DATA = "{}".getBytes(Charsets.UTF_8);

    private CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();

    public ZkClientFactory() {
        builder.defaultData(DEFAULT_DATA);
    }

    /**
     * 
     * TODO: 返回 CuratorFramework 注意 要调用 CuratorFramework.start()方法
     *
     * @param zkurl
     * @return
     */
    public CuratorFramework setAllAndGetCuratorFramework(String zkurl) {
        setAllAndGetClientFactory(zkurl);
        return this.newCuratorFramework();
    }

    /**
     * 
     * TODO: 返回 CuratorFramework 注意 要调用 CuratorFramework.start()方法
     *
     * @param zkurl zookeeper 集群的zkurl
     * @param retryPolicy 重试策略 建议使用 ExponentialBackoffRetry、BoundedExponentialBackoffRetry
     * @param printMemFile 检测打印内存结构的文件名
     * @param flushMemFile 检测强制刷新内存结构的文件名
     * @return CuratorFramework
     */
    public CuratorFramework setAllAndGetCuratorFramework(String zkurl, RetryPolicy retryPolicy) {
        setAllAndGetClientFactory(zkurl, retryPolicy);
        return this.newCuratorFramework();
    }

    /**
     * 
     * TODO: 默认 matrix-print-mem matrix-flush-mem 事务超时时间为15000ms 链接超时时间为30000ms 重试策略为间隔3秒 重试6次
     *
     * @param zkurl
     * @return
     */
    public ZkClientFactory setAllAndGetClientFactory(String zkurl) {
        Preconditions.checkNotNull(zkurl, "pram [ zkurl ] can not be null");
        this.setConnectString(zkurl).setRetryPolicy(new ExponentialBackoffRetry(3000, 3)).setSessionTimeoutMs(15000)
                .setConnectionTimeoutMs(15000);
        return this;
    }

    public ZkClientFactory setAllAndGetClientFactory(String zkurl, RetryPolicy retryPolicy) {
        Preconditions.checkNotNull(zkurl, "zkurl can not be null");
        Preconditions.checkNotNull(retryPolicy, "retryPolicy can not be null");
        this.setConnectString(zkurl).setRetryPolicy(retryPolicy).setSessionTimeoutMs(15000).setConnectionTimeoutMs(30000);
        return this;
    }

    public ZkClientFactory setConnectString(String connectString) {
        builder.connectString(connectString);
        return this;
    }

    public ZkClientFactory setSessionTimeoutMs(int sessionTimeoutMs) {
        builder.sessionTimeoutMs(sessionTimeoutMs);
        return this;
    }

    public ZkClientFactory setConnectionTimeoutMs(int connectionTimeoutMs) {
        builder.connectionTimeoutMs(connectionTimeoutMs);
        return this;
    }

    public ZkClientFactory setRetryPolicy(RetryPolicy retryPolicy) {
        builder.retryPolicy(retryPolicy);
        return this;
    }

    public CuratorFramework newCuratorFramework() {
        return builder.build();
    }

}

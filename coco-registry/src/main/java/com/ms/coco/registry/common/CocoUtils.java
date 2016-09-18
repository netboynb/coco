package com.ms.coco.registry.common;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ms.coco.registry.model.FileNameExclusionStrategy;
import com.ms.coco.registry.model.ServerNode;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月13日 下午9:58:03
 * @func
 */
public class CocoUtils {
    public static final Gson GSON =
            new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .setExclusionStrategies(new FileNameExclusionStrategy("key")).create();

    /**
     * 创建定时执行的线程池
     */
    public static ScheduledExecutorService createScheduledPool(int corePoolSize, String threadName, boolean isDaemon) {
        ThreadFactory threadFactory =
                new ThreadFactoryBuilder().setNameFormat(threadName + "-%d").setDaemon(isDaemon).build();
        return Executors.newScheduledThreadPool(corePoolSize, threadFactory);
    }

    /**
     * 创建指定线程数目的线程池
     */
    public static ExecutorService createFixedPool(int corePoolSize, String threadName, boolean isDaemon) {
        ThreadFactory threadFactory =
                new ThreadFactoryBuilder().setNameFormat(threadName + "-%d").setDaemon(isDaemon).build();
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
    }


    public static String buildPath(String... pathName) {
        List<String> list = Lists.newArrayList(pathName);
        list.add(0, "");
        return Joiner.on("/").skipNulls().join(list);
    }

    /**
     * 
     * TODO: split one string to array,using specify string
     *
     * @param targetStr eg: "service/group"
     * @param separator eg:"/"
     * @return eg:[service,group]
     */
    public static List<String> splitStr(String targetStr, String separator) {
        if (Strings.isNullOrEmpty(targetStr))
            return null;
        return Splitter.on(separator).omitEmptyStrings().trimResults().splitToList(targetStr);
    }

    /**
     * 
     * TODO: 检测zk上是否存在指定节点，存在返回true
     *
     * @param curator
     * @param path eg ："/service/group"
     * @return
     * @throws Exception
     */
    public static boolean checkNode(CuratorFramework curator, String path) throws Exception {
        if (null == curator.checkExists().forPath(path)) {
            return false;
        }
        return true;
    }

    /**
     * 
     * TODO: DOCUMENT ME!
     *
     * @param curator
     * @param path eg: "/service/group"
     * @throws Exception
     */
    public static void createNode(CuratorFramework curator, String path) throws Exception {
        if (checkNode(curator, path)) {
            return;
        }
        curator.create().creatingParentsIfNeeded().forPath(path);
    }

    /**
     * 
     * TODO: 设置或者更新节点的数据
     *
     * @param curator
     * @param data eg: 22
     * @param path eg: "/service/group/status"
     * @throws Exception
     */
    public static void createOrUpdateNode(CuratorFramework curator, String path, Object data) throws Exception {
        Preconditions.checkNotNull(data, "Argument data  can not be null");
        Preconditions.checkNotNull(path, "Argument path  can not be null");
        Stat stat = curator.checkExists().forPath(path);
        if (stat == null) {
            curator.create().creatingParentsIfNeeded().forPath(path, data.toString().getBytes());
        } else {
            curator.setData().forPath(path, data.toString().getBytes());
        }
    }

    public static void deleteNode(CuratorFramework curator, String path) throws Exception {
        Preconditions.checkNotNull(curator, "Argument curator  can not be null");
        Preconditions.checkNotNull(path, "Argument path  can not be null");
        Stat stat = curator.checkExists().forPath(path);
        if (stat != null) {
            curator.delete().forPath(path);
        }
    }

    public static Object getPathaData(CuratorFramework curatorFramework, String path) throws Exception {
        Preconditions.checkNotNull(curatorFramework, " curatorFramework can not be null");
        Preconditions.checkNotNull(path, "path can not be null");
        Object object = null;
        if (!checkNode(curatorFramework, path)) {
            return null;
        }
        byte[] dataByte = curatorFramework.getData().forPath(path);
        object = new String(dataByte, Charsets.UTF_8);
        return object;
    }

    public static void removePath(CuratorFramework curator, String path) throws Exception {
        if (!checkNode(curator, path)) {
            return;
        }
        curator.delete().forPath(path);
    }

    public static void createOrUpdateServerNode(CuratorFramework curator, ServerNode serverNode, String parentPath)
            throws Exception {
        String path = ZKPaths.makePath(parentPath, String.valueOf(serverNode.hostKey()));
        if (curator.checkExists().forPath(path) == null) {
            curator.create().creatingParentsIfNeeded().forPath(path, serverNode.toNodeJsonBinary());
        } else {
            curator.setData().forPath(path, serverNode.toNodeJsonBinary());
        }
    }
}

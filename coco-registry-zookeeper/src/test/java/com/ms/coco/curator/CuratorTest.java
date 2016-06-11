package com.ms.coco.curator;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.ms.coco.registry.zk.common.RegisterHolder;

import junit.framework.Assert;

/**
 * 
 * TODO: curator test func
 * 
 * @author wanglin
 */
public class CuratorTest {

    private static final Logger logger = LoggerFactory.getLogger(CuratorTest.class);

    TestingServer zkTestServer;
    CuratorFramework client;
    String namespace = "micro-service";
    String serviceNode = "service";
    String groupNode = "group";
    String proxyNode = "proxy";
    String groupProxyNode = "groupProxy";
    String ipProxyNode = "ipProxy";
    String balanceNode = "balance";
    String servicePath;
    String groupPath;
    String groupProxyPath;
    String ipProxyPath;
    String balancePath;
    String group_1_path;
    String group_2_path;

    @Before
    public void before() throws Exception {
        zkTestServer = new TestingServer(2182);

        zkTestServer.start();
        String registerUrl = zkTestServer.getConnectString();
        client = RegisterHolder.getClient(registerUrl);
        servicePath = joinNodePath(namespace, serviceNode);
        groupPath = joinNodePath(namespace, groupNode);
        group_1_path = joinNodePath(namespace, groupNode, "group_1");
        group_2_path = joinNodePath(namespace, groupNode, "group_2");
        groupProxyPath = joinNodePath(namespace, proxyNode, groupProxyNode);
        ipProxyPath = joinNodePath(namespace, proxyNode, ipProxyNode);
        balancePath = joinNodePath(namespace, balanceNode);
        client.create().forPath(servicePath);
        client.create().forPath(groupPath);
        client.create().forPath(group_1_path);
        client.create().forPath(group_2_path);
        client.create().forPath(groupProxyPath);
        client.create().forPath(ipProxyPath);
        client.create().forPath(balancePath);

    }

    private String joinNodePath(String... strings) {
        String result = null;
        result = Joiner.on("/").skipNulls().join("", strings);
        return result;
    }

    @After
    public void after() throws IOException {
        if (client != null) {
            client.close();
        }
        if (zkTestServer != null) {
            zkTestServer.close();
        }

    }

    @Test
    public void test_tree_cache() throws Exception {


        final TreeCacheEvent.Type[] saveEventType = new TreeCacheEvent.Type[1];
        final long[] saveTime = new long[1];
        TreeCache treeCache = new TreeCache(client, joinNodePath(namespace));
        treeCache.start();
        treeCache.getListenable().addListener(new TreeCacheListener() {

            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                logger.info("event type={}", event.getType());
                switch (event.getType()) {
                    case NODE_ADDED:
                        saveEventType[0] = TreeCacheEvent.Type.NODE_ADDED;
                        saveTime[0] = System.currentTimeMillis();
                        logger.info("child[path={}, date={}] added", event.getData().getPath(),
                                new String(event.getData().getData()));
                        break;
                    case NODE_UPDATED:
                        saveEventType[0] = TreeCacheEvent.Type.NODE_UPDATED;
                        saveTime[0] = System.currentTimeMillis();
                        logger.info("child[path={}, date={}] updated", event.getData().getPath(),
                                new String(event.getData().getData()));
                        break;
                    case NODE_REMOVED:
                        saveEventType[0] = TreeCacheEvent.Type.NODE_REMOVED;
                        saveTime[0] = System.currentTimeMillis();
                        logger.info("child[path={}, date={}] updated", event.getData().getPath(),
                                new String(event.getData().getData()));
                        break;
                    case CONNECTION_SUSPENDED:
                        break;
                    case CONNECTION_RECONNECTED:
                        break;
                    case CONNECTION_LOST:
                        break;
                    case INITIALIZED:
                        break;
                }
            }
        });
        String hostPath = groupPath + "/localhost:8001";
        client.create().forPath(hostPath);
        long wtStart = System.currentTimeMillis();
        Thread.sleep(300);
        // use 15 ms
        // System.out.println("listener wait time="+(saveTime[0] - wtStart));
        Assert.assertEquals(TreeCacheEvent.Type.NODE_ADDED, saveEventType[0]);
        // System.out.println(new String(zk.getData().forPath(hostPath)));

        // create three node
        String threePath = groupPath + "/hosts/localhost:8001";
        client.create().creatingParentsIfNeeded().forPath(threePath);
        client.setData().forPath(threePath, "{tree}".getBytes());
        // test update
        client.setData().forPath(hostPath, "{}".getBytes());
        Thread.sleep(300);
        Assert.assertEquals(TreeCacheEvent.Type.NODE_UPDATED, saveEventType[0]);

        // test set parent node's data
        client.setData().forPath("/group-1", "{grou-data}".getBytes());
        Thread.sleep(300);
        Assert.assertEquals(TreeCacheEvent.Type.NODE_UPDATED, saveEventType[0]);
        while (true) {
            Thread.sleep(3000);
            System.out.println("tree cache sleep 3s");
        }
    }
}

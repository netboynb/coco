package com.ms.coco.registry.common;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.coco.registry.common.RegisterHolder;

import junit.framework.Assert;

/**
 * 
 * TODO: curator test func
 * 
 * @author wanglin
 */
public class CuratorTest {

    private static final Logger logger = LoggerFactory.getLogger(CuratorTest.class);

    TestingServer zkServer;
    CuratorFramework curator;

    @Before
    public void before() throws Exception {
        zkServer = new TestingServer(2182);

        zkServer.start();
        String zkUrl = zkServer.getConnectString();
        curator = RegisterHolder.getClient(zkUrl);
    }

    @After
    public void after() throws IOException {
        if (curator != null) {
            curator.close();
        }
        if (zkServer != null) {
            zkServer.close();
        }

    }

    @Test
    public void test_namespace() throws Exception {
        CuratorFramework zk = curator.usingNamespace("namespace-test");
        zk.create().forPath("/group-1");

        Stat stat = zk.checkExists().forPath("/group-1");
        Assert.assertNotNull(stat);

        boolean pathCreated = zk.getZookeeperClient().getZooKeeper().exists("/namespace-test/group-1", false) != null;
        Assert.assertTrue(pathCreated);
    }

    @Test
    public void test_path_cache() throws Exception {
        CuratorFramework zk = curator.usingNamespace("namespace-test");
        String groupPath = "/group-1";
        String s = zk.create().forPath(groupPath);
        Assert.assertEquals(groupPath, s);
        Stat stat = zk.checkExists().forPath("/group-1");
        Assert.assertNotNull(stat);
        stat = zk.checkExists().forPath("/namespace-test/group-1");
        Assert.assertNull(stat);

        final PathChildrenCacheEvent.Type[] saveEventType = new PathChildrenCacheEvent.Type[1];
        final long[] saveTime = new long[1];
        PathChildrenCache pcc = new PathChildrenCache(zk, groupPath, true);
        pcc.start();
        pcc.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                logger.info("event type={}", event.getType());
                switch (event.getType()) {
                    case CHILD_ADDED:
                        saveEventType[0] = PathChildrenCacheEvent.Type.CHILD_ADDED;
                        saveTime[0] = System.currentTimeMillis();
                        logger.info("child[path={}, date={}] added", event.getData().getPath(), new String(event.getData().getData()));
                        break;
                    case CHILD_UPDATED:
                        saveEventType[0] = PathChildrenCacheEvent.Type.CHILD_UPDATED;
                        saveTime[0] = System.currentTimeMillis();
                        logger.info("child[path={}, date={}] updated", event.getData().getPath(), new String(event.getData().getData()));
                        break;
                }
            }
        });

        String hostPath = groupPath + "/localhost:8001";
        zk.create().forPath(hostPath);
        long wtStart = System.currentTimeMillis();
        Thread.sleep(30);
        // use 15 ms
        // System.out.println("listener wait time="+(saveTime[0] - wtStart));
        Assert.assertEquals(PathChildrenCacheEvent.Type.CHILD_ADDED, saveEventType[0]);
        // System.out.println(new String(zk.getData().forPath(hostPath)));

        // create three node
        String threePath = groupPath + "/hosts/localhost:8001";
        zk.create().creatingParentsIfNeeded().forPath(threePath);
        zk.setData().forPath(threePath, "{tree}".getBytes());
        // test update
        zk.setData().forPath(hostPath, "{}".getBytes());
        Thread.sleep(30);
        Assert.assertEquals(PathChildrenCacheEvent.Type.CHILD_UPDATED, saveEventType[0]);

        // test set parent node's data
        zk.setData().forPath(groupPath, "{grou-data}".getBytes());
        Thread.sleep(30);
        Assert.assertEquals(PathChildrenCacheEvent.Type.CHILD_UPDATED, saveEventType[0]);
    }

    @Test
    public void test_zkNode() throws Exception {
        CuratorFramework zk = curator.usingNamespace("namespace-test");
        String groupPath = "/group-1/localhost:9001";
        String s = zk.create().creatingParentsIfNeeded().forPath(groupPath);
        Assert.assertEquals(s, "/group-1/localhost:9001");
        NodeCache nodeCache = new NodeCache(zk, "/group-1", true);
        nodeCache.start();
        nodeCache.getListenable().addListener(new NodeCacheListener() {

            @Override
            public void nodeChanged() throws Exception {
                logger.info("node cache change");
            }
        });
        zk.setData().forPath("/group-1", "test-0".getBytes());
        zk.setData().forPath("/group-1", "test-2".getBytes());
    }

    @Test
    public void test_use_2_times_namespace() throws Exception {
        CuratorFramework zk = curator.usingNamespace("namespace-test").usingNamespace("ns2-test");
        String serverPath = "/server";
        zk.create().forPath(serverPath);
        Assert.assertNotNull(zk.checkExists().forPath(serverPath));
        Stat exists = zk.getZookeeperClient().getZooKeeper().exists("/ns2-test" + serverPath, false);
        Assert.assertNotNull(exists);
    }


    @Test
    public void test_tree_cache() throws Exception {
        String rootPath = "/namespace-test";
        CuratorFramework zk = curator.usingNamespace("namespace-test");
        String groupPath = "/group-1";
        String s = zk.create().forPath(groupPath,"group-1-data".getBytes());
        Assert.assertEquals(groupPath, s);
        Stat stat = zk.checkExists().forPath("/group-1");
        Assert.assertNotNull(stat);
        stat = zk.checkExists().forPath(rootPath);
        Assert.assertNull(stat);

        final TreeCacheEvent.Type[] saveEventType = new TreeCacheEvent.Type[1];
        final long[] saveTime = new long[1];
        TreeCache treeCache = new TreeCache(curator, rootPath);
        treeCache.start();
        ChildData childData = treeCache.getCurrentData("/namespace-test/group-1");

        treeCache.getListenable().addListener(new TreeCacheListener() {

            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                logger.info("event type={}", event.getType());
                switch (event.getType()) {
                    case NODE_ADDED:
                        saveEventType[0] = TreeCacheEvent.Type.NODE_ADDED;
                        saveTime[0] = System.currentTimeMillis();
                        logger.info("child[path={}, date={}] added", event.getData().getPath(), new String(event.getData().getData()));
                        break;
                    case NODE_UPDATED:
                        saveEventType[0] = TreeCacheEvent.Type.NODE_UPDATED;
                        saveTime[0] = System.currentTimeMillis();
                        logger.info("child[path={}, date={}] updated", event.getData().getPath(), new String(event.getData().getData()));
                        break;
                    case NODE_REMOVED:
                        saveEventType[0] = TreeCacheEvent.Type.NODE_REMOVED;
                        saveTime[0] = System.currentTimeMillis();
                        logger.info("child[path={}, date={}] updated", event.getData().getPath(), new String(event.getData().getData()));
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
        zk.create().forPath(hostPath);
        long wtStart = System.currentTimeMillis();
        Thread.sleep(300);
        // use 15 ms
        // System.out.println("listener wait time="+(saveTime[0] - wtStart));
        Assert.assertEquals(TreeCacheEvent.Type.NODE_ADDED, saveEventType[0]);
        // System.out.println(new String(zk.getData().forPath(hostPath)));

        // create three node
        String threePath = groupPath + "/hosts/localhost:8001";
        zk.create().creatingParentsIfNeeded().forPath(threePath);
        zk.setData().forPath(threePath, "{tree}".getBytes());
        // test update
        zk.setData().forPath(hostPath, "{}".getBytes());
        Thread.sleep(300);
        Assert.assertEquals(TreeCacheEvent.Type.NODE_UPDATED, saveEventType[0]);

        // test set parent node's data
        zk.setData().forPath("/group-1", "{grou-data}".getBytes());
        Thread.sleep(300);
        Assert.assertEquals(TreeCacheEvent.Type.NODE_UPDATED, saveEventType[0]);
    }
}

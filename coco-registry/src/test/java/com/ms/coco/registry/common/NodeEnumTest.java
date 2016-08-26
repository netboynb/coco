package com.ms.coco.registry.common;

import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.ms.coco.registry.common.CocoUtils;
import com.ms.coco.registry.common.NodeEnum;
import com.ms.coco.registry.model.GroupProxy;

/**
* @author wanglin/netboy
* @version 创建时间：2016年8月10日 下午1:41:20
* @func 
*/
public class NodeEnumTest {

    @Test
    public void test() {
        System.out.println(NodeEnum.SERVICE_NODE);
        NodeEnum nodeName = NodeEnum.getEnum("group_node");
        // nodeName = NodeEnum.valueOf("service_nodes");
        switch (nodeName) {
            case SERVICE_NODE:
                System.out.println(NodeEnum.SERVICE_NODE);
                break;
            case GROUP_NODE:
                System.out.println(NodeEnum.GROUP_NODE);
                break;
            default:
                break;
        }
        String namespace = "namespace";
        String pathname = NodeEnum.GROUP_NODE.toString();
        String nodePath = CocoUtils.buildPath(namespace, pathname);
        System.out.println(nodePath);
        for (int i = 0; i < 100; i++) {
            System.out.println(RandomUtils.nextInt(10));
        }
        String teString = new String("".getBytes(), Charsets.UTF_8);
        System.out.println(teString);
    }

    @Test
    public void testGson() {
        GroupProxy groupProxy = new GroupProxy();
        groupProxy.setFromGroup("from").setToGroup("to").setReqRate(23);
        System.out.println(groupProxy.toString());
    }

    @Test
    public void testMap() {
        String key = "key";
        Map<String, String> map = Maps.newConcurrentMap();
        String result = map.putIfAbsent("key", key);
        result = map.get("key");
        System.out.println(result);
    }
}

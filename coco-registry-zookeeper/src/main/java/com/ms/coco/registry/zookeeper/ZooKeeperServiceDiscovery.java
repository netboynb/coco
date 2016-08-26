package com.ms.coco.registry.zookeeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.coco.exception.RpcErrorMsgConstant;
import com.ms.coco.exception.RpcFrameworkException;
import com.ms.coco.registry.inteface.ServiceDiscovery;
import com.ms.coco.util.CollectionUtil;

/**
 * 基于 ZooKeeper 的服务发现接口实现
 *
 * @author wanglin/netboy
 * @since 1.0.0
 */
public class ZooKeeperServiceDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);

    private String zkAddress;
    private Map<String, List<String>> addressMap = new ConcurrentHashMap<>();

    public ZooKeeperServiceDiscovery(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public synchronized void start(String name) {
        List<String> list = addressMap.get(name);
        if (null != list) {
            return;
        }
        list = new ArrayList<>();
        // 创建 ZooKeeper 客户端
        ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.info("connect zookeeper={}", zkAddress);
        // 获取 service 节点
        String servicePath = Constant.ZK_REGISTRY_PATH + "/" + name;
        if (!zkClient.exists(servicePath)) {
            throw new RpcFrameworkException(RpcErrorMsgConstant.FRAMEWORK_REGISTER_ERROR,
                    String.format("can not find any service node on path: %s", servicePath));
        }
        List<String> addressList = zkClient.getChildren(servicePath);
        if (CollectionUtil.isEmpty(addressList)) {
            throw new RpcFrameworkException(RpcErrorMsgConstant.FRAMEWORK_REGISTER_ERROR,
                    String.format("can not find any address node on path: %s", servicePath));
        }
        for (String path : addressList) {
            String addressPath = servicePath + "/" + path;
            String ipPort = zkClient.readData(addressPath);
            list.add(ipPort);
        }
        addressMap.put(name, list);
    }

    @Override
    public String discover(String name) {
        List<String> addressList = addressMap.get(name);
        if (null == addressList) {
            start(name);
            addressList = addressMap.get(name);
        }

        // 获取 address 节点
        String address;
        int size = addressList.size();
        if (size == 1) {
            // 若只有一个地址，则获取该地址
            address = addressList.get(0);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("get only address node: {}", address);
            }
        } else {
            // 若存在多个地址，则随机获取一个地址
            address = addressList.get(ThreadLocalRandom.current().nextInt(size));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("get random address node: {}", address);
            }

        }
        return address;
    }
}

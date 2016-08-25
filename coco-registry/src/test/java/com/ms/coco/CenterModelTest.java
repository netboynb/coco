package com.ms.coco;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.junit.Before;
import org.junit.Test;

import com.ms.coco.balance.BaseBalance;
import com.ms.coco.common.RegisterHolder;
import com.ms.coco.model.GroupProxy;
import com.ms.coco.model.IpProxy;
import com.ms.coco.model.ServerNode;
import com.ms.coco.model.ServiceConf;
import com.ms.coco.utils.RegisterClient;

/**
* @author wanglin/netboy
* @version 创建时间：2016年8月15日 下午8:23:47
* @func 
*/
public class CenterModelTest {
    TestingServer zkServer;
    CuratorFramework curator;
    String zkUrl;
    String namespace;

    @Before
    public void before() throws Exception {
        zkServer = new TestingServer(2182);
        zkServer.start();
        zkUrl = zkServer.getConnectString();
        curator = RegisterHolder.getClient(zkUrl);
        namespace = "api-service";
    }

    @Test
    public void testStart() {
        ServiceConf serviceConf = new ServiceConf();
        ServerNode serverNode = new ServerNode("127.0.0.1", 8001);
        serviceConf.setServiceName(namespace).setGroupName("test_group").setHeartCenterName("heart-center")
                .setRegisterUrl(zkUrl).setServerNode(serverNode);
        CenterModel centerModel = new CenterModel(serviceConf.getServiceName(), serviceConf.getRegisterUrl());
        try {
            centerModel.init();
            centerModel.start();
            Thread.sleep(5000);
            RegisterClient.registerService(serviceConf);
            for (int i = 0; i < 1000; i++) {
                Map<String, List<ServerNode>> allReaders = centerModel.getAllReaders();
                Map<String, List<ServerNode>> availableReaders = centerModel.getAvailableReaders();
                BaseBalance baseBalance = centerModel.getBalance();
                List<GroupProxy> groupProxies = centerModel.getGroupProxy();
                List<IpProxy> ipProxies = centerModel.getIpProxy();
                System.out.println("allReaders = " + allReaders.toString());
                System.out.println("availableReaders = " + availableReaders.toString());
                System.out.println("baseBalance = " + baseBalance.toString());
                System.out.println("groupProxies = " + groupProxies);
                System.out.println("ipProxies = " + ipProxies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                centerModel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

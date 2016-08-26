package com.ms.coco.registry;

import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.ms.coco.registry.balance.BaseBalance;
import com.ms.coco.registry.common.RegisterHolder;
import com.ms.coco.registry.model.GroupProxy;
import com.ms.coco.registry.model.IpProxy;
import com.ms.coco.registry.model.ServerNode;
import com.ms.coco.registry.model.ServiceConf;

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
        CenterModel centerModel = new CenterModel(serviceConf);
        try {
            centerModel.init();
            centerModel.start();
            Thread.sleep(5000);
            while (true) {
                Gson gson = new Gson();
                Map<String, List<ServerNode>> allReaders = centerModel.getAllReaders();
                Map<String, List<ServerNode>> availableReaders = centerModel.getAvailableReaders();
                BaseBalance baseBalance = centerModel.getBalance();
                List<GroupProxy> groupProxies = centerModel.getGroupProxy();
                List<IpProxy> ipProxies = centerModel.getIpProxy();
                System.out.println("#########################################");
                System.out.println("");
                System.out.println("allReaders = " + gson.toJson(allReaders));
                System.out.println("availableReaders = " + gson.toJson(availableReaders));
                System.out.println("proxyStatus = " + centerModel.proxyStatus());
                System.out.println("baseBalance = " + gson.toJson(baseBalance));
                System.out.println("groupProxies = " + gson.toJson(groupProxies));
                System.out.println("ipProxies = " + gson.toJson(ipProxies));
                System.out.println("");
                System.out.println("#########################################");
                System.out.println("");
                System.out.println("sleep 5s");
                Thread.sleep(5000);
                System.out.println("");
                System.out.println("#########################################");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                centerModel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

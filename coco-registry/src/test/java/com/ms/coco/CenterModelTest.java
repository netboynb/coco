package com.ms.coco;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.junit.Before;
import org.junit.Test;

import com.ms.coco.common.RegisterHolder;
import com.ms.coco.model.ServerNode;
import com.ms.coco.model.ServiceConf;

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

package com.ms.coco.utils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.ms.coco.common.CocoUtils;
import com.ms.coco.common.NodeEnum;
import com.ms.coco.common.RegisterHolder;
import com.ms.coco.http.HttpGetHolder;
import com.ms.coco.model.ServiceConf;
import com.ms.coco.simple.SimpleModeCenter;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年08月15日 下午7:50:46
 * @func
 */
public class RegisterClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterClient.class);

    public static void registerService(ServiceConf serviceConf) throws Exception {
        CuratorFramework client = RegisterHolder.getClient(serviceConf.getRegisterUrl());
        String groupPath = CocoUtils.buildPath(serviceConf.getServiceName(), NodeEnum.GROUP_NODE.value(),
                serviceConf.getGroupName());
        String serverPath = groupPath + "/" + serviceConf.getServerNode().hostKey();
        String serverJson = CocoUtils.GSON.toJson(serviceConf.getServerNode());
        LOGGER.info("start to [registe] to path [{}]", serverPath);
        CocoUtils.createOrUpdateNode(client, serverPath, serverJson);
        LOGGER.info("end to [registe] to path [{}]", serverPath);
        // start to ping heart center
        startHeartClient(serviceConf);
    }

    /**
     * 
     * TODO: build httpclient loop to sent ping info to heart center cluster
     *
     * @param serviceConf
     */
    private static void startHeartClient(ServiceConf serviceConf) {
        String heartCenterName = serviceConf.getHeartCenterName();
        String zkurl = serviceConf.getRegisterUrl();
        if (heartCenterName == null || zkurl == null) {
            LOGGER.error("heartCenterName || zkurl can not be null,heartCenterName={},zkurl={},check them",
                    heartCenterName, zkurl);
            return;
        }
        SimpleModeCenter simpleModeCenter = new SimpleModeCenter(heartCenterName, zkurl);
        simpleModeCenter.start();
        long pingSleep = serviceConf.getPingSleepTimeInMillisecond();
        Thread clientThread = new Thread(new Runnable() {
            public void run() {
                CloseableHttpClient client = HttpClients.createDefault();
                long count = 0L;
                while (true) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(pingSleep);
                    } catch (InterruptedException e) {
                        continue;
                    }
                    List<String> list = Lists.newArrayList(simpleModeCenter.getItemSet());
                    if (list.size() < 1) {
                        continue;
                    }
                    int num = (int) Math.abs(count % list.size());
                    String key = list.get(num);
                    count++;
                    CloseableHttpResponse httpResponse = null;
                    try {
                        httpResponse = client.execute(HttpGetHolder.getClient(key, serviceConf));
                        boolean result = httpResponse.getStatusLine().getStatusCode() == 200 ? true : false;
                        LOGGER.info("[{}] ping ={}", count, result);
                    } catch (Exception e) {
                        LOGGER.error("http client error,info={}", e.toString());
                        continue;
                    } finally {
                        if (httpResponse != null) {
                            try {
                                httpResponse.close();
                            } catch (IOException e) {
                                LOGGER.error("httpResponse close error,info={}", e.toString());
                            }
                        }
                    }
                }
            }
        });
        clientThread.setDaemon(true);
        clientThread.setName("heart-center-client");
        clientThread.start();
    }
}

package com.ms.coco.registry.http;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coco.utils.Consts;
import com.coco.utils.web.Proto;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.ms.coco.registry.model.ServiceConf;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月15日 下午7:31:11
 * @func
 */
public class HttpGetHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpGetHolder.class);
    private volatile static Map<String, HttpGet> httpMap;
    private volatile static HttpGetHolder httpGetHolder;
    private static ReentrantLock lock;

    private static final Gson gson = new Gson();

    private HttpGetHolder() {}

    public static HttpGet getClient(String key, ServiceConf serviceConf) {
        if (httpGetHolder == null) {
            synchronized (HttpGetHolder.class) {
                if (httpGetHolder == null) {
                    httpGetHolder = new HttpGetHolder();
                    httpMap = Maps.newHashMap();
                    lock = new ReentrantLock();
                }
            }
        }
        HttpGet client = null;
        lock.lock();
        try {
            String tempUrl = key.trim();
            if (!httpMap.containsKey(tempUrl)) {
                String[] array = tempUrl.split(":");
                if (array.length < 2) {
                    return null;
                }
                String ip = array[0];
                Integer port = Integer.valueOf(array[1]);
                Map<String, String> data = Maps.newHashMap();
                data.put(Consts.zkurl, serviceConf.getRegisterUrl());
                data.put(Consts.serviceName, serviceConf.getServiceName());
                data.put(Consts.groupName, serviceConf.getGroupName());
                data.put(Consts.hostKey, serviceConf.getServerNode().hostKey());
                String livetime = serviceConf.getRedisKeyLiveTimeInMillisecond();
                if (livetime != null) {
                    data.put(Consts.liveTimeInMillisecond, livetime);
                }

                Proto proto = new Proto(0, null, data);
                URIBuilder builder = new URIBuilder();
                builder.setScheme("http").setHost(ip).setPort(port).setPath(Consts.heartPath)
                        .setParameter(Consts.payload, gson.toJson(proto));
                RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000)
                        .setConnectionRequestTimeout(1000).setSocketTimeout(1000).build();
                HttpGet request = new HttpGet(builder.build());
                request.setConfig(requestConfig);
                httpMap.put(tempUrl, request);
                client = request;
            } else {
                client = httpMap.get(tempUrl);
            }
        } catch (Exception e) {
            LOGGER.error("HttpGetHolder build httpGet error,info={}", e);
        } finally {
            lock.unlock();
        }
        return client;
    }
}

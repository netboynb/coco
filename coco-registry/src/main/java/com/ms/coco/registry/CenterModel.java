package com.ms.coco.registry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;

import com.google.common.collect.Lists;
import com.ms.coco.registry.balance.BaseBalance;
import com.ms.coco.registry.common.CocoUtils;
import com.ms.coco.registry.common.NodeEnum;
import com.ms.coco.registry.common.RegisterHolder;
import com.ms.coco.registry.entry.impl.BalanceEntry;
import com.ms.coco.registry.entry.impl.GroupEntry;
import com.ms.coco.registry.entry.impl.ProxyEntry;
import com.ms.coco.registry.model.GroupProxy;
import com.ms.coco.registry.model.IpProxy;
import com.ms.coco.registry.model.ServerNode;
import com.ms.coco.registry.model.ServiceConf;
import com.ms.coco.registry.refresher.BalanceRefresher;
import com.ms.coco.registry.refresher.GroupRefresher;
import com.ms.coco.registry.refresher.ProxyRefresher;
import com.ms.coco.registry.refresher.Refresher;
import com.ms.coco.registry.utils.RegisterClient;
import com.ms.coco.registry.utils.ThreadPoolHolder;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月15日 下午7:26:26
 * @func
 */
public class CenterModel implements CenterService {
    private List<Refresher> refresherList = Lists.newArrayList();
    private GroupEntry groupEntry;
    private BalanceEntry balanceEntry;
    private ProxyEntry proxyEntry;
    private ServiceConf serviceConf;

    public CenterModel(ServiceConf serviceConf) {
        this.serviceConf = serviceConf;
    }

    @Override
    public void init() {
        String namespace = serviceConf.getServiceName();
        String registerUrl = serviceConf.getRegisterUrl();
        Integer min = serviceConf.getMinPoolSize();
        Integer max = serviceConf.getMaxPoolSize();
        if (StringUtils.isBlank(namespace) || StringUtils.isBlank(registerUrl)) {
            throw new RuntimeException("namespace && registerUrl can't be null,please check them");
        } else {
            createParentPath(namespace, registerUrl);
            CuratorFramework client = RegisterHolder.getClient(registerUrl.trim());
            // create parent node first ,then create
            ExecutorService executorService = ThreadPoolHolder.getFixedPool("center-refresher", min, max);
            groupEntry = new GroupEntry();
            balanceEntry = new BalanceEntry();
            proxyEntry = new ProxyEntry();

            Refresher groupRefresher = new GroupRefresher(namespace, client, executorService, groupEntry);
            Refresher balanceRefresher = new BalanceRefresher(namespace, client, executorService, balanceEntry);
            Refresher proxyRefresher = new ProxyRefresher(namespace, client, executorService, proxyEntry);
            refresherList.add(groupRefresher);
            refresherList.add(balanceRefresher);
            refresherList.add(proxyRefresher);
        }
    }

    @Override
    public void start() throws Exception {
        for (Refresher refresher : refresherList) {
            refresher.start();
        }
        // register server node
        RegisterClient.registerService(serviceConf);
    }

    @Override
    public void close() throws Exception {
        for (Refresher refresher : refresherList) {
            refresher.close();
        }
        RegisterClient.deleteService(serviceConf);

    }

    /**
     * 
     * TODO: before start ,we will create /namespace/service_node /namespace/group_node
     * /namespace/proxy_node /namespace/balance_node /namespace/client_node
     *
     */
    private void createParentPath(String namespace, String registerUrl) {
        CuratorFramework client = RegisterHolder.getClient(registerUrl);
        try {
            CocoUtils.createNode(client, CocoUtils.buildPath(namespace, NodeEnum.SERVICE_NODE.value()));
            CocoUtils.createNode(client, CocoUtils.buildPath(namespace, NodeEnum.GROUP_NODE.value()));
            CocoUtils.createNode(client, CocoUtils.buildPath(namespace, NodeEnum.PROXY_NODE.value()));
            CocoUtils.createNode(client, CocoUtils.buildPath(namespace, NodeEnum.BALANCE_NODE.value()));
            CocoUtils.createNode(client, CocoUtils.buildPath(namespace, NodeEnum.CLIENT_NODE.value()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Map<String, List<ServerNode>> getAllReaders() {
        return groupEntry.getAllReaders();
    }

    @Override
    public Map<String, List<ServerNode>> getAvailableReaders() {
        return groupEntry.getAvailableReaders();
    }

    @Override
    public BaseBalance getBalance() {
        return balanceEntry.getBalance();
    }

    @Override
    public List<GroupProxy> getGroupProxy() {
        return proxyEntry.getGroupProxy();
    }

    @Override
    public List<IpProxy> getIpProxy() {
        return proxyEntry.getIpProxy();
    }

    @Override
    public boolean proxyStatus() {
        return proxyEntry.proxyStatus();
    }
}

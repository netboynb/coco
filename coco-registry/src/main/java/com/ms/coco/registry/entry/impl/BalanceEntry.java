package com.ms.coco.registry.entry.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.ms.coco.registry.balance.BaseBalance;
import com.ms.coco.registry.balance.DefaultBalance;
import com.ms.coco.registry.common.CocoEnum;
import com.ms.coco.registry.entry.BalanceService;
import com.ms.coco.registry.entry.NotifyService;
import com.ms.coco.registry.model.Node;

/**
* @author wanglin/netboy
* @version 创建时间：2016年8月10日 下午8:17:57
* @func 
*/
public class BalanceEntry implements BalanceService, NotifyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceEntry.class);
    private Map<String, BaseBalance> instanceMap = Maps.newConcurrentMap();
    private BaseBalance balance;

    public BalanceEntry() {
        balance = new DefaultBalance();
        instanceMap.put(CocoEnum.SLB_DEFAULT.value(), balance);
    }

    @Override
    public BaseBalance getBalance() {
        return balance;
    }

    @Override
    public void addEvent(Node node) {
        String data = node.getData();
        BaseBalance newBalance = balance;
        if (!StringUtils.isBlank(data) && !data.equals("{}")) {
            String balanceName = data.trim();
            newBalance = instanceMap.get(balanceName);
            if (null == newBalance) {
                try {
                    newBalance = (BaseBalance) Class.forName(balanceName).newInstance();
                    instanceMap.put(balanceName, newBalance);
                    // switch the new instance
                    balance = newBalance;
                    LOGGER.info("balance class [{}] instance success", balanceName);
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    LOGGER.error("balance class [{}] can't be instance,info={}", balanceName, e.toString());
                }
            } else {
                balance = newBalance;
            }
        }
    }

    @Override
    public void updateEvent(Node node) {
        addEvent(node);
    }

    @Override
    public void removeEvent(Node node) {
        balance = instanceMap.get(CocoEnum.SLB_DEFAULT);
        LOGGER.warn("[Balance-Remove-Event] switch balance to default balance");
    }

    @Override
    public void refresh() {}

}

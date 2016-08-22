package com.ms.coco.balance;

import java.util.List;

import org.apache.commons.lang.math.RandomUtils;

import com.ms.coco.model.GroupProxy;
import com.ms.coco.model.IpProxy;
import com.ms.coco.model.ServerNode;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午9:15:38
 * @func
 */
public class DefaultBalance implements BaseBalance {

    @Override
    public ServerNode select(List<ServerNode> list, GroupProxy groupProxy, IpProxy ipProxy) {
        Integer size = list.size();
        Integer random = RandomUtils.nextInt(size);
        return list.get(random);
    }

}

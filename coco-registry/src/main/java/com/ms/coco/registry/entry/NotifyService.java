package com.ms.coco.registry.entry;

import com.ms.coco.registry.model.Node;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午7:29:19
 * @func
 */
public interface NotifyService {

    void addEvent(Node node);

    void updateEvent(Node node);

    void removeEvent(Node node);

    void refresh();
}

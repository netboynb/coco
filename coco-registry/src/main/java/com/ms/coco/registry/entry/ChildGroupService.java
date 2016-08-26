package com.ms.coco.registry.entry;

import java.util.List;

import com.ms.coco.registry.model.ServerNode;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午6:30:54
 * @func
 */
public interface ChildGroupService {
    List<ServerNode> getAllReaders();

    List<ServerNode> getAvailableReaders();

    boolean groupStatus();
}

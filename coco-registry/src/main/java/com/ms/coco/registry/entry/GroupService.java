package com.ms.coco.registry.entry;

import java.util.List;
import java.util.Map;

import com.ms.coco.registry.model.ServerNode;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月15日 下午12:45:30
 * @func
 */
public interface GroupService {

    Map<String, List<ServerNode>> getAllReaders();

    Map<String, List<ServerNode>> getAvailableReaders();
}

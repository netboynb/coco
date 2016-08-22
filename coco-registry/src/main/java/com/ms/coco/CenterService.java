package com.ms.coco;

import java.io.IOException;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月15日 下午7:32:07
 * @func
 */
public interface CenterService {
    void init();

    void start() throws Exception;

    void close() throws IOException;
}

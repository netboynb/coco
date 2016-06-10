package com.ms.coco;

import org.restexpress.RestExpress;
import org.restexpress.util.DefaultShutdownHook;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年6月10日 下午4:38:28
 * @func
 */
public class CocoRestExpress extends RestExpress {

    public CocoRestExpress() {
        super();
    }

    @Override
    public void awaitShutdown() {
        Runtime.getRuntime().addShutdownHook(new DefaultShutdownHook(this));
    }
}

package com.ms.coco.route;

import org.restexpress.RestExpress;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年6月8日 下午7:13:25
 * @func
 */
public interface CocoRoute {
    /**
     * 
     * TODO: 将url 与 controller 对应起来
     *
     * @param server
     */
    void defineRoutes(RestExpress server);

    void init();
}

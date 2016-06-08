package com.ms.coco.controller;

import org.restexpress.Request;
import org.restexpress.Response;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年6月8日 下午7:20:22
 * @func
 */
public abstract class CocoController<T> {

    public abstract T create(Request request, Response response);

    public abstract T read(Request request, Response response);

    public abstract T update(Request request, Response response);

    public abstract T delete(Request request, Response response);

}

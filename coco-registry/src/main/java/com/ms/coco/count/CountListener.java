package com.ms.coco.count;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月11日 下午11:02:17
 * @func
 */
public interface CountListener {
	/**
	 * 重试次数 达到预期时 回调
	 * 
	 * @param hostKey
	 */
	public void callBack(String hostKey);
}

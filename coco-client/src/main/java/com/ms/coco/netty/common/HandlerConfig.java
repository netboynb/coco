
package com.ms.coco.netty.common;

/**
 * 
 * TODO: DOCUMENT ME!
 * 
 * @author netboy
 */
public class HandlerConfig {

	public static final HandlerConfig DEFAULT_CONFIG = new HandlerConfig(ResponsePromiseContainer.GLOBAL_CONTAINER);

	private ResponsePromiseContainer responsePromiseContainer;
    private int maxFrameLengthMB = 20;

	public HandlerConfig(ResponsePromiseContainer responsePromiseContainer) {
		this.responsePromiseContainer = responsePromiseContainer;
	}

	public ResponsePromiseContainer getResponsePromiseContainer() {
		return responsePromiseContainer;
	}

	public int getMaxFrameLengthMB() {
		return maxFrameLengthMB;
	}

	public void setMaxFrameLengthMB(int maxFrameLengthMB) {
		this.maxFrameLengthMB = maxFrameLengthMB;
	}
}

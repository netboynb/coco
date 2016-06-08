
package com.ms.coco.netty.common;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.util.internal.PlatformDependent;

/**
 * long max=9223372036854775807
 *      rid=1464937619923000009
 * rid = currentTimeMillis * 1000000 + ridSeed.incrementAndGet()
 * 
 * timeStamp = requestId / (1000000 * 1000) 能够得到秒
 */
public class ResponsePromiseContainer {

	/**
	 * Global_Container
	 */
	public static final ResponsePromiseContainer GLOBAL_CONTAINER = new ResponsePromiseContainer();

	protected AtomicLong ridSeed = new AtomicLong(0);
	protected ConcurrentMap<Long, ResponsePromise> responsePromiseMaps = PlatformDependent.newConcurrentHashMap();

	public ResponsePromise createResponsePromise() {
		long rid = ridSeed.incrementAndGet();
        long timeOffSet = System.currentTimeMillis() * 100000;
        long id = rid + timeOffSet;
        ResponsePromise responsePromise = new ResponsePromise(id);
		do {
            ResponsePromise oldPromise = responsePromiseMaps.putIfAbsent(id, responsePromise);
            if (oldPromise != null) {
                rid = ridSeed.incrementAndGet();
                id = rid + timeOffSet;
			} else {
                responsePromise.setRid(id);
				break;
			}
		} while (true);
		return responsePromise;
	}

	public ResponsePromise removeResponsePromise(long rid) {
		return responsePromiseMaps.remove(rid);
	}
}

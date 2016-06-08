package com.ms.coco.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restexpress.Request;
import org.restexpress.Response;
import org.restexpress.pipeline.MessageObserver;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年6月8日 下午8:41:59
 * @func
 */
public class CocoMessageObserver extends MessageObserver {
    // SECTION: INSTANCE VARIABLES

    private Map<String, Timer> timers = new ConcurrentHashMap<String, Timer>();


    // SECTION: MESSAGE OBSERVER

    @Override
    protected void onReceived(Request request, Response response) {
        timers.put(request.getCorrelationId(), new Timer());
    }

    @Override
    protected void onException(Throwable exception, Request request, Response response) {
        System.out.println(request.getEffectiveHttpMethod().toString() + " " + request.getUrl() + " threw exception: "
                + exception.getClass().getSimpleName());
        exception.printStackTrace();
    }

    @Override
    protected void onSuccess(Request request, Response response) {}

    @Override
    protected void onComplete(Request request, Response response) {
        Timer timer = timers.remove(request.getCorrelationId());
        if (timer != null)
            timer.stop();

        StringBuffer sb = new StringBuffer(request.getEffectiveHttpMethod().toString());
        sb.append(" ");
        sb.append(request.getUrl());

        if (timer != null) {
            sb.append(" responded with ");
            sb.append(response.getResponseStatus().toString());
            sb.append(" in ");
            sb.append(timer.toString());
        } else {
            sb.append(" responded with ");
            sb.append(response.getResponseStatus().toString());
            sb.append(" (no timer found)");
        }

        System.out.println(sb.toString());
    }


    // SECTION: INNER CLASS

    private static class Timer {
        private long startMillis = 0;
        private long stopMillis = 0;

        public Timer() {
            super();
            this.startMillis = System.currentTimeMillis();
        }

        public void stop() {
            this.stopMillis = System.currentTimeMillis();
        }

        public String toString() {
            long stopTime = (stopMillis == 0 ? System.currentTimeMillis() : stopMillis);

            return String.valueOf(stopTime - startMillis) + "ms";
        }
    }
}

package com.bookislife.flow.server.handler.impl;

import com.bookislife.flow.core.Env;
import com.bookislife.flow.server.handler.ResponseTimeHandler;
import io.vertx.rxjava.ext.web.RoutingContext;

/**
 * ResponseTimeHandlerImpl
 *
 * @author SidneyXu
 */
public class ResponseTimeHandlerImpl implements ResponseTimeHandler {
    @Override
    public void handle(RoutingContext context) {
        long start = System.currentTimeMillis();
        context.addHeadersEndHandler(event -> {
            long duration = System.currentTimeMillis() - start;
            context.response().putHeader(Env.Header.RESPONSE_TIME, "" + duration);
        });
        context.next();
    }
}

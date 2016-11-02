package com.bookislife.flow.server.handler.impl;

import com.bookislife.flow.server.handler.StaticResourceHandler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

/**
 * StaticResourceHandlerImpl
 *
 * @author SidneyXu
 */
public class StaticResourceHandlerImpl implements StaticResourceHandler {

    @Override
    public void handle(RoutingContext context) {
        HttpServerRequest request = context.request();
        // TODO: 16/6/2
        // idle
        context.next();
    }
}

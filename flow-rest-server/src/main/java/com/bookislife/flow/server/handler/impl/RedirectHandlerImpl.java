package com.bookislife.flow.server.handler.impl;

import com.bookislife.flow.server.handler.RedirectHandler;
import io.vertx.ext.web.RoutingContext;

/**
 * RedirectHandlerImpl
 *
 * @author SidneyXu
 */
public class RedirectHandlerImpl implements RedirectHandler {
    @Override
    public void handle(RoutingContext context) {
        context.next();
    }
}

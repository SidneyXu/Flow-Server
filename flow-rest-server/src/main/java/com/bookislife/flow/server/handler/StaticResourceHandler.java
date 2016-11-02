package com.bookislife.flow.server.handler;

import com.bookislife.flow.server.handler.impl.StaticResourceHandlerImpl;
import com.google.inject.ImplementedBy;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * StaticResourceHandler
 * <br>
 * Handle static resources.
 *
 * @author SidneyXu
 */
@ImplementedBy(StaticResourceHandlerImpl.class)
public interface StaticResourceHandler extends Handler<RoutingContext> {
}

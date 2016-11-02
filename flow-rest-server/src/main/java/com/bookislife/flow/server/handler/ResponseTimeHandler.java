package com.bookislife.flow.server.handler;

import com.bookislife.flow.server.handler.impl.ResponseTimeHandlerImpl;
import com.google.inject.ImplementedBy;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;

/**
 * ResponseTimeHandler
 * <br>
 * Log duration between the request and the response.
 *
 * @author SidneyXu
 */
@ImplementedBy(ResponseTimeHandlerImpl.class)
public interface ResponseTimeHandler extends Handler<RoutingContext> {
}

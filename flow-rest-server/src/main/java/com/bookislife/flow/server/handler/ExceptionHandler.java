package com.bookislife.flow.server.handler;

import com.bookislife.flow.server.handler.impl.ExceptionHandlerImpl;
import com.google.inject.ImplementedBy;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * ExceptionHandler
 * <br>
 * Handle exception.
 *
 * @author SidneyXu
 */
@ImplementedBy(ExceptionHandlerImpl.class)
public interface ExceptionHandler extends Handler<RoutingContext> {
}

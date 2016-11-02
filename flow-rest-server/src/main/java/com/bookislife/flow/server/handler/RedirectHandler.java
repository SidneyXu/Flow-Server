package com.bookislife.flow.server.handler;

import com.bookislife.flow.server.handler.impl.RedirectHandlerImpl;
import com.google.inject.ImplementedBy;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * RedirectHandler
 * <br>
 * Handle redirect request.
 *
 * @author SidneyXu
 */
@ImplementedBy(RedirectHandlerImpl.class)
public interface RedirectHandler extends Handler<RoutingContext> {
}

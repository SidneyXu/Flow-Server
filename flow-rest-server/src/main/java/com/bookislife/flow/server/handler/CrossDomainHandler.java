package com.bookislife.flow.server.handler;

import com.bookislife.flow.server.handler.impl.CrossDomainHandlerImpl;
import com.google.inject.ImplementedBy;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * CrossDomainHandler
 * <br>
 * Handle cross-domain request.
 *
 * @author SidneyXu
 */
@ImplementedBy(CrossDomainHandlerImpl.class)
public interface CrossDomainHandler extends Handler<RoutingContext> {
}

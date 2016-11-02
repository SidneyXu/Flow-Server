package com.bookislife.flow.server.handler;

import com.bookislife.flow.server.handler.impl.AccessControllerHandlerImpl;
import com.google.inject.ImplementedBy;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;

/**
 * AccessControllerHandler
 * <br>
 * Handle acl.
 *
 * @author SidneyXu
 */
@ImplementedBy(AccessControllerHandlerImpl.class)
public interface AccessControllerHandler extends Handler<RoutingContext> {
}

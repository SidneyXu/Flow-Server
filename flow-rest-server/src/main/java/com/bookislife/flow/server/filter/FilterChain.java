package com.bookislife.flow.server.filter;

import com.bookislife.flow.core.exception.FlowException;
import io.vertx.rxjava.ext.web.RoutingContext;

/**
 * FilterChain
 *
 * @author SidneyXu
 */
public interface FilterChain {
    void doFilter(RoutingContext context) throws FlowException;
}

package com.bookislife.flow.server.filter;

import com.bookislife.flow.core.exception.FlowException;
import io.vertx.rxjava.ext.web.RoutingContext;

/**
 * Filter
 *
 * @author SidneyXu
 */
public interface Filter {

    void init() throws FlowException;

    void doFilter(RoutingContext context, FilterChain chain) throws FlowException;

}

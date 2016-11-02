package com.bookislife.flow.server;

import com.bookislife.flow.server.handler.CrossDomainHandler;
import com.bookislife.flow.server.handler.ExceptionHandler;
import com.bookislife.flow.server.handler.RedirectHandler;
import com.bookislife.flow.server.handler.StaticResourceHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;

import javax.inject.Inject;


/**
 * Middleware
 *
 * @author SidneyXu
 */
public class Middleware {

    @Inject
    private ExceptionHandler exceptionHandler;

    @Inject
    private StaticResourceHandler staticResourceHandler;

    @Inject
    private CrossDomainHandler crossDomainHandler;

    @Inject
    private RedirectHandler redirectHandler;

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public ResponseTimeHandler getResponseTimeHandler() {
        return ResponseTimeHandler.create();
    }

    public StaticResourceHandler getStaticResourceHandler() {
        return staticResourceHandler;
    }

    public CrossDomainHandler getCrossDomainHandler() {
        return crossDomainHandler;
    }

    public RedirectHandler getRedirectHandler() {
        return redirectHandler;
    }

    public CookieHandler getCookieHandler() {
        return CookieHandler.create();
    }

    public BodyHandler getBodyHandler() {
        return BodyHandler.create().setUploadsDirectory("uploads");
    }
}

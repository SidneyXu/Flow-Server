package com.bookislife.flow.server.domain;

import com.bookislife.flow.core.Env;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import java.util.Set;

/**
 * RoutingContextWrapper
 * <br>
 * This class wraps the RoutingContext for using later.
 *
 * @author SidneyXu
 */
public class RoutingContextWrapper {

    private RoutingContext ctx;

    public RoutingContextWrapper(RoutingContext routingContext) {
        this.ctx = routingContext;
    }

    public String getDatabaseName() {
        return ctx.request().getHeader(Env.Header.APPLICATION_ID);
    }

    public String getTableName() {
        return ctx.request().getParam("className");
    }

    public String getObjectId() {
        return ctx.request().getParam("objectId");
    }

    public RoutingContext getContext() {
        return ctx;
    }

    public HttpServerRequest request() {
        return ctx.request();
    }

    public JsonObject getBodyAsJson() {
        return ctx.getBodyAsJson();
    }

    public Set<FileUpload> uploadedFiles() {
        return ctx.fileUploads();
    }

    public FileUpload uploadedFile(String name) {
        return uploadedFile(uploadedFiles(), name);
    }

    public FileUpload uploadedFile(Set<FileUpload> fileUploads, String name) {
        for (FileUpload fileUpload : fileUploads) {
            if (fileUpload.name().equals(name)) {
                return fileUpload;
            }
        }
        return null;
    }

}

package com.example.resources;

import com.bookislife.flow.server.domain.RoutingContextWrapper;
import com.bookislife.flow.server.utils.JacksonJsonBuilder;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/hello")
public class HelloResources {

    @GET
    @Path("getString")
    @Produces(MediaType.TEXT_PLAIN)
    public String getString(RoutingContextWrapper context) {
        return "foobar";
    }

    @GET
    @Path("getJson")
    public Map<String, Object> getJson(RoutingContextWrapper context) {
        Map<String, Object> map = new HashMap<>();
        map.put("x", 1);
        return map;
    }

    @POST
    @Path("postJson")
    @Consumes(MediaType.APPLICATION_JSON)
    public String postJson(RoutingContextWrapper context) {
        JsonObject body = context.getBodyAsJson();
        System.out.println("receive " + body);
        return body.encode();
    }

    @POST
    @Path("postForm")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Object postForm(RoutingContextWrapper context,
                           @FormParam("y") int y,
                           @FormParam("x") String x) {
        MultiMap attributes = context.request().formAttributes();
        System.out.println(attributes.get("x"));
        System.out.println("x= " + x);
        System.out.println("y= " + y);
        return JacksonJsonBuilder.create()
                .put("x", x)
                .put("y", y)
                .build();
    }

    @POST
    @Path("postFormData")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Object postFormData(RoutingContextWrapper context,
                               @FormParam("y") int y,
                               @FormParam("x") String x,
                               @FormParam("attachment") FileUpload fileUpload) {
        MultiMap attributes = context.request().formAttributes();
        System.out.println(fileUpload.fileName());
        System.out.println(attributes.get("x"));
        System.out.println("x= " + x);
        System.out.println("y= " + y);
        return JacksonJsonBuilder.create()
                .put("x", x)
                .put("y", y)
                .put("attachment", fileUpload.uploadedFileName())
                .build();
    }

}

package com.bookislife.flow.server.web;


import com.bookislife.flow.core.utils.Pair;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * ResourceDescriptor
 *
 * @author SidneyXu
 */
public class ResourceDescriptor {

    public final Class<?> clazz;
    public final Method method;
    public final String httpMethod;
    public final String path;
    public final ImmutableList<String> consumeType;
    public final ImmutableList<String> produceType;
    public final ImmutableMap<String, Pair<Integer, Type>> formParams;
    public final ImmutableMap<String, Pair<Integer, Type>> pathParams;
    public final ImmutableMap<String, Pair<Integer, Type>> contextParams;
    public final ImmutableMap<String, Pair<Integer, Type>> queryParams;

    public ResourceDescriptor(Class<?> clazz) {
        this.clazz = clazz;
        this.method = null;
        this.httpMethod = null;
        this.formParams = null;
        this.pathParams = null;
        this.contextParams = null;
        this.queryParams = null;
        if (clazz.isAnnotationPresent(Path.class)) {
            path = clazz.getAnnotation(Path.class).value();
        } else {
            path = null;
        }
        if (clazz.isAnnotationPresent(Consumes.class)) {
            String[] consumes = clazz.getAnnotation(Consumes.class).value();
            consumeType = ImmutableList.copyOf(consumes);
        } else {
            consumeType = null;
        }
        if (clazz.isAnnotationPresent(Produces.class)) {
            String[] produces = clazz.getAnnotation(Produces.class).value();
            produceType = ImmutableList.copyOf(produces);
        } else {
            produceType = null;
        }
    }

    public ResourceDescriptor(Class<?> clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
        if (method.isAnnotationPresent(Path.class)) {
            path = method.getAnnotation(Path.class).value();
        } else {
            path = null;
        }
        if (method.isAnnotationPresent(Consumes.class)) {
            String[] consumes = method.getAnnotation(Consumes.class).value();
            consumeType = ImmutableList.copyOf(consumes);
        } else {
            consumeType = null;
        }
        if (method.isAnnotationPresent(Produces.class)) {
            String[] produces = method.getAnnotation(Produces.class).value();
            produceType = ImmutableList.copyOf(produces);
        } else {
            produceType = null;
        }
        if (method.isAnnotationPresent(HttpMethod.class)) {
            httpMethod = method.getAnnotation(HttpMethod.class).value();
        } else if (method.isAnnotationPresent(GET.class)) {
            httpMethod = HttpMethod.GET;
        } else if (method.isAnnotationPresent(POST.class)) {
            httpMethod = HttpMethod.POST;
        } else if (method.isAnnotationPresent(PUT.class)) {
            httpMethod = HttpMethod.PUT;
        } else if (method.isAnnotationPresent(DELETE.class)) {
            httpMethod = HttpMethod.DELETE;
        } else {
            httpMethod = null;
        }

        Annotation[][] paramTypes = method.getParameterAnnotations();
        if (null != paramTypes && paramTypes.length > 0) {
            Map<String, Pair<Integer, Type>> formParamMap = new HashMap<>();
            Map<String, Pair<Integer, Type>> pathParamMap = new HashMap<>();
            Map<String, Pair<Integer, Type>> contextParamMap = new HashMap<>();
            Map<String, Pair<Integer, Type>> queryParamMap = new HashMap<>();
            for (int i = 0; i < paramTypes.length; i++) {
                Annotation[] annotations = paramTypes[i];
                for (Annotation annotation : annotations) {
                    if (annotation instanceof FormParam) {
                        String paramName = ((FormParam) annotation).value();
                        formParamMap.put(paramName, new Pair<>(i, method.getParameters()[i].getType()));
                    } else if (annotation instanceof PathParam) {
                        String paramName = ((PathParam) annotation).value();
                        pathParamMap.put(paramName, new Pair<>(i, method.getParameters()[i].getType()));
                    } else if (annotation instanceof Context) {
                        String paramName = "context";
                        contextParamMap.put(paramName, new Pair<>(i, method.getParameters()[i].getType()));
                    } else if (annotation instanceof QueryParam) {
                        String paramName = ((QueryParam) annotation).value();
                        queryParamMap.put(paramName, new Pair<>(i, method.getParameters()[i].getType()));
                    }
                }
            }
            this.formParams = ImmutableMap.copyOf(formParamMap);
            this.pathParams = ImmutableMap.copyOf(pathParamMap);
            this.contextParams = ImmutableMap.copyOf(contextParamMap);
            this.queryParams = ImmutableMap.copyOf(queryParamMap);
        } else {
            this.formParams = null;
            this.pathParams = null;
            this.contextParams = null;
            this.queryParams = null;
        }

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResourceDescriptor{");
        sb.append("clazz=").append(clazz);
        sb.append(", method=").append(method);
        sb.append(", httpMethod='").append(httpMethod).append('\'');
        sb.append(", path='").append(path).append('\'');
        sb.append(", consumeType=").append(consumeType);
        sb.append(", produceType=").append(produceType);
        sb.append('}');
        return sb.toString();
    }
}

package com.bookislife.flow.server;

import com.bookislife.flow.core.exception.FlowException;
import com.bookislife.flow.core.utils.Pair;
import com.bookislife.flow.server.domain.RoutingContextWrapper;
import com.bookislife.flow.server.utils.CommonUtil;
import com.bookislife.flow.server.utils.JacksonWriter;
import com.bookislife.flow.server.utils.ResponseCreator;
import com.bookislife.flow.server.utils.Runner;
import com.bookislife.flow.server.web.ResourceDescriptor;
import com.bookislife.flow.server.web.ResourceLoader;
import com.bookislife.flow.server.web.ResourceResolver;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Launcher
 *
 * @author SidneyXu
 */
public class Launcher extends AbstractVerticle {

    public static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    private Injector injector;
    private ServerConfig serverConfig;

    private final ExecutorService executorService = Executors.newWorkStealingPool();

    public static void main(String[] args) {
        Runner.runExample(Launcher.class);
    }

    @Override
    public void start() throws Exception {
        initConfig();

        // init web server
        Router router = Router.router(vertx);
        registerGlobalHandler(router);
        registerResourceHandler(router);

        HttpServerOptions options = new HttpServerOptions();
        vertx.createHttpServer(options)
                .requestHandler(router::accept)
                .listen(serverConfig.port);

        logger.debug("Server is running and listening on " + serverConfig.port);
    }

    private void registerGlobalHandler(Router router) {
        Middleware middleware = injector.getInstance(Middleware.class);

        router.route().failureHandler(middleware.getExceptionHandler());
        router.route().handler(middleware.getResponseTimeHandler());
        router.route().handler(middleware.getCookieHandler());
        router.route().handler(middleware.getBodyHandler());
        router.route().handler(middleware.getRedirectHandler());
        router.route().handler(middleware.getCrossDomainHandler());
        router.route().handler(middleware.getStaticResourceHandler());
    }

    private Route applyRoute(Router router, ResourceDescriptor cd, ResourceDescriptor md) {
        Route route = router.route();
        if (null != cd.path) {
            if (null == md.path) {
                route.path(cd.path.replaceAll("\\{(.*)\\}$", ":$1"));
            } else {
                String path = "/".equals(cd.path) ? md.path + md.path : cd.path + "/" + md.path;
                route.path(path.replaceAll("\\{(.*)\\}$", ":$1"));
            }
            if (serverConfig.baseContext != null) {
                String contextPath = CommonUtil.addPrefix(serverConfig.baseContext, "/");
                route.path(contextPath + route.getPath());
            }
        }
        if (null != md.httpMethod) {
            route.method(HttpMethod.valueOf(md.httpMethod));
        }
        if (null != md.consumeType) {
            md.consumeType.forEach(route::consumes);
        } else if (null != cd.consumeType) {
            cd.consumeType.forEach(route::consumes);
        }
        if (null != md.produceType) {
            md.produceType.forEach(route::produces);
        } else if (null != cd.produceType) {
            cd.produceType.forEach(route::produces);
        }
        return route;
    }

    private void registerResourceHandler(Router rootRouter) {
        ResourceLoader resourceLoader = new ResourceLoader(Launcher.class.getClassLoader());
        Set<Class<?>> classSet = resourceLoader.scanPackage(serverConfig.resourcePath);


        classSet.stream()
                .map(ResourceResolver::resolveResource)
                .forEach(resource -> {
                    ResourceDescriptor clazzDescriptor = resource.getClassDescriptor();
                    resource.getMethodDescriptorList().forEach(methodDescriptor -> {
                        Object singleton = injector.getInstance(resource.clazz);

                        Route route = applyRoute(rootRouter, clazzDescriptor, methodDescriptor);

                        // TODO: 2016/9/22
                        route.handler(ctx -> CompletableFuture.runAsync(() -> {
                            Method method = methodDescriptor.method;

                            // consume or product
                            ImmutableList<String> produceTypes = methodDescriptor.produceType;
                            ImmutableList<String> consumeTypes = methodDescriptor.consumeType;
                            String defaultProduceType = MediaType.APPLICATION_JSON;
                            String defaultConsumeType = null;
                            if (produceTypes != null && produceTypes.size() > 0) {
                                defaultProduceType = produceTypes.get(0);
                            }
                            if (consumeTypes != null && consumeTypes.size() > 0) {
                                defaultConsumeType = consumeTypes.get(0);
                            }

                            // TODO: 5/19/16 add interceptor
                            try {
                                assert method != null;
                                RoutingContextWrapper wrapper = new RoutingContextWrapper(ctx);
                                Object result = invokeMethod(
                                        wrapper,
                                        singleton,
                                        method,
                                        defaultConsumeType,
                                        methodDescriptor);
                                if (result != null) {
                                    processResponse(ctx, defaultProduceType, result);
                                } else {
                                    ctx.response()
                                            .setStatusCode(200)
                                            .end();
                                }
                            } catch (IllegalAccessException e) {
                                logger.error("error occurs when invoking methods", e);
                                e.printStackTrace();
                            }
                            // process errors from resource handler
                            catch (Exception e) {
                                Throwable cause = e.getCause();
                                if (cause == null) {
                                    cause = e;
                                }
                                if (cause instanceof FlowException) {
                                    ctx.response()
                                            .setStatusCode(400)
                                            .putHeader("Content-Type", MediaType.APPLICATION_JSON)
                                            .end(ResponseCreator.newErrorResponse((FlowException) cause));
                                    return;
                                }
                                logger.error("error occurs when invoking methods", e);
                                ctx.response()
                                        .setStatusCode(400)
                                        .putHeader("Content-Type", MediaType.APPLICATION_JSON)
                                        .end(ResponseCreator.newErrorResponse(cause));
                            }
                        }, executorService));
                    });
                });
    }

    private void processResponse(RoutingContext ctx, String produceType, Object result) {
        if (produceType.contains(MediaType.APPLICATION_JSON)) {
            if (result instanceof String) {
                ctx.response()
                        .putHeader("Content-Type", produceType)
                        .end(result.toString());
            } else {
                ctx.response()
                        .putHeader("Content-Type", produceType)
                        .end(JacksonWriter.write(result));
            }
        } else {
            ctx.response()
                    .putHeader("Content-Type", produceType)
                    .end(result.toString());
        }
    }

    private Object invokeMethod(RoutingContextWrapper wrapper,
                                Object singleton,
                                Method method,
                                String consumeType,
                                ResourceDescriptor descriptor)
            throws IllegalAccessException, InvocationTargetException {
        Object result;

        if (MediaType.APPLICATION_FORM_URLENCODED.equals(consumeType)) {
            result = processFormRequest(wrapper, singleton, method, descriptor);
        } else if (MediaType.MULTIPART_FORM_DATA.equals(consumeType)) {
            result = processFormDataRequest(wrapper, singleton, method, descriptor);
        } else {
            result = processPathParams(wrapper, singleton, method, descriptor);
        }

        return result;
    }

    private Object processPathParams(RoutingContextWrapper wrapper,
                                     Object singleton,
                                     Method method,
                                     ResourceDescriptor descriptor)
            throws InvocationTargetException, IllegalAccessException {
        RoutingContext context = wrapper.getContext();
        HttpServerRequest request = context.request();

        MultiMap requestParams = request.params();
        ImmutableMap<String, Pair<Integer, Type>> pathParams = descriptor.pathParams;
        ImmutableMap<String, Pair<Integer, Type>> queryParams = descriptor.queryParams;
        ImmutableMap<String, Pair<Integer, Type>> contextParams = descriptor.contextParams;

        int pathParamsSize = pathParams == null ? 0 : pathParams.size();
        int queryParamsSize = queryParams == null ? 0 : queryParams.size();
        int contextParamsSize = contextParams == null ? 0 : contextParams.size();
        Object[] params = new Object[method.getParameterCount()];

        setParameters(requestParams, pathParams, pathParamsSize, params);
        setParameters(requestParams, queryParams, queryParamsSize, params);

        // process context param
        if (contextParamsSize > 0) {
            for (Map.Entry<String, Pair<Integer, Type>> param : contextParams.entrySet()) {
                Class<?> type = (Class<?>) param.getValue().second;
                if (type.isAssignableFrom(RoutingContextWrapper.class)) {
                    params[param.getValue().first] = wrapper;
                }
            }
        }
        return method.invoke(singleton, params);
    }

    private void setParameters(MultiMap requestParams,
                               ImmutableMap<String, Pair<Integer, Type>> paramsDesc,
                               int sizeOfParamsDesc,
                               Object[] params) {
        if (sizeOfParamsDesc > 0) {
            for (Map.Entry<String, Pair<Integer, Type>> param : paramsDesc.entrySet()) {
                String paramName = param.getKey();
                Object paramValue = requestParams.get(paramName);
                Class<?> type = (Class<?>) param.getValue().second;
                castValue(params, param, paramValue, type);
            }
        }
    }

    private Object processFormRequest(RoutingContextWrapper wrapper,
                                      Object singleton,
                                      Method method,
                                      ResourceDescriptor descriptor) throws IllegalAccessException, InvocationTargetException {
        ImmutableMap<String, Pair<Integer, Type>> paramNames = descriptor.formParams;
        if (null != paramNames) {
            MultiMap paramMap = wrapper.request().formAttributes();
            Object[] params = new Object[paramMap.size() + 1];
            for (Map.Entry<String, Pair<Integer, Type>> param : paramNames.entrySet()) {
                Object value = paramMap.get(param.getKey());
                Class<?> type = (Class<?>) param.getValue().second;
                castValue(params, param, value, type);

            }
            params[0] = wrapper;
            return method.invoke(singleton, params);
        }
        return method.invoke(singleton, wrapper);
    }

    // TODO: 16/10/28
    private Object processFormDataRequest(RoutingContextWrapper wrapper,
                                          Object singleton,
                                          Method method,
                                          ResourceDescriptor descriptor) throws IllegalAccessException, InvocationTargetException {
        ImmutableMap<String, Pair<Integer, Type>> paramNames = descriptor.formParams;
        if (null != paramNames) {
            MultiMap paramMap = wrapper.request().formAttributes();
            Object[] params = new Object[paramNames.size() + 1];
            for (Map.Entry<String, Pair<Integer, Type>> param : paramNames.entrySet()) {
                Object value = paramMap.get(param.getKey());
                Class<?> type = (Class<?>) param.getValue().second;
                if (type == FileUpload.class && type.isAssignableFrom(FileUpload.class)) {
                    FileUpload fileUpload = wrapper.uploadedFile(param.getKey());
                    params[param.getValue().first] = fileUpload;
                } else {
                    castValue(params, param, value, type);
                }

            }
            params[0] = wrapper;
            return method.invoke(singleton, params);
        }
        return method.invoke(singleton, wrapper);
    }

    private void castValue(Object[] params, Map.Entry<String, Pair<Integer, Type>> param, Object value, Class<?> type) {
        if (type.isAssignableFrom(int.class)) {
            params[param.getValue().first] = Integer.valueOf(value.toString());
        } else if (type.isAssignableFrom(long.class)) {
            params[param.getValue().first] = Long.valueOf(value.toString());
        } else if (type.isAssignableFrom(float.class)) {
            params[param.getValue().first] = Float.valueOf(value.toString());
        } else if (type.isAssignableFrom(double.class)) {
            params[param.getValue().first] = Double.valueOf(value.toString());
        } else if (value == null) {
            params[param.getValue().first] = null;
        } else {
            params[param.getValue().first] = type.cast(value);
        }
    }

    private void initConfig() {
        // ioc
        injector = Guice.createInjector(new ServerModule());

        if (serverConfig == null) {
            serverConfig = new ServerConfig();
        }
    }

    public Injector getInjector() {
        return injector;
    }
}
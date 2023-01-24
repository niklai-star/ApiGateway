package com.niklai.apigateway.route;

import com.alicp.jetcache.CacheManager;
import com.niklai.apigateway.entity.RouteInfo;
import com.niklai.apigateway.request.ApiRequestBody;
import com.niklai.apigateway.response.ApiResponseBody;
import com.niklai.apigateway.utils.ApiGatewayContextHolder;
import com.niklai.apigateway.utils.CacheUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.springframework.util.CollectionUtils.unmodifiableMultiValueMap;

public class DemoRouteLocator implements RouteLocator {
    private static final String API_REQUEST_BODY = "API_REQUEST_BODY";

    private final RouteLocatorBuilder routeLocatorBuilder;

    private final CacheManager cacheManager;

    public DemoRouteLocator(RouteLocatorBuilder builder, CacheManager cacheManager) {
        this.routeLocatorBuilder = builder;
        this.cacheManager = cacheManager;
    }

    @Override
    public Flux<Route> getRoutes() {
        RouteLocatorBuilder.Builder builder = routeLocatorBuilder.routes();

        builder = builder.route("default",
                        predicateSpec -> predicateSpec.path("/route/rest")
                                .and()
                                .method(HttpMethod.POST, HttpMethod.PUT)
                                .and()
                                .readBody(ApiRequestBody.class, readBodyPredicate())
                                .filters(f -> f.modifyRequestBody(ApiRequestBody.class, Object.class, modifyRequestBodyFilterFunction())
                                        .modifyResponseBody(Object.class, ApiResponseBody.class, modifyResponseBodyFilterFunction())
                                        .filters(
                                                customSetPath(),
                                                changeRoute()
                                        ))
                                .uri("http://localhost:8080"))
                .route("default2",
                        predicateSpec -> predicateSpec.path("/route/rest")
                                .and()
                                .method(HttpMethod.GET, HttpMethod.DELETE)
                                .and()
                                .asyncPredicate(getPredicate())
                                .filters(f -> f
                                        .modifyResponseBody(Object.class, ApiResponseBody.class, modifyResponseBodyFilterFunction())
                                        .filters(
                                                customSetPath(),
                                                customRemoveParam("api", "v"),
                                                changeRoute()
                                        ))
                                .uri("http://localhost:8080"));
        return builder.build().getRoutes();
    }

    private Predicate<ApiRequestBody> readBodyPredicate() {
        return apiRequestBody -> {
            if (StringUtils.isBlank(apiRequestBody.getApi())) {
                return false;
            }

            Optional<RouteInfo> infoOptional = getRouteInfo(apiRequestBody.getApi());
            if (infoOptional.isPresent()) {
                ApiGatewayContextHolder.get().getAttributes().put("ROUTE", infoOptional.get());
                return true;
            }

            return false;
        };
    }

    private AsyncPredicate<ServerWebExchange> getPredicate() {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            if (!request.getQueryParams().containsKey("api")) {
                return Mono.just(false);
            }


            Optional<RouteInfo> infoOptional = getRouteInfo(request.getQueryParams().getFirst("api"));
            if (infoOptional.isPresent()) {
                exchange.getAttributes().put("ROUTE", infoOptional.get());
                return Mono.just(true);
            }

            return Mono.just(false);
        };
    }

    private RewriteFunction<ApiRequestBody, Object> modifyRequestBodyFilterFunction() {
        return (exchange, apiRequestBody) -> {
            if (apiRequestBody != null) {
                exchange.getAttributes().put(API_REQUEST_BODY, apiRequestBody);
                return Mono.justOrEmpty(apiRequestBody.getBody());
            }

            return Mono.empty();
        };
    }

    private RewriteFunction<Object, ApiResponseBody> modifyResponseBodyFilterFunction() {
        return (serverWebExchange, o) -> {
            ServerHttpResponse response = serverWebExchange.getResponse();
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ResponseStatusException(response.getStatusCode());
            }

            return Mono.just(ApiResponseBody.builder().data(o).build());
        };
    }

    private GatewayFilter changeRoute() {
        return (exchange, chain) -> {
            RouteInfo routeInfo = exchange.getAttribute("ROUTE");
            Route route = Route.async()
                    .id(routeInfo.getProject() + ":" + routeInfo.getService())
                    .predicate(serverWebExchange -> true)
                    .uri(routeInfo.getUri())
                    .build();
            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR, route);
            return chain.filter(exchange);
        };
    }

    private GatewayFilter customSetPath() {
        return (exchange, chain) -> {
            ServerHttpRequest req = exchange.getRequest();
            exchange.getAttributes().computeIfAbsent(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR, s -> new LinkedHashSet<>());
            LinkedHashSet<URI> uris = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
            uris.add(req.getURI());

            String api = getApi(exchange);
            String[] paths = api.split("\\.");
            StringBuffer sb = new StringBuffer();

            String v = getV(exchange);
            if (StringUtils.isNotBlank(v)) {
                sb.append("/" + v);
            }

            for (int i = 2; i < paths.length; i++) {
                sb.append("/").append(paths[i]);
            }

            ServerHttpRequest request = req.mutate().path(sb.toString()).build();
            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    private String getApi(ServerWebExchange exchange) {
        ApiRequestBody apiRequestBody = exchange.getAttribute(API_REQUEST_BODY);
        if (apiRequestBody != null) {
            return apiRequestBody.getApi();
        }
        return exchange.getRequest().getQueryParams().getFirst("api");
    }

    private String getV(ServerWebExchange exchange) {
        ApiRequestBody apiRequestBody = exchange.getAttribute(API_REQUEST_BODY);
        if (apiRequestBody != null) {
            return apiRequestBody.getV();
        }
        return exchange.getRequest().getQueryParams().getFirst("v");
    }

    private GatewayFilter customRemoveParam(final String... param) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(request.getQueryParams());
            for (String p : param) {
                queryParams.remove(p);
            }
            URI newUri = UriComponentsBuilder.fromUri(request.getURI())
                    .replaceQueryParams(unmodifiableMultiValueMap(queryParams)).build().toUri();
            ServerHttpRequest updatedRequest = exchange.getRequest().mutate().uri(newUri).build();
            return chain.filter(exchange.mutate().request(updatedRequest).build());
        };
    }

    private Optional<RouteInfo> getRouteInfo(String api) {
        String[] apiSplit = api.split("\\.");
        if (apiSplit.length <= 2) {
            return Optional.empty();
        }

        List<RouteInfo> routeInfos = (List<RouteInfo>) cacheManager.getCache(CacheUtils.ROUTE_INFO_LIST_KEY).get("");
        if (CollectionUtils.isEmpty(routeInfos)) {
            return Optional.empty();
        }

        return routeInfos.stream().filter(routeInfo -> routeInfo.getProject().equals(apiSplit[0]) && routeInfo.getService().equals(apiSplit[1])).findFirst();
    }
}

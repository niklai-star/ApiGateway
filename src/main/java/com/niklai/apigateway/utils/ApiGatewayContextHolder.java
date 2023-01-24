package com.niklai.apigateway.utils;

import org.springframework.web.server.ServerWebExchange;

public class ApiGatewayContextHolder {
    private ApiGatewayContextHolder() {
    }

    private static final ThreadLocal<ServerWebExchange> TL = new ThreadLocal<>();

    public static void set(ServerWebExchange exchange) {
        TL.set(exchange);
    }

    public static ServerWebExchange get() {
        return TL.get();
    }

    public static void cleanUp() {
        TL.remove();
    }
}

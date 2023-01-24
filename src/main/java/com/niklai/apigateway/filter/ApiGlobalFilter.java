package com.niklai.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ApiGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.just(exchange)).map(serverWebExchange -> {
            ServerHttpResponse response = serverWebExchange.getResponse();
            return serverWebExchange;
        }).then();
    }

    @Override
    public int getOrder() {
        return -90;
    }
}

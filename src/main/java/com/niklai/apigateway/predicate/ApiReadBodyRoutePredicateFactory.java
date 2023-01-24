package com.niklai.apigateway.predicate;

import com.niklai.apigateway.utils.ApiGatewayContextHolder;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.ReadBodyRoutePredicateFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class ApiReadBodyRoutePredicateFactory extends ReadBodyRoutePredicateFactory {

    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    @Override
    public AsyncPredicate<ServerWebExchange> applyAsync(Config config) {
        return new AsyncPredicate<>() {
            public Publisher<Boolean> apply(ServerWebExchange exchange) {
                ApiGatewayContextHolder.set(exchange);
                Class inClass = config.getInClass();
                Object cachedBody = exchange.getAttribute("cachedRequestBodyObject");
                if (cachedBody != null) {
                    try {
                        boolean test = config.getPredicate().test(cachedBody);
                        exchange.getAttributes().put("read_body_predicate_test_attribute", test);
                        return Mono.just(test);
                    } catch (ClassCastException var6) {
                        if (ReadBodyRoutePredicateFactory.log.isDebugEnabled()) {
                            ReadBodyRoutePredicateFactory.log.debug("Predicate test failed because class in predicate does not match the cached body object", var6);
                        }

                        return Mono.just(false);
                    }
                } else {
                    return ServerWebExchangeUtils.cacheRequestBodyAndRequest(exchange, (serverHttpRequest) -> {
                        return ServerRequest.create(exchange.mutate().request(serverHttpRequest).build(), ApiReadBodyRoutePredicateFactory.this.messageReaders)
                                .bodyToMono(inClass).doOnNext((objectValue) -> {
                                    exchange.getAttributes().put("cachedRequestBodyObject", objectValue);
                                }).map((objectValue) -> {
                                    return config.getPredicate().test(objectValue);
                                });
                    });
                }
            }

            public Object getConfig() {
                return config;
            }

            public String toString() {
                return String.format("ReadBody: %s", config.getInClass());
            }
        };
    }
}

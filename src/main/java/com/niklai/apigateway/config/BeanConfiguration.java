package com.niklai.apigateway.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niklai.apigateway.error.ApiErrorAttributes;
import com.niklai.apigateway.error.ApiErrorWebExceptionHandler;
import com.niklai.apigateway.route.DemoRouteLocator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.stream.Collectors;

@Configuration
public class BeanConfiguration {

    @Bean
    public ApiErrorAttributes errorAttributes() {
        return new ApiErrorAttributes();
    }

    @Bean
    @Order(-1)
    public DefaultErrorWebExceptionHandler errorWebExceptionHandler(
            ErrorAttributes errorAttributes,
            WebProperties webProperties,
            ObjectProvider<ViewResolver> viewResolvers,
            ServerCodecConfigurer serverCodecConfigurer,
            ApplicationContext applicationContext,
            ServerProperties serverProperties) {
        ApiErrorWebExceptionHandler handler = new ApiErrorWebExceptionHandler(errorAttributes, webProperties.getResources(), serverProperties.getError(), applicationContext);
        handler.setViewResolvers(viewResolvers.orderedStream().collect(Collectors.toList()));
        handler.setMessageWriters(serverCodecConfigurer.getWriters());
        handler.setMessageReaders(serverCodecConfigurer.getReaders());
        return handler;
    }

    @Bean
    public RouteLocator routeLocator(
            RouteLocatorBuilder builder,
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper
    ) {
        return new DemoRouteLocator(builder, redisTemplate, objectMapper);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}

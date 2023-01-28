package com.niklai.apigateway.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niklai.apigateway.ddd.entity.ApiRoute;
import com.niklai.apigateway.ddd.service.RouteService;
import com.niklai.apigateway.utils.CacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class CacheRunner implements CommandLineRunner {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RouteService routeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        List<ApiRoute> routes = routeService.getRoutes();
        if (routes == null) {
            routes = new ArrayList<>();
        }

        redisTemplate.opsForValue().set(CacheUtils.ROUTE_INFO_LIST_KEY, objectMapper.writeValueAsString(routes), Duration.ofDays(1));
    }
}

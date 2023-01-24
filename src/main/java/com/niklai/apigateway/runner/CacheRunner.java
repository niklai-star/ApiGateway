package com.niklai.apigateway.runner;

import com.alicp.jetcache.CacheLoader;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.template.QuickConfig;
import com.niklai.apigateway.entity.RouteInfo;
import com.niklai.apigateway.repository.RouteInfoRepository;
import com.niklai.apigateway.utils.CacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class CacheRunner implements CommandLineRunner {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RouteInfoRepository routeInfoRepository;

    @Override
    public void run(String... args) throws Exception {
        cacheManager.getOrCreateCache(
                QuickConfig.newBuilder(CacheUtils.ROUTE_INFO_LIST_KEY)
                        .expire(Duration.ofDays(1))
                        .loader((CacheLoader<String, List<RouteInfo>>) key -> {
                            Iterable<RouteInfo> routeInfos = routeInfoRepository.findAll();
                            List<RouteInfo> infos = new ArrayList<>();
                            routeInfos.forEach(infos::add);
                            return infos;
                        })
                        .build()
        );
    }
}

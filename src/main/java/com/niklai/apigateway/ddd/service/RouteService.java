package com.niklai.apigateway.ddd.service;

import com.niklai.apigateway.ddd.entity.ApiRoute;
import com.niklai.apigateway.jpa.entity.RouteInfo;
import com.niklai.apigateway.jpa.repository.RouteInfoRepository;
import com.niklai.apigateway.mapstruct.mapper.ApiRouteStructMappper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    @Autowired
    private RouteInfoRepository routeInfoRepository;

    @Autowired
    private ApiRouteStructMappper ApiRouteStructMappper;

    public List<ApiRoute> getRoutes() {
        List<RouteInfo> routeInfoList = routeInfoRepository.findByDelFlagFalse();
        return ApiRouteStructMappper.toApiRouteList(routeInfoList);
    }
}

package com.niklai.apigateway.mapstruct.mapper;

import com.niklai.apigateway.ddd.entity.ApiRoute;
import com.niklai.apigateway.jpa.entity.RouteInfo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApiRouteStructMappper {

    List<ApiRoute> toApiRouteList(List<RouteInfo> routeInfo);
}

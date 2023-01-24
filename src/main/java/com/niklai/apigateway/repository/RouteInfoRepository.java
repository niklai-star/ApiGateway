package com.niklai.apigateway.repository;

import com.niklai.apigateway.entity.RouteInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteInfoRepository extends CrudRepository<RouteInfo, Long> {

}

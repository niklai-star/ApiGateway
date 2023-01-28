package com.niklai.apigateway.jpa.repository;

import com.niklai.apigateway.jpa.entity.RouteInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteInfoRepository extends CrudRepository<RouteInfo, Long> {
    List<RouteInfo> findByDelFlagFalse();

}

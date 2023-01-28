package com.niklai.apigateway.ddd.entity;

import lombok.Data;

@Data
public class ApiRoute {
    private Long id;
    private String project;

    private String service;

    private String uri;
}

package com.niklai.apigateway.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "route_info")
public class RouteInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint", nullable = false)
    private Long id;

    @Column(name = "project", columnDefinition = "varchar(20)", nullable = false)
    private String project;

    @Column(name = "service", columnDefinition = "varchar(20)", nullable = false)
    private String service;

    @Column(name = "uri", columnDefinition = "varchar(20)", nullable = false)
    private String uri;
}

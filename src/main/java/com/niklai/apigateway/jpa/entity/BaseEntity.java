package com.niklai.apigateway.jpa.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class BaseEntity implements Serializable {

    @Column(name = "created_at", columnDefinition = "varchar(32)", updatable = false)
    private String createdAt;

    @Column(name = "created_time", updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",
            nullable = false)
    private String createdTime;

    @Column(name = "updated_at", columnDefinition = "varchar(32)")
    private String updatedAt;

    @Column(name = "updated_time", updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",
            nullable = false)
    private String updatedTime;

    @Column(name = "del_flag", columnDefinition = "bit default 0", nullable = false)
    private Boolean delFlag;
}

package com.niklai.apigateway.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiRequestBody {

    @NotEmpty
    private String api;

    private String v;

    private Object body;
}

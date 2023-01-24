package com.niklai.apigateway;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiException extends RuntimeException {
    private String code;
}

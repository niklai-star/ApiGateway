package com.niklai.apigateway.error;

import lombok.Data;

@Data
public class ApiGatewayException extends RuntimeException {
    private String code;

    private String msg;

    public ApiGatewayException(String code) {
        this.code = code;
    }

    public ApiGatewayException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiGatewayException(String code, String msg, Throwable throwable) {
        super(throwable);
        this.code = code;
        this.msg = msg;
    }
}

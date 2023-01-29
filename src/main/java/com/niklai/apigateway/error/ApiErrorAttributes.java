package com.niklai.apigateway.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ApiErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = this.getError(request);
        log.error(error.getMessage(), error);
        if (error instanceof ApiGatewayException) {
            return apiErrorAttributes((ApiGatewayException) error);
        }

        Map<String, Object> errorAttributes = new LinkedHashMap();
        MergedAnnotation<ResponseStatus> responseStatusAnnotation = MergedAnnotations.from(error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ResponseStatus.class);
        HttpStatus errorStatus = error instanceof ResponseStatusException ? ((ResponseStatusException) error).getStatus() : responseStatusAnnotation.getValue("code", HttpStatus.class).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        errorAttributes.put("code", errorStatus.value());
        errorAttributes.put("msg", errorStatus.getReasonPhrase());
        errorAttributes.put("data", null);
        return errorAttributes;
    }

    private Map<String, Object> apiErrorAttributes(ApiGatewayException ex) {
        Map<String, Object> errorAttributes = new LinkedHashMap();
        errorAttributes.put("code", ex.getCode());
        errorAttributes.put("msg", ex.getMsg());
        errorAttributes.put("data", null);
        return errorAttributes;
    }
}

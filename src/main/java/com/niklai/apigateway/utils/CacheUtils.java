package com.niklai.apigateway.utils;

import java.util.Locale;

public class CacheUtils {
    public static final String ROUTE_INFO_LIST_KEY = "gateway:routeInfoList";

    private static final String ACCESS_TOKEN_KEY_PREFIX = "access_token:";

    public static final String accessTokenKey(String accessToken) {
        return String.format(Locale.ENGLISH, "%s%s", ACCESS_TOKEN_KEY_PREFIX, accessToken);
    }
}

package io.surisoft.capi.lb.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HttpUtils {
    public String setHttpConnectTimeout(String endpoint, int timeout) {
        if (!endpoint.endsWith("&")) {
            endpoint = endpoint + "&";
        }
        return endpoint + Constants.HTTP_CONNECT_TIMEOUT + timeout;
    }

    public String setHttpSocketTimeout(String endpoint, int timeout) {
        if (!endpoint.endsWith("&")) {
            endpoint = endpoint + "&";
        }
        return endpoint + Constants.HTTP_SOCKET_TIMEOUT + timeout;
    }
}
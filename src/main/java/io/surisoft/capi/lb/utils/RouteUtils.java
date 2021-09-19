package io.surisoft.capi.lb.utils;

import io.surisoft.capi.lb.processor.HttpErrorProcessor;
import io.surisoft.capi.lb.schema.Api;
import io.surisoft.capi.lb.schema.HttpMethod;
import io.surisoft.capi.lb.schema.Mapping;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.apache.camel.language.constant.ConstantLanguage.constant;

@Component
public class RouteUtils {

    @Value("${capi.gateway.error.endpoint}")
    private String capiGatewayErrorEndpoint;

    @Autowired
    private HttpErrorProcessor httpErrorProcessor;

    @Autowired
    private HttpUtils httpUtils;

    public void buildOnExceptionDefinition(RouteDefinition routeDefinition,
                                           boolean isZipkinTraceIdVisible,
                                           boolean isInternalExceptionMessageVisible,
                                           boolean isInternalExceptionVisible,
                                           String routeID) {
        routeDefinition
                .onException(Exception.class)
                .handled(true)
                .setHeader(Constants.ERROR_API_SHOW_TRACE_ID, constant(isZipkinTraceIdVisible))
                .setHeader(Constants.ERROR_API_SHOW_INTERNAL_ERROR_MESSAGE, constant(isInternalExceptionMessageVisible))
                .setHeader(Constants.ERROR_API_SHOW_INTERNAL_ERROR_CLASS, constant(isInternalExceptionVisible))
                .process(httpErrorProcessor)
                .setHeader(Constants.ROUTE_ID_HEADER, constant(routeID))
                .toF(Constants.FAIL_REST_ENDPOINT_OBJECT, capiGatewayErrorEndpoint)
                .removeHeader(Constants.ERROR_API_SHOW_TRACE_ID)
                .removeHeader(Constants.ERROR_API_SHOW_INTERNAL_ERROR_MESSAGE)
                .removeHeader(Constants.ERROR_API_SHOW_INTERNAL_ERROR_CLASS)
                .removeHeader(Constants.CAPI_URL_IN_ERROR)
                .removeHeader(Constants.CAPI_URI_IN_ERROR)
                .removeHeader(Constants.ROUTE_ID_HEADER)
                .end();
    }

    public String[] buildEndpoints(Api api) {
        List<String> transformedEndpointList = new ArrayList<>();
        for(Mapping mapping : api.getMappingList()) {
            //endpoint = pathHasParams ? protocol + endpoint + Constants.HTTP4_CALL_PARAMS : protocol + endpoint + httpUtils.setPath(path) + Constants.HTTP4_CALL_PARAMS;
            String endpoint = api.getHttpProtocol() + "://" + mapping.getHostname() + ":" + mapping.getPort() + mapping.getRootContext() + "?bridgeEndpoint=true&throwExceptionOnFailure=false";
            if(api.getConnectTimeout() > -1) {
                endpoint = httpUtils.setHttpConnectTimeout(endpoint, api.getConnectTimeout());
            }
            if(api.getSocketTimeout() > -1) {
                endpoint = httpUtils.setHttpSocketTimeout(endpoint, api.getSocketTimeout());
            }
            transformedEndpointList.add(endpoint);
        }
        return transformedEndpointList.stream().toArray(n -> new String[n]);
    }

    public String buildFrom(Api api) {
        if(!api.getContext().startsWith("/")) {
            return "/" + api.getContext();
        }
        return api.getContext();
    }

    public HttpMethod getMethodFromRoute(RouteDefinition routeDefinition) {
        VerbDefinition verbDefinition = routeDefinition.getRestDefinition().getVerbs().get(0);
        if(verbDefinition instanceof GetVerbDefinition) {
            return HttpMethod.GET;
        } else if(verbDefinition instanceof PostVerbDefinition) {
            return HttpMethod.POST;
        } else if(verbDefinition instanceof PutVerbDefinition) {
            return HttpMethod.PUT;
        } else if(verbDefinition instanceof DeleteVerbDefinition) {
            return HttpMethod.DELETE;
        }
        return null;
    }

}

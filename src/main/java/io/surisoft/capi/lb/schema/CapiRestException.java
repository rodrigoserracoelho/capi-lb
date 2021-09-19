package io.surisoft.capi.lb.schema;

import lombok.Data;

@Data
public class CapiRestException {
    private String routeID;
    private String errorMessage;
    private int errorCode;
    private String httpUrl;
    private String httpUri;
    private String exception;
    private String internalExceptionMessage;
    private String zipkinTraceID;
}

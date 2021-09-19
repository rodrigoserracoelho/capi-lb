package io.surisoft.capi.lb.schema;

import lombok.Data;

import java.io.Serializable;

@Data
public class RunningApi implements Serializable {
    private String apiId;
    private String routeId;
    private String name;
    private String context;
    private HttpMethod httpMethod;

}
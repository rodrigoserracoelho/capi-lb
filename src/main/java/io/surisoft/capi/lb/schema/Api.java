package io.surisoft.capi.lb.schema;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Api implements Serializable {
    public static final String CLIENT_KEY = "Api";

    private String id;
    private String name;
    private String context;
    private List<Mapping> mappingList = new ArrayList<>();
    private boolean roundRobinEnabled;
    private boolean failoverEnabled;
    private boolean matchOnUriPrefix;
    private HttpMethod httpMethod;
    private HttpProtocol httpProtocol;
    private String swaggerEndpoint;
    private int connectTimeout;
    private int socketTimeout;
    private int maximumFailoverAttempts;
}

package io.surisoft.capi.lb.schema;

import lombok.Data;

import java.io.Serializable;

@Data
public class Mapping implements Serializable {
    private String hostname;
    private int port;
    private String rootContext;
}

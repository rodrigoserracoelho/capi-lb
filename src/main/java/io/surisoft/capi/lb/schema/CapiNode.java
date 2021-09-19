package io.surisoft.capi.lb.schema;

import lombok.Data;

import java.io.Serializable;

@Data
public class CapiNode implements Serializable {
    public static final String CLIENT_KEY = "CapiNode";

    //private String uuid;
    private String address;
    private int port;
}

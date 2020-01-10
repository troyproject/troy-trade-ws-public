package com.troy.streamingexchange.gateio.dto;

import java.io.Serializable;

/**
 * Created by Pavel Chertalev on 15.03.2018.
 */
public class GateioWebSocketBaseTransaction implements Serializable {

    protected final String method;

    public GateioWebSocketBaseTransaction(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

}

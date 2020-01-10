package com.troy.streamingexchange.gateio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Pavel Chertalev on 15.03.2018.
 */
public class GateioWebSocketSubscriptionMessage {

    private final String method;
    private final int id;
    private final List params;

    public GateioWebSocketSubscriptionMessage(@JsonProperty("id") int id, @JsonProperty("method") String method, @JsonProperty("params") List params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public int getId() {
        return id;
    }

    public List getParams() {
        return params;
    }
}

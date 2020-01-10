package com.troy.streamingexchange.gateio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Pavel Chertalev on 15.03.2018.
 */
public class GateioWebSocketBaseParams {

    protected final String symbol;

    public GateioWebSocketBaseParams(@JsonProperty("symbol") String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

}

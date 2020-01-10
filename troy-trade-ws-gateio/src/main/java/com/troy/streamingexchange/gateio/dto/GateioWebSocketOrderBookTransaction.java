package com.troy.streamingexchange.gateio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Pavel Chertalev on 15.03.2018.
 */
public class GateioWebSocketOrderBookTransaction extends GateioWebSocketBaseTransaction {

    private static final String ORDERBOOK_METHOD_UPDATE = "depth.update";
    private GateioWebSocketOrderBookParams params;

    public GateioWebSocketOrderBookTransaction(@JsonProperty("method") String method, @JsonProperty("params") GateioWebSocketOrderBookParams params) {
        super(method);
        this.params = params;
    }

    public GateioWebSocketOrderBookParams getParams() {
        return params;
    }

    public GateioWebSocketOrderBook toGateioOrderBook(GateioWebSocketOrderBook orderbook) {
        if (orderbook == null) {
            return new GateioWebSocketOrderBook(this,false);
        }
        orderbook.updateOrderBook(this);
        return orderbook;
    }
}

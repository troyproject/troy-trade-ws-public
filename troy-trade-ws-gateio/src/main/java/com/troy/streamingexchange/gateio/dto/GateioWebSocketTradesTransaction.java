package com.troy.streamingexchange.gateio.dto;

/**
 * Created by Pavel Chertalev on 15.03.2018.
 */
public class GateioWebSocketTradesTransaction extends GateioWebSocketBaseTransaction {
    private final GateioWebSocketTradeParams params;

    public GateioWebSocketTradesTransaction(String method, GateioWebSocketTradeParams params) {
        super(method);
        this.params = params;
    }

    public GateioWebSocketTradeParams getParams() {
        return params;
    }

}

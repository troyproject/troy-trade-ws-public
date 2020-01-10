package com.troy.streamingexchange.gateio.dto;


import java.util.List;

/**
 * Created by Pavel Chertalev on 15.03.2018.
 */
public class GateioWebSocketOrderBookParams extends GateioWebSocketBaseParams {

    private final List<GateioPublicOrder> asks;
    private final List<GateioPublicOrder> bids;
    private final String result;

    public GateioWebSocketOrderBookParams(String symbol, List<GateioPublicOrder> asks, List<GateioPublicOrder> bids, String result) {
        super(symbol);
        this.asks = asks;
        this.bids = bids;
        this.result = result;
    }

    public List<GateioPublicOrder> getAsks() {
        return asks;
    }

    public List<GateioPublicOrder> getBids() {
        return bids;
    }

    public String getResult() {
        return result;
    }
}

package com.troy.streamingexchange.gateio.dto;

import java.util.List;

/**
 * Created by Pavel Chertalev on 15.03.2018.
 */
public class GateioWebSocketTradeParams extends GateioWebSocketBaseParams {

    private final List<GateioPublicTrade> data;

    public GateioWebSocketTradeParams(String symbol, List<GateioPublicTrade> data) {
        super(symbol);
        this.data = data;
    }

    public List<GateioPublicTrade> getData() {
        return data;
    }

}


package com.troy.streamingexchange.gateio.dto;

import java.util.List;

/**
 * GateioWebSocketOpenOrderParams
 *
 * @author liuxiaocheng
 * @date 2018/7/2
 */
public class GateioWebSocketOpenOrderParams extends GateioWebSocketBaseParams {
    private List<GateioOrderUpdate> gateioOrderUpdates;

    public GateioWebSocketOpenOrderParams(String symbol, List<GateioOrderUpdate> gateioOrderUpdates) {
        super(symbol);
        this.gateioOrderUpdates = gateioOrderUpdates;
    }

    public List<GateioOrderUpdate> getGateioOrderUpdates() {
        return gateioOrderUpdates;
    }
}

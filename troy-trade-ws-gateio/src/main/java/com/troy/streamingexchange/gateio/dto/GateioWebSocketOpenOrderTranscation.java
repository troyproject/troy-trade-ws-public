package com.troy.streamingexchange.gateio.dto;

/**
 * GateioWebSocketOpenOrderTranscation
 *
 * @author liuxiaocheng
 * @date 2018/7/2
 */
public class GateioWebSocketOpenOrderTranscation extends GateioWebSocketBaseTransaction {

    private static final String ORDERBOOK_METHOD_UPDATE = "order.update";
    private GateioWebSocketOpenOrderParams params;

    public GateioWebSocketOpenOrderTranscation(String method, GateioWebSocketOpenOrderParams params) {
        super(method);
        this.params = params;
    }

    public static String getOrderbookMethodUpdate() {
        return ORDERBOOK_METHOD_UPDATE;
    }

    public GateioWebSocketOpenOrderParams getParams() {
        return params;
    }

    public void setParams(GateioWebSocketOpenOrderParams params) {
        this.params = params;
    }
}

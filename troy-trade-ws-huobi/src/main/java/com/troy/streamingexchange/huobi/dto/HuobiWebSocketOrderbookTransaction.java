package com.troy.streamingexchange.huobi.dto;

public abstract class HuobiWebSocketOrderbookTransaction {

    protected HuobiDepthResult huobiDepthResult;

    public HuobiWebSocketOrderbookTransaction(HuobiDepthResult huobiDepthResult) {
        this.huobiDepthResult = huobiDepthResult;
    }

    public abstract HuobiOrderbook toHuobiOrderBook();

    public abstract HuobiOrderbookLevel[] getAsks();

    public abstract HuobiOrderbookLevel[] getBids();
}

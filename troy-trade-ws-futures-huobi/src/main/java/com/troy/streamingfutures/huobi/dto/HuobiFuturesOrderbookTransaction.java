package com.troy.streamingfutures.huobi.dto;

public abstract class HuobiFuturesOrderbookTransaction {

    protected HuobiFuturesDepthResult huobiFuturesDepthResult;

    public HuobiFuturesOrderbookTransaction(HuobiFuturesDepthResult huobiFuturesDepthResult) {
        this.huobiFuturesDepthResult = huobiFuturesDepthResult;
    }

    public abstract HuobiFuturesOrderbook toHuobiFuturesOrderBook();

    public abstract HuobiFuturesOrderbookLevel[] getAsks();

    public abstract HuobiFuturesOrderbookLevel[] getBids();
}

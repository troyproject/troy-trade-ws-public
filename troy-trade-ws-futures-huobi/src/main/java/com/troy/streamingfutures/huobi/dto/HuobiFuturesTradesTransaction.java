package com.troy.streamingfutures.huobi.dto;

import java.util.List;

/**
 * HuobiFuturesTradesTransaction
 */
public abstract class HuobiFuturesTradesTransaction {
    protected HuobiFuturesTradeResult huobiTradeResult;

    public HuobiFuturesTradesTransaction(HuobiFuturesTradeResult huobiTradeResult) {
        this.huobiTradeResult = huobiTradeResult;
    }

    public HuobiFuturesTradeResult getHuobiTradeResult() {
        return huobiTradeResult;
    }

    public abstract List<HuobiFuturesTrade> toHuobiTrades();
}

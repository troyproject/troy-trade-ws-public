package com.troy.streamingexchange.huobi.dto;

import java.util.List;

/**
 * HuobiWebSocketTradesTransaction
 *
 * @author liuxiaocheng
 * @date 2018/7/12
 */
public abstract class HuobiWebSocketTradesTransaction {
    protected HuobiTradeResult huobiTradeResult;

    public HuobiWebSocketTradesTransaction(HuobiTradeResult huobiTradeResult) {
        this.huobiTradeResult = huobiTradeResult;
    }

    public HuobiTradeResult getHuobiTradeResult() {
        return huobiTradeResult;
    }

    public abstract List<HuobiTrade> toHuobiTrades();
}

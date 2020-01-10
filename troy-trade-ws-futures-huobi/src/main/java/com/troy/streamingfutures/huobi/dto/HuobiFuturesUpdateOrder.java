package com.troy.streamingfutures.huobi.dto;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * HuobiFuturesUpdateOrder
 */
public class HuobiFuturesUpdateOrder extends HuobiFuturesTradesTransaction {
    public HuobiFuturesUpdateOrder(HuobiFuturesTradeResult huobiTradeResult) {
        super(huobiTradeResult);
    }

    @Override
    public List<HuobiFuturesTrade> toHuobiTrades() {
        List<HuobiFuturesTrade> huobiTrades = Lists.newArrayList();
        if (this.huobiTradeResult != null && !CollectionUtils.isEmpty(huobiTradeResult.getTick().getData())) {
            huobiTrades = huobiTradeResult.getTick().getData();
        }
        return huobiTrades;
    }
}

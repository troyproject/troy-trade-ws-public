package com.troy.streamingexchange.huobi.dto;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * HuobiWebSocketUpdateOrder
 *
 * @author liuxiaocheng
 * @date 2018/7/12
 */
public class HuobiWebSocketUpdateOrder extends HuobiWebSocketTradesTransaction {
    public HuobiWebSocketUpdateOrder(HuobiTradeResult huobiTradeResult) {
        super(huobiTradeResult);
    }

    @Override
    public List<HuobiTrade> toHuobiTrades() {
        List<HuobiTrade> huobiTrades = Lists.newArrayList();
        if (this.huobiTradeResult != null && !CollectionUtils.isEmpty(huobiTradeResult.getTick().getData())) {
            huobiTrades = huobiTradeResult.getTick().getData();
        }
        return huobiTrades;
    }
}

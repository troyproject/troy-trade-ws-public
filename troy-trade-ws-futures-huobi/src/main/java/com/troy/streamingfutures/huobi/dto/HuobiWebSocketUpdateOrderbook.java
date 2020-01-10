package com.troy.streamingfutures.huobi.dto;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 最新价
 */
public class HuobiWebSocketUpdateOrderbook extends HuobiFuturesOrderbookTransaction {

    public HuobiWebSocketUpdateOrderbook(HuobiFuturesDepthResult huobiFuturesDepthResult) {
        super(huobiFuturesDepthResult);
    }

    @Override
    public HuobiFuturesOrderbook toHuobiFuturesOrderBook() {
        HuobiFuturesOrderbook huobiOrderbook = new HuobiFuturesOrderbook();
        huobiOrderbook.createFromLevels(this);
        return huobiOrderbook;
    }

    @Override
    public HuobiFuturesOrderbookLevel[] getAsks() {
        HuobiFuturesOrderbookLevel[] result = null;
        List<HuobiFuturesOrderbookLevel> levelList = Lists.newArrayList();
        if (huobiFuturesDepthResult != null && huobiFuturesDepthResult.getResult() != null
                && !CollectionUtils.isEmpty(huobiFuturesDepthResult.getResult().getAsks())) {
            for (BigDecimal price : huobiFuturesDepthResult.getResult().getAsks().keySet()) {
                BigDecimal amount = huobiFuturesDepthResult.getResult().getAsks().get(price);
                levelList.add(new HuobiFuturesOrderbookLevel(price, amount));
            }
            result = new HuobiFuturesOrderbookLevel[levelList.size()];
            result = levelList.toArray(result);
        }
        return result;
    }

    @Override
    public HuobiFuturesOrderbookLevel[] getBids() {
        HuobiFuturesOrderbookLevel[] result = null;
        List<HuobiFuturesOrderbookLevel> levelList = Lists.newArrayList();
        if (huobiFuturesDepthResult != null && huobiFuturesDepthResult.getResult() != null
                && !CollectionUtils.isEmpty(huobiFuturesDepthResult.getResult().getBids())) {
            for (BigDecimal price : huobiFuturesDepthResult.getResult().getBids().keySet()) {
                BigDecimal amount = huobiFuturesDepthResult.getResult().getBids().get(price);
                levelList.add(new HuobiFuturesOrderbookLevel(price, amount));
            }
            result = new HuobiFuturesOrderbookLevel[levelList.size()];
            result = levelList.toArray(result);
        }
        return result;
    }
}

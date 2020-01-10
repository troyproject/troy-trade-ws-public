package com.troy.streamingexchange.huobi.dto;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 最新价
 */
public class HuobiWebSocketUpdateOrderbook extends HuobiWebSocketOrderbookTransaction {

    public HuobiWebSocketUpdateOrderbook(HuobiDepthResult huobiDepthResult) {
        super(huobiDepthResult);
    }

    @Override
    public HuobiOrderbook toHuobiOrderBook() {
        HuobiOrderbook huobiOrderbook = new HuobiOrderbook();
        huobiOrderbook.createFromLevels(this);
        return huobiOrderbook;
    }

    @Override
    public HuobiOrderbookLevel[] getAsks() {
        HuobiOrderbookLevel[] result = null;
        List<HuobiOrderbookLevel> levelList = Lists.newArrayList();
        if (huobiDepthResult != null && huobiDepthResult.getResult() != null
                && !CollectionUtils.isEmpty(huobiDepthResult.getResult().getAsks())) {
            for (BigDecimal price : huobiDepthResult.getResult().getAsks().keySet()) {
                BigDecimal amount = huobiDepthResult.getResult().getAsks().get(price);
                levelList.add(new HuobiOrderbookLevel(price, amount));
            }
            result = new HuobiOrderbookLevel[levelList.size()];
            result = levelList.toArray(result);
        }
        return result;
    }

    @Override
    public HuobiOrderbookLevel[] getBids() {
        HuobiOrderbookLevel[] result = null;
        List<HuobiOrderbookLevel> levelList = Lists.newArrayList();
        if (huobiDepthResult != null && huobiDepthResult.getResult() != null
                && !CollectionUtils.isEmpty(huobiDepthResult.getResult().getBids())) {
            for (BigDecimal price : huobiDepthResult.getResult().getBids().keySet()) {
                BigDecimal amount = huobiDepthResult.getResult().getBids().get(price);
                levelList.add(new HuobiOrderbookLevel(price, amount));
            }
            result = new HuobiOrderbookLevel[levelList.size()];
            result = levelList.toArray(result);

        }
        return result;
    }
}

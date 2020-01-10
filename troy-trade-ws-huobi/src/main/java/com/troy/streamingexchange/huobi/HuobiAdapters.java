package com.troy.streamingexchange.huobi;

import com.google.common.collect.Lists;
import com.troy.streamingexchange.huobi.dto.HuobiOrderbook;
import com.troy.streamingexchange.huobi.dto.HuobiOrderbookLevel;
import com.troy.streamingexchange.huobi.dto.HuobiTicker;
import com.troy.streamingexchange.huobi.dto.HuobiTrade;
import com.troy.trade.ws.dto.*;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.enums.OrderTypeEnum;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * HuobiAdapters
 *
 * @author liuxiaocheng
 * @date 2018/7/11
 */
public class HuobiAdapters {
    /**
     * 最新市场成交最大返回给前端条数
     */
    private final static int MAX_TRADE_SIZE = 25;
    /**
     * 买卖挂单最大返回给前端条数
     */
    private final static int MAX_DEPTH_SIZE = 30;

    /**
     * 买卖挂单
     * 1.设置序号
     * 2.截取
     *
     * @param huobiOrderbook
     * @param currencyPair
     * @return
     */
    public static OrderBook adaptOrderBook(HuobiOrderbook huobiOrderbook, CurrencyPair currencyPair) {
        List<LimitOrder> asks = Lists.newArrayList();
        List<LimitOrder> bids = Lists.newArrayList();
        int i = 0;
        int j = 0;
        if (huobiOrderbook != null) {
            //卖盘 原始返回 低-高
            BigDecimal askCumulativeAmount = BigDecimal.ZERO;
            if (!CollectionUtils.isEmpty(huobiOrderbook.getAsks())) {
                for (Map.Entry<BigDecimal, HuobiOrderbookLevel> entry : huobiOrderbook.getAsks().entrySet()) {
                    i++;
                    askCumulativeAmount = entry.getValue().getAmount().add(askCumulativeAmount);
                    asks.add(new LimitOrder(OrderTypeEnum.ASK, entry.getValue().getAmount(), askCumulativeAmount, currencyPair, String.valueOf(i), new Date(), entry.getValue().getPrice()));
                }
                //适应前端
                if (huobiOrderbook.getAsks().size() > MAX_DEPTH_SIZE) {
                    asks = asks.subList(0, MAX_DEPTH_SIZE);
                }

                Collections.reverse(asks);
            }
            //买盘 原始返回 高-低
            BigDecimal bidCumulativeAmount = BigDecimal.ZERO;
            if (!CollectionUtils.isEmpty(huobiOrderbook.getBids())) {
                for (Map.Entry<BigDecimal, HuobiOrderbookLevel> entry : huobiOrderbook.getBids().entrySet()) {
                    j++;
                    bidCumulativeAmount = entry.getValue().getAmount().add(bidCumulativeAmount);
                    bids.add(new LimitOrder(OrderTypeEnum.BID, entry.getValue().getAmount(), bidCumulativeAmount, currencyPair, String.valueOf(j), new Date(), entry.getValue().getPrice()));
                }
                if (huobiOrderbook.getBids().size() > MAX_DEPTH_SIZE) {
                    bids = bids.subList(0, MAX_DEPTH_SIZE);
                }
            }
        }
        return new OrderBook(new Date(), asks, bids);
    }

    public static Ticker adaptTicker(HuobiTicker huobiTicker, CurrencyPair currencyPair) {
        Ticker.Builder builder = new Ticker.Builder();
        builder.open(huobiTicker.getOpen());
        builder.last(huobiTicker.getClose());
        builder.high(huobiTicker.getHigh());
        builder.low(huobiTicker.getLow());
        builder.quoteVolume(huobiTicker.getVol());
        builder.volume(huobiTicker.getAmount());
        builder.currencyPair(currencyPair);
        return builder.build();
    }

    /**
     * 深度
     *
     * @param huobiTrades
     * @param currencyPair
     * @return
     */
    public static Trades adaptTrades(List<HuobiTrade> huobiTrades, CurrencyPair currencyPair) {
        List<Trade> tradeList = new ArrayList<>();
        long lastTradeId = 0;
        for (HuobiTrade trade : huobiTrades) {
            Trade adaptedTrade = adaptTrade(trade, currencyPair);
            tradeList.add(adaptedTrade);
        }
        if (!CollectionUtils.isEmpty(tradeList) && tradeList.size() > MAX_TRADE_SIZE) {
            tradeList = tradeList.subList(0, MAX_TRADE_SIZE);
        }
        return new Trades(tradeList, lastTradeId, Trades.TradeSortType.SortByTimestamp);
    }

    private static Trade adaptTrade(HuobiTrade huobiTrade, CurrencyPair currencyPair) {
        OrderTypeEnum orderType = null;
        String direction = huobiTrade.getDirection();
        if ("buy".equals(direction)) {
            orderType = OrderTypeEnum.BID;
        } else if ("sell".equals(direction)) {
            orderType = OrderTypeEnum.ASK;
        }
        Trade trade = new Trade(orderType, new BigDecimal(huobiTrade.getAmount()), currencyPair, new BigDecimal(huobiTrade.getPrice()), new Date(huobiTrade.getTs()), huobiTrade.getId());
        return trade;
    }


}

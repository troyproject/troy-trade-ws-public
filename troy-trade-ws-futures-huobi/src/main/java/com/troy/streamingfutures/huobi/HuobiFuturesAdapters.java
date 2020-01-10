package com.troy.streamingfutures.huobi;

import com.google.common.collect.Lists;
import com.troy.streamingfutures.huobi.dto.HuobiFuturesOrderbook;
import com.troy.streamingfutures.huobi.dto.HuobiFuturesOrderbookLevel;
import com.troy.streamingfutures.huobi.dto.HuobiFuturesTicker;
import com.troy.streamingfutures.huobi.dto.HuobiFuturesTrade;
import com.troy.trade.ws.dto.*;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.enums.OrderTypeEnum;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * HuobiFuturesAdapters
 *
 * @author liuxiaocheng
 * @date 2018/7/11
 */
public class HuobiFuturesAdapters {
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
     * @param huobiFuturesOrderbook
     * @param currencyPair
     * @return
     */
    public static OrderBook adaptOrderBook(HuobiFuturesOrderbook huobiFuturesOrderbook, CurrencyPair currencyPair) {
        List<LimitOrder> asks = Lists.newArrayList();
        List<LimitOrder> bids = Lists.newArrayList();
        int i = 0;
        int j = 0;
        if (huobiFuturesOrderbook != null) {
            //卖盘 原始返回 低-高
            BigDecimal askCumulativeAmount = BigDecimal.ZERO;
            if (!CollectionUtils.isEmpty(huobiFuturesOrderbook.getAsks())) {
                for (Map.Entry<BigDecimal, HuobiFuturesOrderbookLevel> entry : huobiFuturesOrderbook.getAsks().entrySet()) {
                    i++;
                    askCumulativeAmount = entry.getValue().getAmount().add(askCumulativeAmount);
                    asks.add(new LimitOrder(OrderTypeEnum.ASK, entry.getValue().getAmount(), askCumulativeAmount, currencyPair, String.valueOf(i), new Date(), entry.getValue().getPrice()));
                }
                //适应前端
                if (huobiFuturesOrderbook.getAsks().size() > MAX_DEPTH_SIZE) {
                    asks = asks.subList(0, MAX_DEPTH_SIZE);
                }

                Collections.reverse(asks);
            }
            //买盘 原始返回 高-低
            BigDecimal bidCumulativeAmount = BigDecimal.ZERO;
            if (!CollectionUtils.isEmpty(huobiFuturesOrderbook.getBids())) {
                for (Map.Entry<BigDecimal, HuobiFuturesOrderbookLevel> entry : huobiFuturesOrderbook.getBids().entrySet()) {
                    j++;
                    bidCumulativeAmount = entry.getValue().getAmount().add(bidCumulativeAmount);
                    bids.add(new LimitOrder(OrderTypeEnum.BID, entry.getValue().getAmount(), bidCumulativeAmount, currencyPair, String.valueOf(j), new Date(), entry.getValue().getPrice()));
                }
                if (huobiFuturesOrderbook.getBids().size() > MAX_DEPTH_SIZE) {
                    bids = bids.subList(0, MAX_DEPTH_SIZE);
                }
            }
        }
        return new OrderBook(new Date(), asks, bids);
    }

    public static Ticker adaptTicker(HuobiFuturesTicker huobiFuturesTicker, CurrencyPair currencyPair) {
        Ticker.Builder builder = new Ticker.Builder();
        builder.open(huobiFuturesTicker.getOpen());
        builder.last(huobiFuturesTicker.getClose());
        builder.high(huobiFuturesTicker.getHigh());
        builder.low(huobiFuturesTicker.getLow());
        builder.quoteVolume(huobiFuturesTicker.getVol());
        builder.volume(huobiFuturesTicker.getAmount());
        builder.currencyPair(currencyPair);
        return builder.build();
    }

    /**
     * 深度
     *
     * @param huobiFuturesTrades
     * @param currencyPair
     * @return
     */
    public static Trades adaptTrades(List<HuobiFuturesTrade> huobiFuturesTrades, CurrencyPair currencyPair) {
        List<Trade> tradeList = new ArrayList<>();
        long lastTradeId = 0;
        for (HuobiFuturesTrade trade : huobiFuturesTrades) {
            Trade adaptedTrade = adaptTrade(trade, currencyPair);
            tradeList.add(adaptedTrade);
        }
        if (!CollectionUtils.isEmpty(tradeList) && tradeList.size() > MAX_TRADE_SIZE) {
            tradeList = tradeList.subList(0, MAX_TRADE_SIZE);
        }
        return new Trades(tradeList, lastTradeId, Trades.TradeSortType.SortByTimestamp);
    }

    private static Trade adaptTrade(HuobiFuturesTrade huobiFuturesTrade, CurrencyPair currencyPair) {
        OrderTypeEnum orderType = null;
        String direction = huobiFuturesTrade.getDirection();
        if ("buy".equals(direction)) {
            orderType = OrderTypeEnum.BID;
        } else if ("sell".equals(direction)) {
            orderType = OrderTypeEnum.ASK;
        }
        Trade trade = new Trade(orderType, new BigDecimal(huobiFuturesTrade.getAmount()), currencyPair,
                new BigDecimal(huobiFuturesTrade.getPrice()),
                new Date(huobiFuturesTrade.getTs()), huobiFuturesTrade.getId());
        return trade;
    }


}

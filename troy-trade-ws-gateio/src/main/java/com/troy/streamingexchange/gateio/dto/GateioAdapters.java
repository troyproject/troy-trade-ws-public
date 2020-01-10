package com.troy.streamingexchange.gateio.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.troy.streamingexchange.gateio.dto.marketdata.GateioTicker;
import com.troy.streamingexchange.gateio.enums.GateioOrderTypeEnum;
import com.troy.trade.ws.dto.*;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.enums.OrderTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.troy.streamingexchange.gateio.GateioStreamingMarketDataServiceImpl.adaptTrade;

/**
 * Adapters
 *
 * @author liuxiaocheng
 * @date 2018/7/4
 */
@Slf4j
public class GateioAdapters {
    /**
     * 最新市场成交最大返回给前端条数
     */
    private final static int MAX_TRADE_SIZE = 25;


    /**
     * gateio盘口更新通知解析
     *
     * @param json
     * @return
     */
    public static GateioWebSocketOrderBookTransaction gateioWebSocketOrderBookTransaction(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("params");
        String resutl = jsonArray.get(0).toString();
        String symbol = jsonArray.get(2).toString();
        JSONObject orders = jsonArray.getJSONObject(1);

        JSONArray askJSONArray = orders.getJSONArray("asks");
        JSONArray bidJSONArray = orders.getJSONArray("bids");
        List<GateioPublicOrder> askOrders = Lists.newArrayList();
        List<GateioPublicOrder> bidOrders = Lists.newArrayList();

        if (askJSONArray != null && askJSONArray.toArray().length > 0) {
            for (Object item : askJSONArray.toArray()) {
                JSONArray itemJson = (JSONArray) item;
                askOrders.add(new GateioPublicOrder(itemJson.getBigDecimal(0), itemJson.getBigDecimal(1)));
            }
        }

        if (bidJSONArray != null && bidJSONArray.toArray().length > 0) {
            for (Object item : bidJSONArray.toArray()) {
                JSONArray itemJson = (JSONArray) item;
                bidOrders.add(new GateioPublicOrder(itemJson.getBigDecimal(0), itemJson.getBigDecimal(1)));
            }
        }
        return new GateioWebSocketOrderBookTransaction("depth.update", new GateioWebSocketOrderBookParams(symbol, askOrders, bidOrders, resutl));
    }

    /**
     * gateio 委托更新通知解析
     *
     * @param s
     * @return
     */
    public static GateioWebSocketOpenOrderTranscation toGateioWebSocketOrderTransaction(String s) {

        List<GateioOrderUpdate> orders = Lists.newArrayList();

        JSONObject jsonObject = JSONObject.parseObject(s);
        String method = jsonObject.getString("method");
        JSONArray jsonArray = jsonObject.getJSONArray("params");

        int step = 2;
        if (jsonArray != null && jsonArray.toArray().length > 0) {
            for (int i = 0; i < jsonArray.toArray().length; i = i + step) {
                GateioOrderUpdate gateioOrderUpdate = new GateioOrderUpdate();
                Integer eventType;
                if (i % 2 == 0) {
                    eventType = jsonArray.getInteger(i);
                    gateioOrderUpdate.setEventType(eventType);
                }
                JSONObject orderUpdate = (JSONObject) jsonArray.get(i + 1);
                gateioOrderUpdate.setOrderId(orderUpdate.getString("id"));
                //交易对
                String[] market = orderUpdate.getString("market").split("_");
                gateioOrderUpdate.setCurrencyPair(new CurrencyPair(market[0], market[1]));
                gateioOrderUpdate.setOrderType(orderUpdate.getString("orderType"));
                String type = orderUpdate.getString("type");
                if (!Strings.isNullOrEmpty(type)) {
                    if ("sell".equals(type)) {
                        gateioOrderUpdate.setType(OrderTypeEnum.ASK.toString());
                    }
                    if ("buy".equals(type)) {
                        gateioOrderUpdate.setType(OrderTypeEnum.BID.toString());
                    }
                }
                gateioOrderUpdate.setCtime(orderUpdate.getLong("ctime"));
                gateioOrderUpdate.setPrice(orderUpdate.getBigDecimal("price"));
                gateioOrderUpdate.setAmount(orderUpdate.getBigDecimal("amount"));
                gateioOrderUpdate.setLeft(orderUpdate.getBigDecimal("left"));
                gateioOrderUpdate.setFilledAmount(orderUpdate.getBigDecimal("filledAmount"));
                gateioOrderUpdate.setFilledTotal(orderUpdate.getBigDecimal("filledTotal"));
                gateioOrderUpdate.setDealFee(orderUpdate.getBigDecimal("dealFee"));
                orders.add(gateioOrderUpdate);
            }
        }
        return new GateioWebSocketOpenOrderTranscation(method, new GateioWebSocketOpenOrderParams(null, orders));
    }

    /**
     * gateio 实时成交解析
     *
     * @param json
     * @return
     */
    public static GateioWebSocketTradesTransaction toGateioWebSocketTradesTransaction(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("params");
        String symbol = jsonArray.get(0).toString();
        JSONArray trades = jsonArray.getJSONArray(1);
        String js = JSONObject.toJSONString(trades);
        List<GateioPublicTrade> publicTrades = JSONObject.parseArray(js, GateioPublicTrade.class);
        return new GateioWebSocketTradesTransaction("trades.update", new GateioWebSocketTradeParams(symbol, publicTrades));
    }

    /**
     * 市场最新成交转换
     *
     * @param gateioPublicTrades
     * @param currencyPair
     * @return
     */
    public static Trades adaptTrades(List<GateioPublicTrade> gateioPublicTrades, CurrencyPair currencyPair) {
        List<Trade> tradeList = new ArrayList<>();
        long lastTradeId = 0;
        for (GateioPublicTrade trade : gateioPublicTrades) {
            String tradeIdString = trade.getTradeId();
            if (!tradeIdString.isEmpty()) {
                long tradeId = Long.valueOf(tradeIdString);
                if (tradeId > lastTradeId) {
                    lastTradeId = tradeId;
                }
            }
            Trade adaptedTrade = adaptTrade(trade, currencyPair);
            tradeList.add(adaptedTrade);
        }
        if (!CollectionUtils.isEmpty(tradeList) && tradeList.size() > MAX_TRADE_SIZE) {
            tradeList = tradeList.subList(0, MAX_TRADE_SIZE);
        }
        return new Trades(tradeList, lastTradeId, Trades.TradeSortType.SortByID);

    }

    /**
     * 买卖挂单转换
     *
     * @param orderBook
     * @param currencyPair
     * @return
     */
    public static OrderBook adaptOrderBook(GateioWebSocketOrderBook orderBook, CurrencyPair currencyPair) {
        List<LimitOrder> asks = new ArrayList<>();
        List<LimitOrder> bids = new ArrayList<>();
        int i = 0;
        //卖盘原始返回 价格:低-高
        BigDecimal askCumulativeAmount = BigDecimal.ZERO;
        for (GateioPublicOrder item : orderBook.getAsks().values()) {
            i++;
            askCumulativeAmount = item.getAmount().add(askCumulativeAmount);
            LimitOrder limitOrder = new LimitOrder(OrderTypeEnum.ASK, item.getAmount(), askCumulativeAmount, currencyPair, String.valueOf(i), null, item.getPrice());
            asks.add(limitOrder);
        }
        int j = 0;
        //买盘原始返回 价格:高-低
        BigDecimal bidCumulativeAmount = BigDecimal.ZERO;
        for (GateioPublicOrder item : orderBook.getBids().values()) {
            j++;
            bidCumulativeAmount = item.getAmount().add(bidCumulativeAmount);
            LimitOrder limitOrder = new LimitOrder(OrderTypeEnum.BID, item.getAmount(), bidCumulativeAmount, currencyPair, String.valueOf(j), null, item.getPrice());
            bids.add(limitOrder);
        }
        //适应前端显示（卖盘高-低）
        Collections.reverse(asks);
        return new OrderBook(new Date(), asks, bids);
    }

    public static CurrencyPair adaptCurrencyPair(String pair) {

        final String[] currencies = pair.toUpperCase().split("_");
        return new CurrencyPair(currencies[0], currencies[1]);
    }

    public static Ticker adaptTicker(CurrencyPair currencyPair, GateioTicker gateioTicker) {

        BigDecimal ask = gateioTicker.getLowestAsk();
        BigDecimal bid = gateioTicker.getHighestBid();
        BigDecimal last = gateioTicker.getLast();
        BigDecimal low = gateioTicker.getLow24hr();
        BigDecimal high = gateioTicker.getHigh24hr();
        // Looks like gate.io vocabulary is inverted...
        BigDecimal baseVolume = gateioTicker.getQuoteVolume();
        BigDecimal quoteVolume = gateioTicker.getBaseVolume();

        return new Ticker.Builder()
                .currencyPair(currencyPair)
                .ask(ask)
                .bid(bid)
                .last(last)
                .low(low)
                .high(high)
                .volume(baseVolume)
                .quoteVolume(quoteVolume)
                .build();
    }

    public static OrderTypeEnum adaptOrderType(GateioOrderTypeEnum cryptoTradeOrderType) {

        return (cryptoTradeOrderType.equals(GateioOrderTypeEnum.BUY)) ? OrderTypeEnum.BID : OrderTypeEnum.ASK;
    }

//    public static Trade adaptTrade(
//            GateioPublicTrade trade, CurrencyPair currencyPair) {
//
//        OrderTypeEnum orderType = adaptOrderType(trade.getType());
//        Date timestamp = DateUtils.fromMillisUtc(trade.getDate() * 1000);
//
//        return new Trade(
//                orderType,
//                trade.getAmount(),
//                currencyPair,
//                trade.getPrice(),
//                timestamp,
//                trade.getTradeId());
//    }
//
//    public static Trades adaptTrades(GateioTradeHistory tradeHistory, CurrencyPair currencyPair) {
//
//        List<Trade> tradeList = new ArrayList<>();
//        long lastTradeId = 0;
//        for (GateioTradeHistory.GateioPublicTrade trade : tradeHistory.getTrades()) {
//            String tradeIdString = trade.getTradeId();
//            if (!tradeIdString.isEmpty()) {
//                long tradeId = Long.valueOf(tradeIdString);
//                if (tradeId > lastTradeId) {
//                    lastTradeId = tradeId;
//                }
//            }
//            Trade adaptedTrade = adaptTrade(trade, currencyPair);
//            tradeList.add(adaptedTrade);
//        }
//
//        return new Trades(tradeList, lastTradeId, Trades.TradeSortType.SortByTimestamp);
//    }

//    public static List<LimitOrder> adaptOrders(
//            List<GateioPublicOrder> orders, CurrencyPair currencyPair, OrderTypeEnum orderType) {
//
//        List<LimitOrder> limitOrders = new ArrayList<>();
//
//        for (GateioPublicOrder bterOrder : orders) {
//            limitOrders.add(adaptOrder(bterOrder, currencyPair, orderType));
//        }
//
//        return limitOrders;
//    }
//
//    public static LimitOrder adaptOrder(
//           GateioPublicOrder order, CurrencyPair currencyPair, OrderTypeEnum orderType) {
//
//        return new LimitOrder(orderType, order.getAmount(), currencyPair, "", null, order.getPrice());
//    }
}

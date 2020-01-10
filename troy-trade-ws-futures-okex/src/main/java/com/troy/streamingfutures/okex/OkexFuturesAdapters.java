package com.troy.streamingfutures.okex;


import com.troy.commons.utils.DateUtils;
import com.troy.streamingfutures.okex.dto.OkexFuturesTicker;
import com.troy.streamingfutures.okex.dto.marketdata.OkexFuturesDepth;
import com.troy.streamingfutures.okex.dto.marketdata.OkexFuturesTrade;
import com.troy.trade.ws.dto.*;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.enums.OrderStatusEnum;
import com.troy.trade.ws.enums.OrderTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class OkexFuturesAdapters {

    private OkexFuturesAdapters() {
    }

    public static String adaptSymbol(CurrencyPair currencyPair) {
        return (currencyPair.baseSymbol + "_" + currencyPair.counterSymbol).toLowerCase();
    }

    public static CurrencyPair adaptSymbol(String symbol) {
        String[] currencies = symbol.toUpperCase().split("_");
        return new CurrencyPair(currencies[0], currencies[1]);
    }

    public static Ticker adaptTicker(OkexFuturesTicker tickerResponse, CurrencyPair currencyPair) {
        return (new Ticker.Builder()).currencyPair(currencyPair).high(tickerResponse.getHigh_24h()).low(tickerResponse.getLow_24h()).last(tickerResponse.getLast()).volume(tickerResponse.getBase_volume_24h()).quoteVolume(tickerResponse.getQuote_volume_24h()).timestamp(new Date()).build();
    }

    public static OrderBook adaptOrderBook(OkexFuturesDepth depth, CurrencyPair currencyPair) {
        Stream<LimitOrder> asks = adaptLimitOrders(OrderTypeEnum.ASK, depth.getAsks(), depth.getTimestamp(), currencyPair).sorted();
        Stream<LimitOrder> bids = adaptLimitOrders(OrderTypeEnum.BID, depth.getBids(), depth.getTimestamp(), currencyPair).sorted();
        return new OrderBook(depth.getTimestamp(), asks.collect(Collectors.toList()), bids.collect(Collectors.toList()));
    }

    public static Trades adaptTrades(List<OkexFuturesTrade> trades, CurrencyPair currencyPair) {
        List<Trade> tradeList = new ArrayList(trades.size());
        List<OkexFuturesTrade> var3 = trades;
        int var4 = trades.size();

        for(int var5 = 0; var5 < var4; ++var5) {
            OkexFuturesTrade trade = var3.get(var5);
            tradeList.add(adaptTrade(trade, currencyPair));
        }

        return new Trades(tradeList, 0, Trades.TradeSortType.SortByTimestamp);
    }

    private static Stream<LimitOrder> adaptLimitOrders(OrderTypeEnum type, BigDecimal[][] list, Date timestamp, CurrencyPair currencyPair) {
        return Arrays.stream(list).map((data) -> {
            return adaptLimitOrder(type, data, currencyPair, (String)null, timestamp);
        });
    }

    private static LimitOrder adaptLimitOrder(OrderTypeEnum type, BigDecimal[] data, CurrencyPair currencyPair, String id, Date timestamp) {
        return new LimitOrder(type, data[1], currencyPair, id, timestamp, data[0]);
    }

    private static Trade adaptTrade(OkexFuturesTrade trade, CurrencyPair currencyPair) {
        Date timestamp = null;
        try{
            String okexTimeStamp = trade.getTimestamp();
            if(StringUtils.isNotBlank(okexTimeStamp)){
                if(okexTimeStamp.contains(".")){
                    timestamp = DateUtils.parse(trade.getTimestamp(),DateUtils.FORMAT_DATE_TIME_ISO8601);
                    timestamp = DateUtils.addHours(timestamp,8);
                }else{
                    timestamp = DateUtils.parse(trade.getTimestamp(),DateUtils.FORMAT_DATE_TIME_ISO8601SS);
                    timestamp = DateUtils.addHours(timestamp,8);
                }
            }else{
                timestamp = new Date();
            }
        }catch (Throwable throwable){
            log.error("OkexFuturesAdapters中做Trade转换失败，异常信息：",throwable);
        }
        return new Trade(trade.getSide().toUpperCase().equals("BUY") ? OrderTypeEnum.BID : OrderTypeEnum.ASK, new BigDecimal(trade.getQty()), currencyPair, new BigDecimal(trade.getPrice()), timestamp, "" + trade.getTradeId());
    }

    public static OrderTypeEnum adaptOrderType(String type) {
        byte var2 = -1;
        switch(type.hashCode()) {
            case -1043410923:
                if (type.equals("buy_market")) {
                    var2 = 1;
                }
                break;
            case 49:
                if (type.equals("1")) {
                    var2 = 4;
                }
                break;
            case 50:
                if (type.equals("2")) {
                    var2 = 5;
                }
                break;
            case 51:
                if (type.equals("3")) {
                    var2 = 6;
                }
                break;
            case 52:
                if (type.equals("4")) {
                    var2 = 7;
                }
                break;
            case 97926:
                if (type.equals("buy")) {
                    var2 = 0;
                }
                break;
            case 3526482:
                if (type.equals("sell")) {
                    var2 = 2;
                }
                break;
            case 76562889:
                if (type.equals("sell_market")) {
                    var2 = 3;
                }
        }

        switch(var2) {
            case 0:
                return OrderTypeEnum.BID;
            case 1:
                return OrderTypeEnum.BID;
            case 2:
                return OrderTypeEnum.ASK;
            case 3:
                return OrderTypeEnum.ASK;
            case 4:
                return OrderTypeEnum.BID;
            case 5:
                return OrderTypeEnum.ASK;
            case 6:
                return OrderTypeEnum.EXIT_ASK;
            case 7:
                return OrderTypeEnum.EXIT_BID;
            default:
                return null;
        }
    }

    public static OrderStatusEnum adaptOrderStatus(int status) {
        switch(status) {
            case -1:
                return OrderStatusEnum.CANCELED;
            case 0:
                return OrderStatusEnum.NEW;
            case 1:
                return OrderStatusEnum.PARTIALLY_FILLED;
            case 2:
                return OrderStatusEnum.FILLED;
            case 3:
            default:
                return null;
            case 4:
                return OrderStatusEnum.PENDING_CANCEL;
        }
    }

    private static Date adaptDate(long date) {
        return DateUtils.fromMillisUtc(date);
    }
}


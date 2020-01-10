package com.troy.streamingexchange.okex;

import com.troy.commons.utils.DateUtils;
import com.troy.streamingexchange.okex.dto.OkexTicker;
import com.troy.streamingexchange.okex.dto.marketdata.OkexDepth;
import com.troy.streamingexchange.okex.dto.marketdata.OkexTrade;
import com.troy.trade.ws.dto.*;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.enums.OrderStatusEnum;
import com.troy.trade.ws.enums.OrderTypeEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class OkexAdapters {
//    private static final Balance zeroUsdBalance;

    private OkexAdapters() {
    }

    public static String adaptSymbol(CurrencyPair currencyPair) {
        return (currencyPair.baseSymbol + "_" + currencyPair.counterSymbol).toLowerCase();
    }

    public static CurrencyPair adaptSymbol(String symbol) {
        String[] currencies = symbol.toUpperCase().split("_");
        return new CurrencyPair(currencies[0], currencies[1]);
    }

//    public static Ticker adaptTicker(OkCoinTickerResponse tickerResponse, CurrencyPair currencyPair) {
//        Date date = adaptDate(tickerResponse.getDate());
//        return (new Ticker.Builder()).currencyPair(currencyPair).high(tickerResponse.getTicker().getHigh()).low(tickerResponse.getTicker().getLow()).bid(tickerResponse.getTicker().getBuy()).ask(tickerResponse.getTicker().getSell()).last(tickerResponse.getTicker().getLast()).volume(tickerResponse.getTicker().getVol()).timestamp(date).build();
//    }

    public static Ticker adaptTicker(OkexTicker tickerResponse, CurrencyPair currencyPair) {
        return (new Ticker.Builder()).currencyPair(currencyPair).high(tickerResponse.getHigh_24h()).low(tickerResponse.getLow_24h()).last(tickerResponse.getLast()).volume(tickerResponse.getBase_volume_24h()).quoteVolume(tickerResponse.getQuote_volume_24h()).timestamp(new Date()).build();
    }

    public static OrderBook adaptOrderBook(OkexDepth depth, CurrencyPair currencyPair) {
        Stream<LimitOrder> asks = adaptLimitOrders(OrderTypeEnum.ASK, depth.getAsks(), depth.getTimestamp(), currencyPair).sorted();
        Stream<LimitOrder> bids = adaptLimitOrders(OrderTypeEnum.BID, depth.getBids(), depth.getTimestamp(), currencyPair).sorted();
        return new OrderBook(depth.getTimestamp(), asks.collect(Collectors.toList()), bids.collect(Collectors.toList()));
    }

    public static Trades adaptTrades(List<OkexTrade> trades, CurrencyPair currencyPair) {
        List<Trade> tradeList = new ArrayList(trades.size());
        List<OkexTrade> var3 = trades;
        int var4 = trades.size();

        for(int var5 = 0; var5 < var4; ++var5) {
            OkexTrade trade = var3.get(var5);
            tradeList.add(adaptTrade(trade, currencyPair));
        }

        return new Trades(tradeList, 0, Trades.TradeSortType.SortByTimestamp);
    }

//    public static AccountInfo adaptAccountInfo(OkCoinUserInfo userInfo) {
//        OkCoinFunds funds = userInfo.getInfo().getFunds();
//        Map<String, org.knowm.xchange.dto.account.Balance.Builder> builders = new TreeMap();
//        Iterator var3 = funds.getFree().entrySet().iterator();
//
//        Entry borrowed;
//        while(var3.hasNext()) {
//            borrowed = (Entry)var3.next();
//            builders.put(borrowed.getKey(), (new org.knowm.xchange.dto.account.Balance.Builder()).currency(Currency.getInstance((String)borrowed.getKey())).available((BigDecimal)borrowed.getValue()));
//        }
//
//        org.knowm.xchange.dto.account.Balance.Builder builder;
//        for(var3 = funds.getFreezed().entrySet().iterator(); var3.hasNext(); builders.put(borrowed.getKey(), builder.frozen((BigDecimal)borrowed.getValue()))) {
//            borrowed = (Entry)var3.next();
//            builder = (org.knowm.xchange.dto.account.Balance.Builder)builders.get(borrowed.getKey());
//            if (builder == null) {
//                builder = (new org.knowm.xchange.dto.account.Balance.Builder()).currency(Currency.getInstance((String)borrowed.getKey()));
//            }
//        }
//
//        for(var3 = funds.getBorrow().entrySet().iterator(); var3.hasNext(); builders.put(borrowed.getKey(), builder.borrowed((BigDecimal)borrowed.getValue()))) {
//            borrowed = (Entry)var3.next();
//            builder = (org.knowm.xchange.dto.account.Balance.Builder)builders.get(borrowed.getKey());
//            if (builder == null) {
//                builder = (new org.knowm.xchange.dto.account.Balance.Builder()).currency(Currency.getInstance((String)borrowed.getKey()));
//            }
//        }
//
//        List<Balance> wallet = new ArrayList(builders.size());
//        Iterator var7 = builders.values().iterator();
//
//        while(var7.hasNext()) {
//            builder = (org.knowm.xchange.dto.account.Balance.Builder)var7.next();
//            wallet.add(builder.build());
//        }

//        return new AccountInfo(new Wallet[]{new Wallet(null)});
//        return null;
//    }

//    public static AccountInfo adaptAccountInfoFutures(OkCoinFuturesUserInfoCross futureUserInfo) {
//        OkCoinFuturesInfoCross info = futureUserInfo.getInfo();
//        OkcoinFuturesFundsCross btcFunds = info.getBtcFunds();
//        OkcoinFuturesFundsCross ltcFunds = info.getLtcFunds();
//        OkcoinFuturesFundsCross bchFunds = info.getBchFunds();
//        Balance btcBalance = new Balance(Currency.BTC, btcFunds.getAccountRights());
//        Balance ltcBalance = new Balance(Currency.LTC, ltcFunds.getAccountRights());
//        Balance bchBalance = new Balance(Currency.BCH, bchFunds.getAccountRights());
//        return new AccountInfo(new Wallet[]{new Wallet(new Balance[]{zeroUsdBalance, btcBalance, ltcBalance, bchBalance})});
//    }

//    public static OpenOrders adaptOpenOrders(List<OkCoinOrderResult> orderResults) {
//        List<LimitOrder> openOrders = new ArrayList();
//        Iterator var2 = orderResults.iterator();
//
//        while(var2.hasNext()) {
//            OkCoinOrderResult orderResult = (OkCoinOrderResult)var2.next();
//            OkCoinOrder[] orders = orderResult.getOrders();
//            OkCoinOrder[] var5 = orders;
//            int var6 = orders.length;
//
//            for(int var7 = 0; var7 < var6; ++var7) {
//                OkCoinOrder singleOrder = var5[var7];
//                openOrders.add(adaptOpenOrder(singleOrder));
//            }
//        }
//
//        return new OpenOrders(openOrders);
//    }

//    public static OpenOrders adaptOpenOrdersFutures(List<OkCoinFuturesOrderResult> orderResults) {
//        List<LimitOrder> openOrders = new ArrayList();
//        Iterator var2 = orderResults.iterator();
//
//        while(var2.hasNext()) {
//            OkCoinFuturesOrderResult orderResult = (OkCoinFuturesOrderResult)var2.next();
//            OkCoinFuturesOrder[] orders = orderResult.getOrders();
//            OkCoinFuturesOrder[] var5 = orders;
//            int var6 = orders.length;
//
//            for(int var7 = 0; var7 < var6; ++var7) {
//                OkCoinFuturesOrder singleOrder = var5[var7];
//                openOrders.add(adaptOpenOrderFutures(singleOrder));
//            }
//        }
//
//        return new OpenOrders(openOrders);
//    }

//    public static UserTrades adaptTrades(OkCoinOrderResult orderResult) {
//        List<UserTrade> trades = new ArrayList(orderResult.getOrders().length);
//
//        for(int i = 0; i < orderResult.getOrders().length; ++i) {
//            OkCoinOrder order = orderResult.getOrders()[i];
//            if (!order.getDealAmount().equals(BigDecimal.ZERO)) {
//                trades.add(adaptTrade(order));
//            }
//        }
//
//        return new UserTrades(trades, TradeSortType.SortByTimestamp);
//    }
//
//    public static UserTrades adaptTradesFutures(OkCoinFuturesOrderResult orderResult) {
//        List<UserTrade> trades = new ArrayList(orderResult.getOrders().length);
//
//        for(int i = 0; i < orderResult.getOrders().length; ++i) {
//            OkCoinFuturesOrder order = orderResult.getOrders()[i];
//            if (!order.getDealAmount().equals(BigDecimal.ZERO)) {
//                trades.add(adaptTradeFutures(order));
//            }
//        }
//
//        return new UserTrades(trades, TradeSortType.SortByTimestamp);
//    }

    private static Stream<LimitOrder> adaptLimitOrders(OrderTypeEnum type, BigDecimal[][] list, Date timestamp, CurrencyPair currencyPair) {
        return Arrays.stream(list).map((data) -> {
            return adaptLimitOrder(type, data, currencyPair, (String)null, timestamp);
        });
    }

    private static LimitOrder adaptLimitOrder(OrderTypeEnum type, BigDecimal[] data, CurrencyPair currencyPair, String id, Date timestamp) {
        return new LimitOrder(type, data[1], currencyPair, id, timestamp, data[0]);
    }

    private static Trade adaptTrade(OkexTrade trade, CurrencyPair currencyPair) {
        return new Trade(trade.getSide().toUpperCase().equals("BUY") ? OrderTypeEnum.BID : OrderTypeEnum.ASK, new BigDecimal(trade.getSize()), currencyPair, new BigDecimal(trade.getPrice()), new Date(), "" + trade.getTradeId());
    }

//    private static LimitOrder adaptOpenOrder(OkCoinOrder order) {
//        return new LimitOrder(adaptOrderType(order.getType()), order.getAmount(), adaptSymbol(order.getSymbol()), String.valueOf(order.getOrderId()), order.getCreateDate(), order.getPrice(), order.getAveragePrice(), order.getDealAmount(), (BigDecimal)null, adaptOrderStatus(order.getStatus()));
//    }
//
//    public static LimitOrder adaptOpenOrderFutures(OkCoinFuturesOrder order) {
//        return new LimitOrder(adaptOrderType(order.getType()), order.getAmount(), adaptSymbol(order.getSymbol()), String.valueOf(order.getOrderId()), order.getCreatedDate(), order.getPrice(), order.getAvgPrice(), order.getDealAmount(), order.getFee(), adaptOrderStatus(order.getStatus()));
//    }

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

//    private static UserTrade adaptTrade(OkCoinOrder order) {
//        String orderId;
//        String tradeId = orderId = String.valueOf(order.getOrderId());
//        return new UserTrade(adaptOrderType(order.getType()), order.getDealAmount(), adaptSymbol(order.getSymbol()), order.getAveragePrice(), order.getCreateDate(), tradeId, orderId, (BigDecimal)null, (Currency)null);
//    }
//
//    private static UserTrade adaptTradeFutures(OkCoinFuturesOrder order) {
//        return new UserTrade(adaptOrderType(order.getType()), order.getDealAmount(), adaptSymbol(order.getSymbol()), order.getPrice(), order.getCreatedDate(), (String)null, String.valueOf(order.getOrderId()), (BigDecimal)null, (Currency)null);
//    }
//    public static UserTrades adaptTradeHistory(OkCoinFuturesTradeHistoryResult[] okCoinFuturesTradeHistoryResult) {
//        List<UserTrade> trades = new ArrayList();
//        long lastTradeId = 0L;
//        OkCoinFuturesTradeHistoryResult[] var4 = okCoinFuturesTradeHistoryResult;
//        int var5 = okCoinFuturesTradeHistoryResult.length;
//
//        for(int var6 = 0; var6 < var5; ++var6) {
//            OkCoinFuturesTradeHistoryResult okCoinFuturesTrade = var4[var6];
//            OrderType orderType = okCoinFuturesTrade.getType().equals(TransactionType.sell) ? OrderType.ASK : OrderType.BID;
//            BigDecimal originalAmount = BigDecimal.valueOf(okCoinFuturesTrade.getAmount());
//            BigDecimal price = okCoinFuturesTrade.getPrice();
//            Date timestamp = new Date(okCoinFuturesTrade.getTimestamp());
//            long transactionId = okCoinFuturesTrade.getId();
//            if (transactionId > lastTradeId) {
//                lastTradeId = transactionId;
//            }
//
//            String tradeId = String.valueOf(transactionId);
//            String orderId = String.valueOf(okCoinFuturesTrade.getId());
//            CurrencyPair currencyPair = CurrencyPair.BTC_USD;
//            BigDecimal feeAmont = BigDecimal.ZERO;
//            UserTrade trade = new UserTrade(orderType, originalAmount, currencyPair, price, timestamp, tradeId, orderId, feeAmont, Currency.getInstance(currencyPair.counter.getCurrencyCode()));
//            trades.add(trade);
//        }
//
//        return new UserTrades(trades, lastTradeId, TradeSortType.SortByID);
//    }

    private static Date adaptDate(long date) {
        return DateUtils.fromMillisUtc(date);
    }

//    public static List<FundingRecord> adaptFundingHistory(OkCoinAccountRecords[] okCoinAccountRecordsList) {
//        List<FundingRecord> fundingRecords = new ArrayList();
//        if (okCoinAccountRecordsList != null && okCoinAccountRecordsList.length > 0) {
//            OkCoinAccountRecords depositRecord = okCoinAccountRecordsList[0];
//            int var6;
//            if (depositRecord != null) {
//                Currency depositCurrency = Currency.getInstance(depositRecord.getSymbol());
//                OkCoinRecords[] var4 = depositRecord.getRecords();
//                int var5 = var4.length;
//
//                for(var6 = 0; var6 < var5; ++var6) {
//                    OkCoinRecords okCoinRecordEntry = var4[var6];
//                    Status status = null;
//                    if (okCoinRecordEntry.getStatus() != null) {
//                        RechargeStatus rechargeStatus = RechargeStatus.fromInt(okCoinRecordEntry.getStatus());
//                        if (rechargeStatus != null) {
//                            status = Status.resolveStatus(rechargeStatus.getStatus());
//                        }
//                    }
//
//                    fundingRecords.add(new FundingRecord(okCoinRecordEntry.getAddress(), adaptDate(okCoinRecordEntry.getDate()), depositCurrency, okCoinRecordEntry.getAmount(), (String)null, (String)null, Type.DEPOSIT, status, (BigDecimal)null, okCoinRecordEntry.getFee(), (String)null));
//                }
//            }
//
//            OkCoinAccountRecords withdrawalRecord = okCoinAccountRecordsList[1];
//            if (withdrawalRecord != null) {
//                Currency withdrawalCurrency = Currency.getInstance(withdrawalRecord.getSymbol());
//                OkCoinRecords[] var13 = withdrawalRecord.getRecords();
//                var6 = var13.length;
//
//                for(int var14 = 0; var14 < var6; ++var14) {
//                    OkCoinRecords okCoinRecordEntry = var13[var14];
//                    Status status = null;
//                    if (okCoinRecordEntry.getStatus() != null) {
//                        WithdrawalStatus withdrawalStatus = WithdrawalStatus.fromInt(okCoinRecordEntry.getStatus());
//                        if (withdrawalStatus != null) {
//                            status = Status.resolveStatus(withdrawalStatus.getStatus());
//                        }
//                    }
//
//                    fundingRecords.add(new FundingRecord(okCoinRecordEntry.getAddress(), adaptDate(okCoinRecordEntry.getDate()), withdrawalCurrency, okCoinRecordEntry.getAmount(), (String)null, (String)null, Type.WITHDRAWAL, status, (BigDecimal)null, okCoinRecordEntry.getFee(), (String)null));
//                }
//            }
//        }
//
//        return fundingRecords;
//    }

//    static {
//        zeroUsdBalance = new Balance(Currency.USD, BigDecimal.ZERO);
//    }
}


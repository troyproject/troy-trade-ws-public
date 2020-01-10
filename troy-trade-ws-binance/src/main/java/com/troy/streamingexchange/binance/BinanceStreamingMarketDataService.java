package com.troy.streamingexchange.binance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.troy.streamingexchange.binance.dto.*;
import com.troy.streamingexchange.binance.dto.marketdata.BinanceOrderbook;
import com.troy.streamingexchange.binance.dto.marketdata.BinanceTicker24h;
import com.troy.trade.ws.dto.*;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.enums.OrderTypeEnum;
import com.troy.trade.ws.exceptions.ExchangeException;
import com.troy.trade.ws.streamingexchange.core.ProductSubscription;
import com.troy.trade.ws.streamingexchange.core.StreamingMarketDataService;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.troy.streamingexchange.binance.dto.BaseBinanceWebSocketTransaction.BinanceWebSocketTypes.*;

public class BinanceStreamingMarketDataService implements StreamingMarketDataService {
    private static final Logger LOG = LoggerFactory.getLogger(BinanceStreamingMarketDataService.class);

    /**
     * 买卖挂单最大返回给前端条数
     */
    private final static int MAX_DEPTH_SIZE = 30;

    private final BinanceStreamingService service;
    private final Map<CurrencyPair, OrderBook> orderbooks = new HashMap<>();

    private final Map<CurrencyPair, Observable<BinanceTicker24h>> tickerSubscriptions = new HashMap<>();
    private final Map<CurrencyPair, Observable<OrderBook>> orderbookSubscriptions = new HashMap<>();
    private final Map<CurrencyPair, Observable<BinanceRawTrade>> tradeSubscriptions = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();


    public BinanceStreamingMarketDataService(BinanceStreamingService service) {
        this.service = service;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        if (!service.getProductSubscription().getOrderBook().contains(currencyPair)) {
            throw new UnsupportedOperationException("Binance exchange only supports up front subscriptions - subscribe at connect time");
        }
        Observable<OrderBook> orderBookbservable = orderbookSubscriptions.get(currencyPair);

        return orderBookbservable.map(orderBook -> {
            OrderBook orderBookNew = adaptOrderBook(orderBook, currencyPair);
            return orderBookNew;
        });
    }

    private OrderBook adaptOrderBook(OrderBook orderBook, CurrencyPair currencyPair) {
        List<LimitOrder> asks = new ArrayList<>();
        List<LimitOrder> bids = new ArrayList<>();
        int i = 0;
        //卖盘原始返回 价格:低-高
        BigDecimal askCumulativeAmount = BigDecimal.ZERO;
        for (LimitOrder item : orderBook.getAsks()) {
            i++;
            askCumulativeAmount = item.getOriginalAmount().add(askCumulativeAmount);
            LimitOrder limitOrder = new LimitOrder(OrderTypeEnum.ASK, item.getOriginalAmount(), askCumulativeAmount, currencyPair, String.valueOf(i), null, item.getLimitPrice());
            asks.add(limitOrder);
        }
        if (orderBook.getAsks().size() > MAX_DEPTH_SIZE) {
            asks = asks.subList(0, MAX_DEPTH_SIZE);
        }
        //适应前端显示（卖盘高-低）
        Collections.reverse(asks);
        int j = 0;
        //买盘原始返回 价格:高-低
        BigDecimal bidCumulativeAmount = BigDecimal.ZERO;
        for (LimitOrder item : orderBook.getBids()) {
            j++;
            bidCumulativeAmount = item.getOriginalAmount().add(bidCumulativeAmount);
            LimitOrder limitOrder = new LimitOrder(OrderTypeEnum.BID, item.getOriginalAmount(), bidCumulativeAmount, currencyPair, String.valueOf(j), null, item.getLimitPrice());
            bids.add(limitOrder);
        }
        if (orderBook.getBids().size() > MAX_DEPTH_SIZE) {
            bids = bids.subList(0, MAX_DEPTH_SIZE);
        }
        return new OrderBook(new Date(), asks, bids);
    }

    public Observable<BinanceTicker24h> getRawTicker(CurrencyPair currencyPair, Object... args) {
        if (!service.getProductSubscription().getTicker().contains(currencyPair)) {
            throw new UnsupportedOperationException("Binance exchange only supports up front subscriptions - subscribe at connect time");
        }
        return tickerSubscriptions.get(currencyPair);
    }

    public Observable<BinanceRawTrade> getRawTrades(CurrencyPair currencyPair, Object... args) {
        if (!service.getProductSubscription().getTrades().contains(currencyPair)) {
            throw new UnsupportedOperationException("Binance exchange only supports up front subscriptions - subscribe at connect time");
        }
        return tradeSubscriptions.get(currencyPair);
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        return getRawTicker(currencyPair)
                .map(BinanceTicker24h::toTicker);
    }

    @Override
    public Observable<List<Trade>> getTradesOnce(CurrencyPair currencyPair, Object... args) {
        return null;
    }

    @Override
    public Observable<List<Trade>> getTrades(CurrencyPair currencyPair, Object... args) {
        return getRawTrades(currencyPair)
                .map(rawTrade -> {
                    List<Trade> trades = new ArrayList<>();
                    Trade trade = new Trade(
                            BinanceAdapters.convertType(!rawTrade.isBuyerMarketMaker()),
                            rawTrade.getQuantity(),
                            currencyPair,
                            rawTrade.getPrice(),
                            new Date(rawTrade.getTimestamp()),
                            String.valueOf(rawTrade.getTradeId())
                    );
                    trades.add(trade);
                    return trades;
                });
    }

    private static String channelFromCurrency(CurrencyPair currencyPair, String subscriptionType) {
        String currency = String.join("", currencyPair.toString().split("/")).toLowerCase();
        return currency + "@" + subscriptionType;
    }

    /**
     * Registers subsriptions with the streaming service for the given products.
     * <p>
     * As we receive messages as soon as the connection is open, we need to register subscribers to handle these before the
     * first messages arrive.
     */
    public void openSubscriptions(ProductSubscription productSubscription) {
        productSubscription.getTicker()
                .forEach(currencyPair ->
                        tickerSubscriptions.put(currencyPair, triggerObservableBody(rawTickerStream(currencyPair).share())));
        if(productSubscription.getIsRobot()){
            productSubscription.getOrderBook()
                    .forEach(currencyPair ->
                            orderbookSubscriptions.put(currencyPair, triggerObservableBody(orderBookStream20(currencyPair).share())));
        }else{
            productSubscription.getOrderBook()
                    .forEach(currencyPair ->
                            orderbookSubscriptions.put(currencyPair, triggerObservableBody(orderBookStream20100ms(currencyPair).share())));
        }
        productSubscription.getTrades()
                .forEach(currencyPair ->
                        tradeSubscriptions.put(currencyPair, triggerObservableBody(rawTradeStream(currencyPair).share())));
    }

    private Observable<BinanceTicker24h> rawTickerStream(CurrencyPair currencyPair) {
        return service.subscribeChannel(channelFromCurrency(currencyPair, "ticker"))
                .map((JsonNode s) -> tickerTransaction(s.toString()))
                .filter(transaction ->
                        transaction.getData().getCurrencyPair().equals(currencyPair) &&
                                transaction.getData().getEventType() == TICKER_24_HR)
                .map(transaction -> transaction.getData().getTicker());
    }

    private Observable<OrderBook> orderBookStream20100ms(CurrencyPair currencyPair) {
        return service.subscribeChannel(channelFromCurrency(currencyPair, "depth20"))
                .map((JsonNode s) -> depth20Transaction(s.toString()))
                .map(transaction -> {
                    PartialDepthBinanceWebSocketTransaction depth = transaction.getData();

                    OrderBook currentOrderBook = new OrderBook(null, new ArrayList<>(), new ArrayList<>());
                    orderbooks.put(currencyPair,currentOrderBook);

                    BinanceOrderbook ob = depth.getOrderBook();
                    ob.bids.forEach((key, value) -> currentOrderBook.update(new OrderBookUpdate(
                            OrderTypeEnum.BID,
                            null,
                            currencyPair,
                            key,
                            null,
                            value)));
                    ob.asks.forEach((key, value) -> currentOrderBook.update(new OrderBookUpdate(
                            OrderTypeEnum.ASK,
                            null,
                            currencyPair,
                            key,
                            null,
                            value)));
                    return currentOrderBook;
                });
    }

    private Observable<OrderBook> orderBookStream(CurrencyPair currencyPair) {
        return service.subscribeChannel(channelFromCurrency(currencyPair, "depth"))
                .map((JsonNode s) -> depthTransaction(s.toString()))
                .filter(transaction ->
                        transaction.getData().getCurrencyPair().equals(currencyPair) &&
                                transaction.getData().getEventType() == DEPTH_UPDATE)
                .map(transaction -> {
                    DepthBinanceWebSocketTransaction depth = transaction.getData();

//                    OrderBook currentOrderBook = orderbooks.computeIfAbsent(currencyPair, orderBook ->
//                            new OrderBook(null, new ArrayList<>(), new ArrayList<>()));

                    BinanceOrderbook ob = depth.getOrderBook();
                    List<LimitOrder> asksList = Lists.newArrayList();
                    List<LimitOrder> bidsList = Lists.newArrayList();
                    ob.bids.forEach((key, value) -> bidsList.add(new LimitOrder(
                            OrderTypeEnum.BID,
                            value,
                            currencyPair,
                            null,
                            depth.getEventTime(),
                            key
                    )));
                    ob.asks.forEach((key, value) -> asksList.add(new LimitOrder(
                            OrderTypeEnum.ASK,
                            value,
                            currencyPair,
                            null,
                            depth.getEventTime(),
                            key)));
                    OrderBook orderBook = new OrderBook(new Date(),asksList,bidsList);
//                    ob.bids.forEach((key, value) -> currentOrderBook.update(new OrderBookUpdate(
//                            OrderType.BID,
//                            value,
//                            currencyPair,
//                            key,
//                            depth.getEventTime(),
//                            value)));
//                    ob.asks.forEach((key, value) -> currentOrderBook.update(new OrderBookUpdate(
//                            OrderType.ASK,
//                            value,
//                            currencyPair,
//                            key,
//                            depth.getEventTime(),
//                            value)));
                    return orderBook;
                });
    }

    private Observable<OrderBook> orderBookStream20(CurrencyPair currencyPair) {
        return service.subscribeChannel(channelFromCurrency(currencyPair, "depth20"))
                .map((JsonNode s) -> depth20Transaction(s.toString()))
                .map(transaction -> {
                    PartialDepthBinanceWebSocketTransaction depth = transaction.getData();

                    OrderBook currentOrderBook = new OrderBook(null, new ArrayList<>(), new ArrayList<>());
                    orderbooks.put(currencyPair,currentOrderBook);

                    BinanceOrderbook ob = depth.getOrderBook();
                    ob.bids.forEach((key, value) -> currentOrderBook.update(new OrderBookUpdate(
                            OrderTypeEnum.BID,
                            null,
                            currencyPair,
                            key,
                            null,
                            value)));
                    ob.asks.forEach((key, value) -> currentOrderBook.update(new OrderBookUpdate(
                            OrderTypeEnum.ASK,
                            null,
                            currencyPair,
                            key,
                            null,
                            value)));
                    return currentOrderBook;
                });
    }

    private Observable<BinanceRawTrade> rawTradeStream(CurrencyPair currencyPair) {
        return service.subscribeChannel(channelFromCurrency(currencyPair, "trade"))
                .map((JsonNode s) -> tradeTransaction(s.toString()))
                .filter(transaction ->
                        transaction.getData().getCurrencyPair().equals(currencyPair) &&
                                transaction.getData().getEventType() == TRADE
                )
                .map(transaction -> transaction.getData().getRawTrade());
    }

    /**
     * Force observable to execute its body, this way we get `BinanceStreamingService` to register the observables emitter
     * ready for our message arrivals.
     */
    private <T> Observable<T> triggerObservableBody(Observable<T> observable) {
        Consumer<T> NOOP = whatever -> {
        };
        observable.subscribe(NOOP);
        return observable;
    }

    private BinanceWebsocketTransaction<TickerBinanceWebsocketTransaction> tickerTransaction(String s) {
        try {
            return mapper.readValue(s, new TypeReference<BinanceWebsocketTransaction<TickerBinanceWebsocketTransaction>>() {
            });
        } catch (IOException e) {
            throw new ExchangeException("Unable to parse ticker transaction", e);
        }
    }

    private BinanceWebsocketTransaction<DepthBinanceWebSocketTransaction> depthTransaction(String s) {
        try {
            return mapper.readValue(s, new TypeReference<BinanceWebsocketTransaction<DepthBinanceWebSocketTransaction>>() {
            });
        } catch (IOException e) {
            throw new ExchangeException("Unable to parse order book transaction", e);
        }
    }

    private BinanceWebsocketTransaction<PartialDepthBinanceWebSocketTransaction> depth20Transaction(String s) {
        try {
            return mapper.readValue(s, new TypeReference<BinanceWebsocketTransaction<PartialDepthBinanceWebSocketTransaction>>() {
            });
        } catch (IOException e) {
            throw new ExchangeException("Unable to parse order book transaction", e);
        }
    }

    private BinanceWebsocketTransaction<TradeBinanceWebsocketTransaction> tradeTransaction(String s) {
        try {
            return mapper.readValue(s, new TypeReference<BinanceWebsocketTransaction<TradeBinanceWebsocketTransaction>>() {
            });
        } catch (IOException e) {
            throw new ExchangeException("Unable to parse trade transaction", e);
        }
    }

}

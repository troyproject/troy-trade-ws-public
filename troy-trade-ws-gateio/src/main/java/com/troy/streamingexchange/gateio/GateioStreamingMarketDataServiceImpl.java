package com.troy.streamingexchange.gateio;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.troy.commons.utils.DateUtils;
import com.troy.streamingexchange.CommonUtil;
import com.troy.streamingexchange.gateio.dto.*;
import com.troy.trade.ws.dto.OrderBook;
import com.troy.trade.ws.dto.Ticker;
import com.troy.trade.ws.dto.Trade;
import com.troy.trade.ws.dto.Trades;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.enums.OrderTypeEnum;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @author public
 */
public class GateioStreamingMarketDataServiceImpl implements GateioStreamingMarketDataService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GateioStreamingService service;

    public GateioStreamingMarketDataServiceImpl(GateioStreamingService service) {
        this.service = service;
    }

    private Map<CurrencyPair, GateioWebSocketOrderBook> orderbooks = new HashMap<>();

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        String pair = currencyPair.baseSymbol + "_" + currencyPair.counterSymbol;
        String channelName = getChannelName("depth", pair);

        Observable<JsonNode> jsonNodeObservable = service.subscribeChannel(channelName, args[0],args[1]);

        int length = args == null?0:args.length;
        final Boolean[] fullData = new Boolean[]{true};
        if(length==3){
            fullData[0] = (Boolean) args[2];
        }
        return jsonNodeObservable
                .map(s -> GateioAdapters.gateioWebSocketOrderBookTransaction(s.toString()))
                .map(s -> {
                    /**
                     *  true: is complete result
                     *  false: is last updated result
                     */
                    if ("true".equals(s.getParams().getResult())) {
                        orderbooks = new HashMap<>();
                    }

                    GateioWebSocketOrderBook gateioOrderBook = s.toGateioOrderBook(orderbooks.getOrDefault(currencyPair, null));
                    orderbooks.put(currencyPair, gateioOrderBook);

                    if(!fullData[0]){
                        GateioWebSocketOrderBook orderbook = new GateioWebSocketOrderBook(s,true);
                        return GateioAdapters.adaptOrderBook(orderbook, currencyPair);
                    }else{
                        return GateioAdapters.adaptOrderBook(gateioOrderBook, currencyPair);
                    }
                });
    }


    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        String pair = currencyPair.baseSymbol + "_" + currencyPair.counterSymbol;
        String channelName = getChannelName("ticker", pair);

        return service.subscribeChannel(channelName)
                .map(s -> JSONObject.parseObject(s.toString(), GateioWebSocketTickerTransaction.class))
                .map(s -> GateioAdapters.adaptTicker(currencyPair, s.toGateioTicker()));
    }

    @Override
    public Observable<List<Ticker>> getKline(CurrencyPair currencyPair, KlineInterval klineInterval, Object... args) {
        String interval = String.valueOf(klineInterval.getMillis().intValue() / 1000);
        String pair = currencyPair.baseSymbol + "_" + currencyPair.counterSymbol;
        String channelName = getChannelName("kline", pair);
        return service.subscribeChannel(channelName, interval)
                .map(s -> JSONObject.parseObject(s.toString(), GateioWebSocketTickerTransaction.class))
                .map(s -> s.toTickers());
    }

    @Override
    public Observable<List<Trade>> getTrades(CurrencyPair currencyPair, Object... args) {
        String pair = currencyPair.baseSymbol + "_" + currencyPair.counterSymbol;
        String channelName = getChannelName("trades", pair);

        return service.subscribeChannel(channelName)
                .map(s -> GateioAdapters.toGateioWebSocketTradesTransaction(s.toString()))
                .map(GateioWebSocketTradesTransaction::getParams)
                .filter(Objects::nonNull)
                .map(GateioWebSocketTradeParams::getData)
                .map(s -> {
                    Trades adaptedTrades = GateioAdapters.adaptTrades(s, currencyPair);
                    return adaptedTrades.getTrades();
                });
    }

    @Override
    public Observable<List<Trade>> getTradesOnce(CurrencyPair currencyPair, Object... args) {
        return null;
    }

//    @Override
//    public Observable<List<GateioOrderUpdate>> getOpenOrders(CurrencyPair currencyPair, Object... args) {
//
//        String pair = currencyPair.baseSymbol + "_" + currencyPair.counterSymbol;
//        String channelName = getChannelName("order", pair);
//
//        Observable<JsonNode> jsonNodeObservable = service.subscribeChannel(channelName, args);
//        return jsonNodeObservable
//                .map(s -> Adapters.toGateioWebSocketOrderTransaction(s.toString()))
//                .map(s -> s.getParams().getGateioOrderUpdates());
//    }

    @Override
    public void ping(Object... args) {
        try {
            String pingMessage = this.service.getSubscribeMessage(GateioWebsocketTypes.SERVER_PING.getSerializedValue(), args);
            this.service.sendMessage(pingMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Observable<Object> authenticated(String apiKey, String secretKeyBase64) {

        Long nonce = CommonUtil.getNonce();
        String signature = CommonUtil.getSignature(secretKeyBase64, nonce);
        String channelName = getChannelName(GateioWebsocketTypes.SERVER_SIGN.getSerializedValue(), apiKey);
        Observable<JsonNode> jsonNodeObservable = service.subscribeChannel(channelName, new Object[]{apiKey, signature, nonce});
        return jsonNodeObservable.map(s -> Boolean.TRUE);
    }

    public static Trade adaptTrade(
            GateioPublicTrade trade, CurrencyPair currencyPair) {

        OrderTypeEnum orderType = GateioAdapters.adaptOrderType(trade.getType());
        Date timestamp = DateUtils.fromMillisUtc(trade.getTimestamp() * 1000);

        return new Trade(
                orderType,
                trade.getAmount(),
                currencyPair,
                trade.getPrice(),
                timestamp,
                trade.getTradeId());
    }

    private String getChannelName(String entityName, String pair) {
        return entityName + "-" + pair;
    }

    public Map<CurrencyPair, GateioWebSocketOrderBook> getOrderbooks() {
        return orderbooks;
    }
}

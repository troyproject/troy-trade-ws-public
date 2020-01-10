package com.troy.streamingexchange.bitfinex;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.streamingexchange.bitfinex.dto.*;
import com.troy.trade.ws.dto.OrderBook;
import com.troy.trade.ws.dto.Ticker;
import com.troy.trade.ws.dto.Trade;
import com.troy.trade.ws.dto.Trades;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.streamingexchange.core.StreamingMarketDataService;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitfinexStreamingMarketDataService implements StreamingMarketDataService {
    private static final Logger LOG = LoggerFactory.getLogger(BitfinexStreamingMarketDataService.class);

    private final BitfinexStreamingService service;

    private Map<CurrencyPair, BitfinexOrderbook> orderbooks = new HashMap<>();

    public BitfinexStreamingMarketDataService(BitfinexStreamingService service) {
        this.service = service;
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {

        String channelName = "book";
        final String depth = args.length > 0 ? args[0].toString() : "100";
        final boolean isRobot = args.length >= 2 ? Boolean.valueOf(args[1].toString()) : false;
        String pair = currencyPair.baseSymbol + currencyPair.counterSymbol;
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Observable<BitfinexWebSocketOrderbookTransaction> subscribedChannel = service.subscribeChannel(channelName,
                new Object[]{pair, "P0", "F0", depth})
                .map(s -> {
                    if (s.get(1).get(0).isArray()) return mapper.readValue(s.toString(),
                            BitfinexWebSocketSnapshotOrderbook.class);
                    else return mapper.readValue(s.toString(), BitfinexWebSocketUpdateOrderbook.class);
                });

        return subscribedChannel
                .map(s -> {
                    BitfinexOrderbook bitfinexOrderbook = s.toBitfinexOrderBook(orderbooks.getOrDefault(currencyPair,
                            null),isRobot);
                    orderbooks.put(currencyPair, bitfinexOrderbook);
                    return BitfinexAdapters.adaptOrderBook(BitfinexAdapters.adaptOrderBook(bitfinexOrderbook.toBitfinexDepth(), currencyPair));
                });
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        String channelName = "ticker";

        String pair = currencyPair.baseSymbol + currencyPair.counterSymbol;
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Observable<BitfinexWebSocketTickerTransaction> subscribedChannel = service.subscribeChannel(channelName,
                new Object[]{pair})
                .map(s -> mapper.readValue(s.toString(), BitfinexWebSocketTickerTransaction.class));

        return subscribedChannel
                .map(s -> BitfinexAdapters.adaptTicker(s.toBitfinexTicker(), currencyPair));
    }

    @Override
    public Observable<List<Trade>> getTrades(com.troy.trade.ws.dto.currency.CurrencyPair currencyPair, Object... args) {
        String channelName = "trades";
        final String tradeType = args.length > 0 ? args[0].toString() : "te";

        String pair = currencyPair.baseSymbol + currencyPair.counterSymbol;
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Observable<BitfinexWebSocketTradesTransaction> subscribedChannel = service.subscribeChannel(channelName,
                new Object[]{pair})
                .filter(s -> s.get(1).asText().equals(tradeType))
                .map(s -> {
                    if (s.get(1).asText().equals("te") || s.get(1).asText().equals("tu")) {
                        return mapper.readValue(s.toString(), BitfinexWebsocketUpdateTrade.class);
                    } else return mapper.readValue(s.toString(), BitfinexWebSocketSnapshotTrades.class);
                });

        return subscribedChannel
                .map(s -> {
                    Trades adaptedTrades = BitfinexAdapters.adaptTrades(s.toBitfinexTrades(), currencyPair);
                    return adaptedTrades.getTrades();
                });
    }

    @Override
    public Observable<List<Trade>> getTradesOnce(CurrencyPair currencyPair, Object... args) {
        return null;
    }
}

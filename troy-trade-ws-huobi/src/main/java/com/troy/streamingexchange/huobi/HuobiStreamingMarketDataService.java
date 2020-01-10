package com.troy.streamingexchange.huobi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.streamingexchange.huobi.dto.*;
import com.troy.trade.ws.dto.OrderBook;
import com.troy.trade.ws.dto.Ticker;
import com.troy.trade.ws.dto.Trade;
import com.troy.trade.ws.dto.Trades;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.streamingexchange.core.StreamingMarketDataService;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.troy.streamingexchange.huobi.HuobiAdapters.adaptOrderBook;
import static com.troy.streamingexchange.huobi.HuobiAdapters.adaptTicker;


public class HuobiStreamingMarketDataService implements StreamingMarketDataService {
    private static final Logger LOG = LoggerFactory.getLogger(HuobiStreamingMarketDataService.class);

    private final HuobiStreamingService service;

    private Map<CurrencyPair, HuobiOrderbook> orderbooks = new HashMap<>();

    public HuobiStreamingMarketDataService(HuobiStreamingService service) {
        this.service = service;
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        String channelName = TopicType.TOPIC_MARKET_DEPTH;
        //默认step0
        final String depthType = args.length > 0 ? args[0].toString() : "step0";
        String pair = (currencyPair.baseSymbol + currencyPair.counterSymbol).toLowerCase();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Observable<HuobiDepthResult> subscribedChannel = service.subscribeChannel(channelName,
                new Object[]{pair, depthType})
                .map(s -> mapper.readValue(s.toString(), HuobiDepthResult.class));

        return subscribedChannel
                .map(s -> {
                    HuobiWebSocketUpdateOrderbook webSocketUpdateOrderbook = new HuobiWebSocketUpdateOrderbook(s);
                    HuobiOrderbook huobiOrderbook = webSocketUpdateOrderbook.toHuobiOrderBook();
                    return adaptOrderBook(huobiOrderbook, currencyPair);
                });
    }

    /**
     * {"ch":"market.meeteth.detail","ts":1532513767292,"tick":{"amount":2195931.3553354633,"open":5.0E-5,"close":5.044E-5,"high":5.169E-5,"id":13681634264,"count":8820,"low":4.664E-5,"version":13681634264,"vol":108.3005203452}}
     *
     * @param currencyPair Currency pair of the ticker
     * @param args
     * @return
     */
    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        String channelName = TopicType.TOPIC_MARKET_DETAIL;

        String pair = (currencyPair.baseSymbol + currencyPair.counterSymbol).toLowerCase();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Observable<HuobiTickerResult> subscribedChannel = service.subscribeChannel(channelName,
                new Object[]{pair})
                .map(s -> mapper.readValue(s.toString(), HuobiTickerResult.class));

        return subscribedChannel
                .map(s -> adaptTicker(s.getResult(), currencyPair));

    }

    @Override
    public Observable<List<Trade>> getTrades(CurrencyPair currencyPair, Object... args) {
        String channelName = TopicType.TOPIC_MARKET_TRADE;
        String pair = (currencyPair.baseSymbol + currencyPair.counterSymbol).toLowerCase();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Observable<HuobiTradeResult> subscribedChannel = service.subscribeChannel(channelName,
                new Object[]{pair})
                .map(s -> mapper.readValue(s.toString(), HuobiTradeResult.class));

        return subscribedChannel
                .map(s -> {
                    HuobiWebSocketUpdateOrder huobiWebSocketUpdateOrder = new HuobiWebSocketUpdateOrder(s);
                    Trades adaptedTrades = HuobiAdapters.adaptTrades(huobiWebSocketUpdateOrder.toHuobiTrades(), currencyPair);
                    return adaptedTrades.getTrades();
                });
    }

    @Override
    public Observable<List<Trade>> getTradesOnce(CurrencyPair currencyPair, Object... args) {
        String channelName = TopicType.TOPIC_MARKET_TRADE_REQ;
        String pair = (currencyPair.baseSymbol + currencyPair.counterSymbol).toLowerCase();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Observable<HuobiTradeRquestResult> subscribedChannel = service.subscribeChannel(channelName,
                new Object[]{pair})
                .map(s -> mapper.readValue(s.toString(), HuobiTradeRquestResult.class));

        return subscribedChannel
                .map(s -> {
                    Trades adaptedTrades = HuobiAdapters.adaptTrades(s.getData(), currencyPair);
                    return adaptedTrades.getTrades();
                });
    }
}

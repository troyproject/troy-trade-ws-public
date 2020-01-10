package com.troy.streamingfutures.huobi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.commons.exchange.model.enums.AliasEnum;
import com.troy.streamingfutures.huobi.dto.*;
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


public class HuobiFuturesStreamingMarketDataService implements StreamingMarketDataService {
    private static final Logger LOG = LoggerFactory.getLogger(HuobiFuturesStreamingMarketDataService.class);

    private final HuobiFuturesStreamingService service;

    private Map<CurrencyPair, HuobiFuturesOrderbook> orderbooks = new HashMap<>();

    public HuobiFuturesStreamingMarketDataService(HuobiFuturesStreamingService service) {
        this.service = service;
    }

    /**
     *
     * @return
     */
    private String getTradeSymbol(String baseName,AliasEnum alias){
        StringBuffer resultSb = new StringBuffer();
        resultSb.append(baseName.toUpperCase());
        resultSb.append("_");
        //如"BTC_CW"表示BTC当周合约，"BTC_NW"表示BTC次周合约，"BTC_CQ"表示BTC季度合约
        if(alias == AliasEnum.THIS_WEEK){
            resultSb.append("CW");
        }else if(alias == AliasEnum.NEXT_WEEK){
            resultSb.append("NW");
        }else if(alias == AliasEnum.QUARTER){
            resultSb.append("CQ");
        }
        return resultSb.toString();
    }

    /**
     *
     * @param currencyPair Currency pair of the order book
     * @param args 第一个为合约类型
     * @return
     */
    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        //xxxxx.$symbol.depth.$type
        String channelName = HuobiFuturesConstant.MARKET_DEPTH_SUB_FORMATE;

        AliasEnum aliasEnum = args.length > 0 ? (AliasEnum)args[0] : AliasEnum.THIS_WEEK;
        String pair = getTradeSymbol(currencyPair.baseSymbol,aliasEnum);

        //默认step0
        final String depthType = "step0";

        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Observable<HuobiFuturesDepthResult> subscribedChannel = service.subscribeChannel(channelName,
                new Object[]{pair, depthType})
                .map(s -> mapper.readValue(s.toString(), HuobiFuturesDepthResult.class)
                );

        return subscribedChannel
                .map(s -> {
                    HuobiWebSocketUpdateOrderbook webSocketUpdateOrderbook = new HuobiWebSocketUpdateOrderbook(s);
                    HuobiFuturesOrderbook huobiOrderbook = webSocketUpdateOrderbook.toHuobiFuturesOrderBook();
                    return HuobiFuturesAdapters.adaptOrderBook(huobiOrderbook, currencyPair);
                });
    }

    /**
     * {"ch":"market.meeteth.detail","ts":1532513767292,"tick":{"amount":2195931.3553354633,"open":5.0E-5,"close":5.044E-5,"high":5.169E-5,"id":13681634264,"count":8820,"low":4.664E-5,"version":13681634264,"vol":108.3005203452}}
     *
     * @param currencyPair Currency pair of the ticker
     * @param args 第一个为合约类型
     * @return
     */
    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        String channelName = HuobiFuturesConstant.MARKET_DETAIL_SUB_FORMATE;

        AliasEnum aliasEnum = args.length > 0 ? (AliasEnum)args[0] : AliasEnum.THIS_WEEK;
        String pair = getTradeSymbol(currencyPair.baseSymbol,aliasEnum);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Observable<HuobiFuturesTickerResult> subscribedChannel = service.subscribeChannel(channelName,
                new Object[]{pair})
                .map(s -> mapper.readValue(s.toString(), HuobiFuturesTickerResult.class));

        return subscribedChannel
                .map(s -> HuobiFuturesAdapters.adaptTicker(s.getResult(), currencyPair));

    }

    /**
     *
     * @param currencyPair Currency pair of the trades
     * @param args 第一个为合约类型
     * @return
     */
    @Override
    public Observable<List<Trade>> getTrades(CurrencyPair currencyPair, Object... args) {
        String channelName = HuobiFuturesConstant.TRADE_DETAIL_SUB_FORMATE;

        AliasEnum aliasEnum = args.length > 0 ? (AliasEnum)args[0] : AliasEnum.THIS_WEEK;
        String pair = getTradeSymbol(currencyPair.baseSymbol,aliasEnum);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Observable<HuobiFuturesTradeResult> subscribedChannel = service.subscribeChannel(channelName,
                new Object[]{pair})
                .map(s -> mapper.readValue(s.toString(), HuobiFuturesTradeResult.class));

        return subscribedChannel
                .map(s -> {
                    HuobiFuturesUpdateOrder huobiFuturesUpdateOrder = new HuobiFuturesUpdateOrder(s);
                    Trades adaptedTrades = HuobiFuturesAdapters.adaptTrades(huobiFuturesUpdateOrder.toHuobiTrades(), currencyPair);
                    return adaptedTrades.getTrades();
                });
    }

    @Override
    public Observable<List<Trade>> getTradesOnce(CurrencyPair currencyPair, Object... args) {
//        String channelName = HuobiFuturesConstant.TRADE_DETAIL_SUB_FORMATE;
//        AliasEnum aliasEnum = args.length > 0 ? (AliasEnum)args[0] : AliasEnum.THIS_WEEK;
//        String pair = getTradeSymbol(currencyPair.baseSymbol,aliasEnum);
//
//        final ObjectMapper mapper = new ObjectMapper();
//        mapperO.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//        Observable<HuobiFuturesTradeRquestResult> subscribedChannel = service.subscribeChannel(channelName,false,
//                new Object[]{pair})
//                .map(s -> mapper.readValue(s.toString(), HuobiFuturesTradeRquestResult.class));
//
//        return subscribedChannel
//                .map(s -> {
//                    Trades adaptedTrades = HuobiFuturesAdapters.adaptTrades(s.getData(), currencyPair);
//                    return adaptedTrades.getTrades();
//                });
        return null;
    }
}

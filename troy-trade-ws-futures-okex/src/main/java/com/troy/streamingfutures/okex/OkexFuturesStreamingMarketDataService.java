package com.troy.streamingfutures.okex;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.streamingfutures.okex.dto.OkexFuturesOrderbook;
import com.troy.streamingfutures.okex.dto.OkexFuturesTicker;
import com.troy.streamingfutures.okex.dto.marketdata.OkexFuturesDepth;
import com.troy.streamingfutures.okex.dto.marketdata.OkexFuturesTrade;
import com.troy.trade.ws.dto.LimitOrder;
import com.troy.trade.ws.dto.OrderBook;
import com.troy.trade.ws.dto.Ticker;
import com.troy.trade.ws.dto.Trade;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.enums.OrderTypeEnum;
import com.troy.trade.ws.streamingexchange.core.StreamingMarketDataService;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class OkexFuturesStreamingMarketDataService implements StreamingMarketDataService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OkexFuturesStreamingService service;

    /**
     * 买卖挂单最大返回给前端条数
     */
    private final static int MAX_DEPTH_SIZE = 30;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, OkexFuturesOrderbook> orderbooks = new HashMap<>();

    OkexFuturesStreamingMarketDataService(OkexFuturesStreamingService service) {
        this.service = service;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     *
     * @param currencyPair Currency pair of the order book
     * @param args 0-是否机器人、1-合约ID
     * @return
     */
    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        boolean isRobot = (boolean) args[0];
        String instrumentId = (String) args[1];
        String channel = String.format("futures/depth:%s", instrumentId);
        return service.subscribeChannel(channel)
                .map(s -> {
                    OkexFuturesOrderbook okexFuturesOrderbook;
                    JsonNode data = s.get("data").get(0);
                    if (!orderbooks.containsKey(instrumentId)) {
                        OkexFuturesDepth okexFuturesDepth = mapper.treeToValue(data, OkexFuturesDepth.class);
                        okexFuturesOrderbook = new OkexFuturesOrderbook(okexFuturesDepth);
                        orderbooks.put(instrumentId, okexFuturesOrderbook);
                    } else {
                        okexFuturesOrderbook = orderbooks.get(instrumentId);
                        if (data.has("asks")) {
                            if (data.get("asks").size() > 0) {
                                BigDecimal[][] askLevels = mapper.treeToValue(data.get("asks"), BigDecimal[][].class);
                                okexFuturesOrderbook.updateLevels(askLevels, OrderTypeEnum.ASK, isRobot);
                            }
                        }

                        if (data.has("bids")) {
                            if (data.get("bids").size() > 0) {
                                BigDecimal[][] bidLevels = mapper.treeToValue(data.get("bids"), BigDecimal[][].class);
                                okexFuturesOrderbook.updateLevels(bidLevels, OrderTypeEnum.BID, isRobot);
                            }
                        }
                    }

                    OrderBook book = OkexFuturesAdapters.adaptOrderBook(okexFuturesOrderbook.toOkexFuturesDepth(data.get("instrument_id").textValue(),data.get("checksum").textValue()), currencyPair);
                    return adaptOrderBook(book, currencyPair);
                });
    }


    /**
     * 买卖挂单
     * 1.设置序号
     * 2.截取
     *
     * @param currencyPair
     * @return
     */
    public static OrderBook adaptOrderBook(OrderBook orderBook, CurrencyPair currencyPair) {
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
    /**
     * #### spot ####
     * 1. ok_sub_spot_X_ticker   订阅行情数据
     *
     * @param currencyPair Currency pair of the ticker
     * @param args         the first arg (instrument_id 比如：BTC-USDT-191227)
     * @return
     */
    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        String instrumentId = (String) args[0];
//        futures/ticker:BTC-USD-170310
        String channel = String.format("futures/ticker:%s", instrumentId);
        return service.subscribeChannel(channel).map(s ->{
                    OkexFuturesTicker okCoinTicker = mapper.treeToValue(s.get("data").get(0), OkexFuturesTicker.class);
                    return OkexFuturesAdapters.adaptTicker(okCoinTicker, currencyPair);
                });
    }

    /**
     * #### future ####
     * 5. futures/xxxxx:BTC-USD-170310   订阅合约交易信息
     *
     * @param currencyPair Currency pair of the trades
     * @param args         the first arg (instrument_id 比如：BTC-USDT-191227)
     * @return
     */
    public Observable<List<Trade>> getTrades(CurrencyPair currencyPair, Object... args) {
        String instrumentId = (String) args[0];
        //futures/order:BTC-USD-170317
        String channel = String.format("futures/xxxxx:%s", instrumentId);
        return service.subscribeChannel(channel)
                .map(s -> {
                    JSONArray array = JSONArray.parseArray(s.get("data").toString());
                    List<OkexFuturesTrade> trades = array.toJavaList(OkexFuturesTrade.class);
                    logger.info("做实体转换后OkexFuturesTrades={}",mapper.writeValueAsString(trades));
                    return OkexFuturesAdapters.adaptTrades(trades, currencyPair).getTrades();
                });
    }

    @Override
    public Observable<List<Trade>> getTradesOnce(CurrencyPair currencyPair, Object... args) {
        return null;
    }

}

package com.troy.streamingexchange.okex;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.streamingexchange.okex.dto.OkexOrderbook;
import com.troy.streamingexchange.okex.dto.OkexTicker;
import com.troy.streamingexchange.okex.dto.marketdata.OkexDepth;
import com.troy.streamingexchange.okex.dto.marketdata.OkexTrade;
import com.troy.trade.ws.dto.LimitOrder;
import com.troy.trade.ws.dto.OrderBook;
import com.troy.trade.ws.dto.Ticker;
import com.troy.trade.ws.dto.Trade;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.enums.OrderTypeEnum;
import com.troy.trade.ws.streamingexchange.core.StreamingMarketDataService;
import io.reactivex.Observable;

import java.math.BigDecimal;
import java.util.*;

public class OkexStreamingMarketDataService implements StreamingMarketDataService {
    private final OkexStreamingService service;

    /**
     * 买卖挂单最大返回给前端条数
     */
    private final static int MAX_DEPTH_SIZE = 30;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<CurrencyPair, OkexOrderbook> orderbooks = new HashMap<>();

    public OkexStreamingMarketDataService(OkexStreamingService service) {
        this.service = service;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * #### spot ####
     * 2. ok_sub_spot_X_depth 订阅币币市场深度(200增量数据返回)
     * 3. ok_sub_spot_X_depth_Y 订阅市场深度
     * #### future ####
     * 3. ok_sub_futureusd_X_depth_Y   订阅合约市场深度(200增量数据返回)
     * 3. ok_sub_futureusd_X_depth_Y   Subscribe Contract Market Depth(Incremental)
     * 4. ok_sub_futureusd_X_depth_Y_Z   订阅合约市场深度(全量返回)
     * 4. ok_sub_futureusd_X_depth_Y_Z   Subscribe Contract Market Depth(Full)
     *
     * @param currencyPair Currency pair of the order book
     * @param args         if the first arg is means future, the next arg is amount
     * @return
     */
    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        String channel = String.format("spot/depth:%s-%s", currencyPair.baseSymbol.toUpperCase(), currencyPair.counterSymbol.toUpperCase());

        boolean isRobot = (boolean) args[0];
        return service.subscribeChannel(channel)
                .map(s -> {
                    OkexOrderbook okCoinOrderbook;
                    JsonNode data = s.get("data").get(0);
                    if (!orderbooks.containsKey(currencyPair)) {
                        OkexDepth okCoinDepth = mapper.treeToValue(data, OkexDepth.class);
                        okCoinOrderbook = new OkexOrderbook(okCoinDepth);
                        orderbooks.put(currencyPair, okCoinOrderbook);
                    } else {
                        okCoinOrderbook = orderbooks.get(currencyPair);
                        if (data.has("asks")) {
                            if (data.get("asks").size() > 0) {
                                BigDecimal[][] askLevels = mapper.treeToValue(data.get("asks"), BigDecimal[][].class);
                                okCoinOrderbook.updateLevels(askLevels, OrderTypeEnum.ASK, isRobot);
                            }
                        }

                        if (data.has("bids")) {
                            if (data.get("bids").size() > 0) {
                                BigDecimal[][] bidLevels = mapper.treeToValue(data.get("bids"), BigDecimal[][].class);
                                okCoinOrderbook.updateLevels(bidLevels, OrderTypeEnum.BID, isRobot);
                            }
                        }
                    }

                    OrderBook book = OkexAdapters.adaptOrderBook(okCoinOrderbook.toOkCoinDepth(null,data.get("instrument_id").textValue(),data.get("checksum").textValue()), currencyPair);
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
     * @param args
     * @return
     */
    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        String channel = String.format("spot/ticker:%s-%s", currencyPair.baseSymbol.toUpperCase(), currencyPair.counterSymbol.toUpperCase());

        return service.subscribeChannel(channel)
                .map(s -> {
                    // TODO: fix parsing of BigDecimal attribute val that has format: 1,625.23
                    OkexTicker okCoinTicker = mapper.treeToValue(s.get("data").get(0), OkexTicker.class);
                    return OkexAdapters.adaptTicker(okCoinTicker, currencyPair);
                });
    }

    /**
     * #### spot ####
     * 4. ok_sub_spot_X_deals   订阅成交记录
     * <p>
     * #### future ####
     * 5. ok_sub_futureusd_X_trade_Y   订阅合约交易信息
     * 5. ok_sub_futureusd_X_trade_Y   Subscribe Contract Trade Record
     *
     * @param currencyPair Currency pair of the trades
     * @param args         the first arg
     * @return
     */
    public Observable<List<Trade>> getTrades(CurrencyPair currencyPair, Object... args) {
        String channel = String.format("spot/xxxxx:%s-%s", currencyPair.baseSymbol.toUpperCase(), currencyPair.counterSymbol.toUpperCase());

        return service.subscribeChannel(channel)
                .map(s -> {
                    JSONArray array = JSONArray.parseArray(s.get("data").toString());
                    List<OkexTrade> trades = array.toJavaList(OkexTrade.class);
                    return OkexAdapters.adaptTrades(trades, currencyPair).getTrades();
//                    .flatMapIterable(Trades::getTrades)
                });
    }

    @Override
    public Observable<List<Trade>> getTradesOnce(CurrencyPair currencyPair, Object... args) {
        return null;
    }

}

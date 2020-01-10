package com.troy.trade.ws.service.streaming;

import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.redis.RedisUtil;
import com.troy.streamingexchange.gateio.GateioStreamingExchange;
import com.troy.streamingexchange.gateio.GateioStreamingMarketDataServiceImpl;
import com.troy.streamingexchange.gateio.dto.GateioAdapters;
import com.troy.streamingexchange.gateio.dto.GateioWebSocketOrderBook;
import com.troy.trade.ws.dto.OrderBook;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.factory.RestExchangeServiceFactory;
import com.troy.trade.ws.model.constant.Constant;
import com.troy.trade.ws.model.domain.StreamingExchangeDto;
import com.troy.trade.ws.model.dto.in.DepthSubscribe;
import com.troy.trade.ws.model.dto.in.RequestDto;
import com.troy.trade.ws.model.dto.in.TradeSubscribe;
import com.troy.trade.ws.model.dto.in.ValidateDto;
import com.troy.trade.ws.model.dto.out.depth.DepthResponse;
import com.troy.trade.ws.model.dto.out.trades.TradeDataResponse;
import com.troy.trade.ws.server.SessionUtil;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingExchangeFactory;
import com.troy.trade.ws.util.DepthInterval;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class GateioStreamingExchangeServiceImpl extends BaseStreamingExchangeServiceImpl  {

    @Autowired
    RestExchangeServiceFactory restExchangeServiceFactory;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public Boolean validate(ValidateDto validateDto) {
        return true;
    }

    @Override
    public void depthSubscribe(RequestDto<DepthSubscribe> depthSubscribeRequestBody) {
        try{
            DepthSubscribe depthSubscribe = depthSubscribeRequestBody.getParams();
            ExchangeCode exchCode = depthSubscribe.getExchangeCode();
            String symbol = depthSubscribe.getSymbol();//交易对名称，如：BTC/USDT

            Integer limit = depthSubscribe.getLimit();//盘口条数

            String intervalOld = depthSubscribeRequestBody.getParams().getInterval();

            String sessionId = depthSubscribe.getSessionId();

            StreamingExchange streamingExchange = SessionUtil.getStreamingExchange(sessionId);

            if (streamingExchange != null && streamingExchange.getStreamingMarketDataService() != null) {
                String orderBookRedisKey = Constant.ORDERBOOK_GATEIO_FULLDATA_REDIS_KEY.replace("{sessionId}",sessionId);
                redisUtil.set(orderBookRedisKey,System.currentTimeMillis());
                redisUtil.expire(orderBookRedisKey,2880, TimeUnit.MINUTES);

                //做全量数据推送
                toSendAllDepth(exchCode,symbol,depthSubscribe,streamingExchange);

                String intervalNew = DepthInterval.fromDepthIntervalCode(intervalOld).getDepth();
                CurrencyPair currencyPairEntity = new CurrencyPair(symbol);
                streamingExchange.getStreamingMarketDataService().getOrderBook(currencyPairEntity, new Object[]{limit, intervalNew,false}).subscribe
                        (
                                orderBook -> {
                                    Long thisTime = System.currentTimeMillis();
                                    String tempOrderBookRedisKey = Constant.ORDERBOOK_GATEIO_FULLDATA_REDIS_KEY.replace("{sessionId}",sessionId);
                                    Long preTime = (Long) redisUtil.get(tempOrderBookRedisKey);
                                    Long subtraction = thisTime-preTime;

                                    boolean isFullData = false;
                                    if(subtraction>=1500){
                                        isFullData = true;
                                        log.info(" gateio 全量数据推送exchCode:{},symbol:{},sessionId:{}",exchCode,symbol,sessionId);
                                        GateioStreamingMarketDataServiceImpl gateioStreamingMarketDataService = (GateioStreamingMarketDataServiceImpl)streamingExchange.getStreamingMarketDataService();
                                        GateioWebSocketOrderBook gateioWebSocketOrderBook = gateioStreamingMarketDataService.getOrderbooks().getOrDefault(currencyPairEntity, null);
                                        orderBook = GateioAdapters.adaptOrderBook(gateioWebSocketOrderBook, currencyPairEntity);
                                        Long temp = System.currentTimeMillis();
                                        redisUtil.set(orderBookRedisKey,temp);
                                        redisUtil.expire(orderBookRedisKey,2880, TimeUnit.MINUTES);
                                        log.info(" gateio 全量数据推送数据准备完毕 time由{}改为{}",preTime,temp);
                                        log.info(" gateio 全量数据 isFullData为{}",isFullData);
                                    }else{
                                        log.info(" gateio 增量数据 isFullData为{}",isFullData);
                                    }

                                    List<List<String>> asksList = new ArrayList<>();
                                    List<List<String>> bidsList = new ArrayList<>();
                                    orderBook.getAsks().stream().forEach(asks->{
                                        List<String> temp = new ArrayList<>();
                                        temp.add(asks.getLimitPrice().stripTrailingZeros().toPlainString());
                                        temp.add(asks.getOriginalAmount().stripTrailingZeros().toPlainString());
                                        asksList.add(temp);
                                    });

                                    orderBook.getBids().stream().forEach(bids->{
                                        List<String> temp = new ArrayList<>();
                                        temp.add(bids.getLimitPrice().stripTrailingZeros().toPlainString());
                                        temp.add(bids.getOriginalAmount().stripTrailingZeros().toPlainString());
                                        bidsList.add(temp);
                                    });
                                    log.info(" gateio 增量数据 isFullData为{}",isFullData);
                                    //String symbol, boolean fullData, List<List<String>> asks,List<List<String>> bids
                                    DepthResponse depthResponse = new DepthResponse(symbol,isFullData,asksList,bidsList);
                                    this.toSendDepth(depthSubscribe,depthResponse);

                                },
                                throwable -> log.error("ERROR in getting depth: ", throwable)
                        );
                log.info("买卖挂单订阅成功 exchCode:{},symbol:{},interval:{},limit:{}", exchCode, symbol, intervalOld, limit);
            }
        }catch (Throwable throwable){
            log.error("盘口订阅异常，异常信息：",throwable);
        }
    }

    @Override
    public void tradeSubscribe(RequestDto<TradeSubscribe> tradeSubscribeRequestBody) {
        try {
            TradeSubscribe tradeSubscribe = tradeSubscribeRequestBody.getParams();
            String sessionId = tradeSubscribe.getSessionId();
            String symbol = tradeSubscribe.getSymbol();//交易对名称，如：BTC/USDT

            StreamingExchange streamingExchange = SessionUtil.getStreamingExchange(sessionId);
            if (streamingExchange != null && streamingExchange.getStreamingMarketDataService() != null) {
                try{
                    TradeDataResponse tradeDataResponse = this.restExchangeServiceFactory.getRestExchangeService(tradeSubscribe.getExchangeCode()).trade(tradeSubscribe);
                    this.toSendTrades(tradeSubscribe,tradeDataResponse);
                }catch (Exception e){
                    log.error("调用 rest 接口查询 gateio 最新成交信息异常，异常信息：",e);
                }

                streamingExchange.getStreamingMarketDataService().getTrades(new CurrencyPair(symbol)).subscribe(
                        tradeList -> {
                            this.toSendTrades(tradeSubscribe,turnTradeToTradeDataResponse(tradeList));
                        },
                        throwable -> log.error("获取 gateio 历史成交记录信息异常，异常信息: ", throwable)
                );
            }
        }catch (Throwable throwable){
            log.error("订阅 gateio 历史成交记录信息异常，异常信息：",throwable);
        }
    }

    @Override
    public StreamingExchange getStreamingExchange(StreamingExchangeDto... args) {
        StreamingExchange streamingExchange = StreamingExchangeFactory.INSTANCE.createExchange(GateioStreamingExchange.class
                .getName());
        streamingExchange.connect().blockingAwait();
        return streamingExchange;
    }

    @Override
    public ExchangeCode getExchCode() {
        return ExchangeCode.GATEIO;
    }

    /**
     * 做买卖挂单全量数据推送
     * @param exchCode
     * @param symbol
     * @param depthSubscribe
     * @param streamingExchange
     */
    public void toSendAllDepth(ExchangeCode exchCode, String symbol,
                               DepthSubscribe depthSubscribe, StreamingExchange streamingExchange){
        String sessionId = depthSubscribe.getSessionId();
        CurrencyPair currencyPairEntity = new CurrencyPair(symbol);
        log.info(" gateio 全量数据推送exchCode:{},symbol:{},sessionId:{}",exchCode,symbol,sessionId);
        GateioStreamingMarketDataServiceImpl gateioStreamingMarketDataService = (GateioStreamingMarketDataServiceImpl)streamingExchange.getStreamingMarketDataService();

        Map<CurrencyPair, GateioWebSocketOrderBook> orderBookMap = gateioStreamingMarketDataService.getOrderbooks();
        if(null == orderBookMap || orderBookMap.isEmpty()){
            return;
        }

        GateioWebSocketOrderBook gateioWebSocketOrderBook = orderBookMap.getOrDefault(currencyPairEntity, null);
        OrderBook orderBook = GateioAdapters.adaptOrderBook(gateioWebSocketOrderBook, currencyPairEntity);

        List<List<String>> asksList = new ArrayList<>();
        List<List<String>> bidsList = new ArrayList<>();
        orderBook.getAsks().stream().forEach(asks->{
            List<String> temp = new ArrayList<>();
            temp.add(asks.getLimitPrice().stripTrailingZeros().toPlainString());
            temp.add(asks.getOriginalAmount().stripTrailingZeros().toPlainString());
            asksList.add(temp);
        });

        orderBook.getBids().stream().forEach(bids->{
            List<String> temp = new ArrayList<>();
            temp.add(bids.getLimitPrice().stripTrailingZeros().toPlainString());
            temp.add(bids.getOriginalAmount().stripTrailingZeros().toPlainString());
            bidsList.add(temp);
        });

        //String symbol, boolean fullData, List<List<String>> asks,List<List<String>> bids
        DepthResponse depthResponse = new DepthResponse(symbol,true,asksList,bidsList);
        this.toSendDepth(depthSubscribe,depthResponse);
    }
}

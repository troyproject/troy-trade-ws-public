package com.troy.trade.ws.service.streaming;

import com.alibaba.fastjson.JSONObject;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.streamingexchange.bitfinex.BitfinexStreamingExchange;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.factory.RestExchangeServiceFactory;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BitfinexStreamingExchangeServiceImpl extends BaseStreamingExchangeServiceImpl  {

    @Autowired
    RestExchangeServiceFactory restExchangeServiceFactory;

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
                //做全量数据推送
                toSendAllDepth(exchCode,symbol,depthSubscribe);

                streamingExchange.getStreamingMarketDataService().getOrderBook(new CurrencyPair(symbol),"100",false).subscribe
                        (
                            orderBook -> {
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
                                DepthResponse depthResponse = new DepthResponse(symbol,false,asksList,bidsList);
                                this.toSendDepth(depthSubscribe,depthResponse);
                                log.debug("bitfinex 盘口刷新，增量推送");
                            },
                            throwable -> log.error("ERROR in getting depth: ", throwable)
                        );
                log.info("bitfinex 盘口订阅成功 exchCode:{},symbol:{},interval:{},limit:{}", exchCode, symbol, intervalOld, limit);
            }
        }catch (Throwable throwable){
            log.error("bitfinex 盘口订阅异常，异常信息：",throwable);
        }
    }

    @Override
    public void tradeSubscribe(RequestDto<TradeSubscribe> tradeSubscribeRequestBody) {
        try {
            TradeSubscribe tradeSubscribe = tradeSubscribeRequestBody.getParams();
            String symbol = tradeSubscribe.getSymbol();//交易对名称，如：BTC/USDT

            String sessionId = tradeSubscribe.getSessionId();

            StreamingExchange streamingExchange = SessionUtil.getStreamingExchange(sessionId);
            if (streamingExchange != null && streamingExchange.getStreamingMarketDataService() != null) {
                try{
                    log.info("bitfinex最新成交:订阅bitfinex最新成交，查询列表入参："+ JSONObject.toJSONString(tradeSubscribe));
                    TradeDataResponse tradeDataResponse = this.restExchangeServiceFactory.getRestExchangeService(tradeSubscribe.getExchangeCode()).trade(tradeSubscribe);
                    this.toSendTrades(tradeSubscribe,tradeDataResponse);
                }catch (Exception e){
                    log.error("bitfinex最新成交:调用 rest 接口查询 bitfinex 最新成交信息异常，异常信息：",e);
                }

                streamingExchange.getStreamingMarketDataService().getTrades(new CurrencyPair(symbol)).subscribe(
                        tradeList -> {
                            this.toSendTrades(tradeSubscribe,turnTradeToTradeDataResponse(tradeList));
                        },
                        throwable -> log.error("bitfinex最新成交:获取 bitfinex 历史成交记录信息异常，异常信息: ", throwable)
                );
            }
        }catch (Throwable throwable){
            log.error("bitfinex最新成交:订阅 biefinex 历史成交记录信息异常，异常信息：",throwable);
        }
    }

    @Override
    public StreamingExchange getStreamingExchange(StreamingExchangeDto... args) {
        StreamingExchange bitfinexStreamingExchange = StreamingExchangeFactory.INSTANCE.createExchange(BitfinexStreamingExchange.class
                .getName());
        bitfinexStreamingExchange.connect().blockingAwait();
        return bitfinexStreamingExchange;
    }

    @Override
    public ExchangeCode getExchCode() {
        return ExchangeCode.BITFINEX;
    }
}

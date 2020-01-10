package com.troy.trade.ws.service.streaming;

import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.streamingexchange.okex.OkexStreamingExchange;
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
public class OkexStreamingExchangeServiceImpl extends BaseStreamingExchangeServiceImpl  {

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

//                final Long[] startTime = {System.currentTimeMillis()};//现在时间毫秒
//                log.debug("okex 行情全量刷新，startTime初始为"+startTime[0]);
                streamingExchange.getStreamingMarketDataService().getOrderBook(new CurrencyPair(symbol),false).subscribe
                        (
                                orderBook -> {
//                                    Long thisTime = System.currentTimeMillis();//现在时间毫秒
//                                    Long sur = (thisTime - startTime[0]);
                                    boolean isFullData = false;
//                                    if(sur>=3000) {//大于三秒重新取一次数据
//
//                                        //做全量数据推送
//                                        toSendAllDepth(exchCode,symbol,depthSubscribe);
//
//                                        Long oldStartTime = startTime[0];
//                                        startTime[0] = thisTime;
//                                        log.debug("okex 买卖挂单全量刷新，startTime改变由 "+oldStartTime+" 变为 "+startTime[0]+",isFullData为 "+ isFullData);
//                                    }else{
//                                        isFullData = false;
                                        List<List<String>> tempAsksList = new ArrayList<>();
                                        List<List<String>> tempBidsList = new ArrayList<>();
                                        orderBook.getAsks().stream().forEach(asks->{
                                            List<String> temp = new ArrayList<>();
                                            temp.add(asks.getLimitPrice().stripTrailingZeros().toPlainString());
                                            temp.add(asks.getOriginalAmount().stripTrailingZeros().toPlainString());
                                            tempAsksList.add(temp);
                                        });

                                        orderBook.getBids().stream().forEach(bids->{
                                            List<String> temp = new ArrayList<>();
                                            temp.add(bids.getLimitPrice().stripTrailingZeros().toPlainString());
                                            temp.add(bids.getOriginalAmount().stripTrailingZeros().toPlainString());
                                            tempBidsList.add(temp);
                                        });
                                        List<List<String>> asksList = tempAsksList;
                                        List<List<String>> bidsList = tempBidsList;
                                        log.info(" okex 增量数据 isFullData为{}",isFullData);

                                        //String symbol, boolean fullData, List<List<String>> asks,List<List<String>> bids
                                        DepthResponse depthResponse = new DepthResponse(symbol,isFullData,asksList,bidsList);
                                        this.toSendDepth(depthSubscribe,depthResponse);
//                                    }
                                },
                                throwable -> log.error("ERROR in getting depth: ", throwable)
                        );
                log.info("okex 盘口订阅成功 exchCode:{},symbol:{},interval:{},limit:{}", exchCode, symbol, intervalOld, limit);
            }
        }catch (Throwable throwable){
            log.error("okex 盘口订阅异常，异常信息：",throwable);
        }
    }

    @Override
    public void tradeSubscribe(RequestDto<TradeSubscribe> tradeSubscribeRequestBody) {
        try {
            TradeSubscribe tradeSubscribe = tradeSubscribeRequestBody.getParams();
            String sessionId = tradeSubscribe.getSessionId();
            ExchangeCode exchCode = tradeSubscribe.getExchangeCode();
            String symbol = tradeSubscribe.getSymbol();//交易对名称，如：BTC/USDT

            StreamingExchange streamingExchange = SessionUtil.getStreamingExchange(sessionId);
            if (streamingExchange != null && streamingExchange.getStreamingMarketDataService() != null) {
                try{
                    TradeDataResponse tradeDataResponse = this.restExchangeServiceFactory.getRestExchangeService(exchCode).trade(tradeSubscribe);
                    this.toSendTrades(tradeSubscribe,tradeDataResponse);
                }catch (Exception e){
                    log.error("调用 rest 接口查询 Okex 最新成交信息异常，异常信息：",e);
                }

                streamingExchange.getStreamingMarketDataService().getTrades(new CurrencyPair(symbol)).subscribe(
                        tradeList -> {
                            this.toSendTrades(tradeSubscribe,turnTradeToTradeDataResponse(tradeList));
                        },
                        throwable -> log.error("调用 Okex 更新历史成交记录异常，异常信息: ", throwable));
                log.info("okex 最新成交订阅成功 exchCode:{},symbol:{}", exchCode, symbol);
            }
        }catch (Throwable throwable){
            log.error("订阅 okex 历史成交记录信息异常，异常信息：",throwable);
        }
    }

    @Override
    public StreamingExchange getStreamingExchange(StreamingExchangeDto... args) {
        StreamingExchange okexStreamingExchange = StreamingExchangeFactory.INSTANCE.createExchange(OkexStreamingExchange.class
                .getName());
        okexStreamingExchange.connect().blockingAwait();
        return okexStreamingExchange;
    }

    @Override
    public ExchangeCode getExchCode() {
        return ExchangeCode.OKEX;
    }
}

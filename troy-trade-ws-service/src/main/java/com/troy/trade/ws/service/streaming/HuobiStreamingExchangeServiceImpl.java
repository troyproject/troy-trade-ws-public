package com.troy.trade.ws.service.streaming;

import com.alibaba.fastjson.JSONObject;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.streamingexchange.huobi.HuobiProStreamingExchange;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import com.troy.trade.ws.factory.RestExchangeServiceFactory;
import com.troy.trade.ws.model.domain.StreamingExchangeDto;
import com.troy.trade.ws.model.dto.in.DepthSubscribe;
import com.troy.trade.ws.model.dto.in.RequestDto;
import com.troy.trade.ws.model.dto.in.TradeSubscribe;
import com.troy.trade.ws.model.dto.in.ValidateDto;
import com.troy.trade.ws.model.dto.out.depth.DepthResponse;
import com.troy.trade.ws.server.SessionUtil;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.streamingexchange.core.StreamingExchangeFactory;
import com.troy.trade.ws.util.DepthInterval;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class HuobiStreamingExchangeServiceImpl extends BaseStreamingExchangeServiceImpl  {

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

                String intervalNew = DepthInterval.fromDepthIntervalCode(intervalOld).getCode();
                if (streamingExchange != null && streamingExchange.getStreamingMarketDataService() != null) {
                    streamingExchange.getStreamingMarketDataService().getOrderBook(new CurrencyPair(symbol), intervalNew).subscribe
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
                                        DepthResponse depthResponse = new DepthResponse(symbol,true,asksList,bidsList);
                                        this.toSendDepth(depthSubscribe,depthResponse);
                                    },
                                    throwable -> log.error("ERROR in getting depth: ", throwable)
                            );
                    log.info("火币盘口订阅成功 exchCode:{},symbol:{},interval:{},limit:{}", exchCode, symbol, intervalOld, limit);
                }
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
            ExchangeCode exchCode = tradeSubscribe.getExchangeCode();
            String symbol = tradeSubscribe.getSymbol();//交易对名称，如：BTC/USDT

            StreamingExchange streamingExchange = SessionUtil.getStreamingExchange(sessionId);
            if (streamingExchange != null && streamingExchange.getStreamingMarketDataService() != null) {

                streamingExchange.getStreamingMarketDataService().getTradesOnce(new CurrencyPair(symbol)).subscribe(firstTradeList ->
                {
                    this.toSendTrades(tradeSubscribe,turnTradeToTradeDataResponse(firstTradeList));
                    log.info("火币 最新成交首次推送成功 exchCode:{},symbol:{},数据列表:{}", exchCode, symbol, JSONObject.toJSONString(firstTradeList));

                    List<Object> firstInfoList = new ArrayList<>();
                    firstInfoList.add(true);
                    int size = firstTradeList == null?0:firstTradeList.size();
                    if(size>0){
                        firstInfoList.add(firstTradeList.get(0).getId());
                    }else{
                        firstInfoList.add("-123");
                    }

                    streamingExchange.getStreamingMarketDataService().getTrades(new CurrencyPair(symbol)).subscribe(
                            tradeList -> {
                                if(null != firstInfoList
                                        && firstInfoList.size()>0
                                        && (Boolean)firstInfoList.get(0)){
                                    log.info("火币 最新成交订阅成功 exchCode:{},symbol:{},第一次获取成交记录。", exchCode, symbol);
                                    int newTradeize = tradeList == null?0:tradeList.size();
                                    for(int i=0;i<newTradeize;i++){
                                        if(StringUtils.equals(tradeList.get(i).getId(),(String)firstInfoList.get(1))){
                                            tradeList.remove(i);
                                            firstInfoList.add(0,false);
                                            break;
                                        }
                                    }
                                }
                                this.toSendTrades(tradeSubscribe,turnTradeToTradeDataResponse(tradeList));
                            },
                            throwable -> log.error("调用 火币 更新历史成交记录异常，异常信息: ", throwable));
                    log.info("火币 最新成交订阅成功 exchCode:{},symbol:{}", exchCode, symbol);

                }, throwable -> log.error("调用火币查询历史成交记录异常，异常信息: ", throwable));
            }
        }catch (Throwable throwable){
            log.error("订阅 火币 历史成交记录信息异常，异常信息：",throwable);
        }
    }

    @Override
    public StreamingExchange getStreamingExchange(StreamingExchangeDto... args) {
        StreamingExchange huoioStreamingExchange = StreamingExchangeFactory.INSTANCE.createExchange(HuobiProStreamingExchange.class.getName());
        huoioStreamingExchange.connect().blockingAwait();
        return huoioStreamingExchange;
    }

    @Override
    public ExchangeCode getExchCode() {
        return ExchangeCode.HUOBI;
    }
}

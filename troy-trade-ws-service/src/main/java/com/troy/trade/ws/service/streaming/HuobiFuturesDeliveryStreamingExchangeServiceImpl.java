package com.troy.trade.ws.service.streaming;

import com.alibaba.fastjson.JSONObject;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.enums.AliasEnum;
import com.troy.commons.utils.EnumUtils;
import com.troy.streamingexchange.huobi.HuobiProStreamingExchange;
import com.troy.streamingfutures.huobi.HuobiFuturesStreamingExchange;
import com.troy.trade.ws.constants.WsScheduledConstant;
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
import com.troy.trade.ws.util.BusinessMethodsUtil;
import com.troy.trade.ws.util.DepthInterval;
import com.troy.trade.ws.util.WebSocketErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class HuobiFuturesDeliveryStreamingExchangeServiceImpl extends BaseStreamingExchangeServiceImpl  {

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
            String alias = depthSubscribe.getAlias().code();//交易对类型: 本周 this_week、次周 next_week、季度 quarter   交割合约 必传
            AliasEnum aliasEnum = EnumUtils.getEnumByCode(alias,AliasEnum.class);
            String intervalOld = depthSubscribeRequestBody.getParams().getInterval();
            String sessionId = depthSubscribe.getSessionId();

            StreamingExchange streamingExchange = SessionUtil.getStreamingExchange(sessionId);

            if (streamingExchange != null && streamingExchange.getStreamingMarketDataService() != null) {
//                String intervalNew = DepthInterval.fromDepthIntervalCode(intervalOld).getCode();
                if (streamingExchange != null && streamingExchange.getStreamingMarketDataService() != null) {
                    streamingExchange.getStreamingMarketDataService().getOrderBook(new CurrencyPair(symbol), aliasEnum).subscribe
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
                    log.info("火币合约盘口订阅成功 exchCode:{},symbol:{},interval:{},limit:{}", exchCode, symbol, intervalOld, limit);
                }
            }
        }catch (Throwable throwable){
            log.error("火币合约盘口订阅异常，异常信息：",throwable);
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
                String alias = tradeSubscribe.getAlias().code();//交易对类型: 本周 this_week、次周 next_week、季度 quarter   交割合约 必传
                AliasEnum aliasEnum = EnumUtils.getEnumByCode(alias,AliasEnum.class);

                try{
                    TradeDataResponse tradeDataResponse = this.restExchangeServiceFactory.getRestExchangeService(exchCode).trade(tradeSubscribe);
                    this.toSendTrades(tradeSubscribe,tradeDataResponse);
                }catch (Exception e){
                    log.error("调用 rest 接口查询 Okex 最新成交信息异常，异常信息：",e);
                }

                streamingExchange.getStreamingMarketDataService().getTrades(new CurrencyPair(symbol),aliasEnum).subscribe(
                        tradeList -> {
                            this.toSendTrades(tradeSubscribe,turnTradeToTradeDataResponse(tradeList));
                        },
                        throwable -> log.error("调用 火币 更新历史成交记录异常，异常信息: ", throwable));
                log.info("火币 最新成交订阅成功 exchCode:{},symbol:{}", exchCode, symbol);
            }
        }catch (Throwable throwable){
            log.error("订阅 火币 历史成交记录信息异常，异常信息：",throwable);
        }
    }

    @Override
    public StreamingExchange getStreamingExchange(StreamingExchangeDto... args) {
        StreamingExchange huoioStreamingExchange = StreamingExchangeFactory.INSTANCE.createExchange(HuobiFuturesStreamingExchange.class.getName());
        huoioStreamingExchange.connect().blockingAwait();
        return huoioStreamingExchange;
    }

    @Override
    public ExchangeCode getExchCode() {
        return ExchangeCode.HUOBI_FUTURES_DELIVERY;
    }
}

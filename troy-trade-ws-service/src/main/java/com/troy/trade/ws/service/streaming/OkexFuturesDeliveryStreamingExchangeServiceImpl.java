package com.troy.trade.ws.service.streaming;

import com.alibaba.fastjson.JSONObject;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.futures.exchange.api.model.dto.out.account.ContractInfoResDto;
import com.troy.redis.RedisUtil;
import com.troy.streamingfutures.okex.OkexFuturesStreamingExchange;
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
import com.troy.trade.ws.util.WebSocketErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OkexFuturesDeliveryStreamingExchangeServiceImpl extends BaseStreamingExchangeServiceImpl  {

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

            log.info("okex 交割合约盘口订阅：入参：{}",depthSubscribe);
            ExchangeCode exchCode = depthSubscribe.getExchangeCode();
            String symbol = depthSubscribe.getSymbol();//交易对名称，如：BTC/USDT

            Integer limit = depthSubscribe.getLimit();//盘口条数
            String alias = depthSubscribe.getAlias().code();//交易对类型: 本周 this_week、次周 next_week、季度 quarter   交割合约 必传

            String intervalOld = depthSubscribeRequestBody.getParams().getInterval();
            String sessionId = depthSubscribe.getSessionId();

            StreamingExchange streamingExchange = SessionUtil.getStreamingExchange(sessionId);

            if (streamingExchange != null && streamingExchange.getStreamingMarketDataService() != null) {

                /**
                 * 从缓存中获取合约ID信息
                 */
                String contractInfoMapKey = BusinessMethodsUtil.getContractKey(symbol.toUpperCase(),alias);
                //xxxxxange:ws:contractInfo:{exchCode}:symbol_alias
                String redisKey = WsScheduledConstant.SYNC_CONTRACT_INFO_SYMBOL_ALIAS_MAP_REDIS_KEY.replace("{exchCode}", ExchangeCode.OKEX_FUTURES_DELIVERY.code());
                Object object = redisUtil.hGet(redisKey,contractInfoMapKey);
                log.info("okex 交割合约盘口订阅：从缓存中获取合约信息，redisKey={},mapKey={},value={}",
                        redisKey,contractInfoMapKey,object);
                if(null == object){
                    this.toSendDepthError(depthSubscribe,WebSocketErrorCode.FAIL);
                    return;
                }

                //做全量数据推送
                toSendAllDepth(exchCode,symbol,depthSubscribe);

                //futures/order:BTC-USD-170317
                String result = (String)object;
                ContractInfoResDto contractInfoResDto = JSONObject.parseObject(result, ContractInfoResDto.class);
                String instrumentId = contractInfoResDto.getInstrumentId();
                streamingExchange.getStreamingMarketDataService().getOrderBook(new CurrencyPair(symbol),false,instrumentId).subscribe
                        (
                                orderBook -> {
                                    boolean isFullData = false;
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
                                },
                                throwable -> log.error("ERROR in getting depth: ", throwable)
                        );
                log.info("okex 交割合约盘口订阅成功 exchCode:{},symbol:{},interval:{},limit:{}", exchCode, symbol, intervalOld, limit);
            }
        }catch (Throwable throwable){
            log.error("okex 交割合约盘口订阅异常，异常信息：",throwable);
        }
    }

    @Override
    public void tradeSubscribe(RequestDto<TradeSubscribe> tradeSubscribeRequestBody) {
        try {
            TradeSubscribe tradeSubscribe = tradeSubscribeRequestBody.getParams();
            String sessionId = tradeSubscribe.getSessionId();

            StreamingExchange streamingExchange = SessionUtil.getStreamingExchange(sessionId);
            if (streamingExchange != null && streamingExchange.getStreamingMarketDataService() != null) {
                ExchangeCode exchCode = tradeSubscribe.getExchangeCode();
                String symbol = tradeSubscribe.getSymbol();//交易对名称，如：BTC/USDT
                String alias = tradeSubscribe.getAlias().code();//交易对类型: 本周 this_week、次周 next_week、季度 quarter   交割合约 必传

                /**
                 * 从缓存中获取合约ID信息
                 */
                String contractInfoMapKey = BusinessMethodsUtil.getContractKey(symbol.toUpperCase(),alias);
                //xxxxxange:ws:contractInfo:{exchCode}:symbol_alias
                String redisKey = WsScheduledConstant.SYNC_CONTRACT_INFO_SYMBOL_ALIAS_MAP_REDIS_KEY.replace("{exchCode}", ExchangeCode.OKEX_FUTURES_DELIVERY.code());
                Object object = redisUtil.hGet(redisKey,contractInfoMapKey);
                if(null == object){
                    this.toSendTradesError(tradeSubscribe,WebSocketErrorCode.FAIL);
                    return;
                }


                try{
                    TradeDataResponse tradeDataResponse = this.restExchangeServiceFactory.getRestExchangeService(exchCode).trade(tradeSubscribe);
                    this.toSendTrades(tradeSubscribe,tradeDataResponse);
                }catch (Exception e){
                    log.error("调用 rest 接口查询 Okex 最新成交信息异常，异常信息：",e);
                }

                //futures/order:BTC-USD-170317
                String result = (String)object;
                ContractInfoResDto contractInfoResDto = JSONObject.parseObject(result, ContractInfoResDto.class);
                String instrumentId = contractInfoResDto.getInstrumentId();
                streamingExchange.getStreamingMarketDataService().getTrades(new CurrencyPair(symbol),instrumentId).subscribe(
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
        StreamingExchange okexStreamingExchange = StreamingExchangeFactory.INSTANCE.createExchange(OkexFuturesStreamingExchange.class
                .getName());
        okexStreamingExchange.connect().blockingAwait();
        return okexStreamingExchange;
    }

    @Override
    public ExchangeCode getExchCode() {
        return ExchangeCode.OKEX_FUTURES_DELIVERY;
    }
}

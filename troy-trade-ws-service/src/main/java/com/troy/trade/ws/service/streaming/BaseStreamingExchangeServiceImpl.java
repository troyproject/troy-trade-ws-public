package com.troy.trade.ws.service.streaming;

import com.alibaba.fastjson.JSONObject;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.enums.OrderSideEnum;
import com.troy.commons.utils.ApplicationContextUtil;
import com.troy.commons.utils.EnumUtils;
import com.troy.trade.ws.constants.Constant;
import com.troy.trade.ws.dto.Trade;
import com.troy.trade.ws.enums.OrderTypeEnum;
import com.troy.trade.ws.factory.RestExchangeServiceFactory;
import com.troy.trade.ws.model.domain.StreamingExchangeDto;
import com.troy.trade.ws.model.dto.in.ConnectDto;
import com.troy.trade.ws.model.dto.in.DepthSubscribe;
import com.troy.trade.ws.model.dto.in.DisconnectDto;
import com.troy.trade.ws.model.dto.in.TradeSubscribe;
import com.troy.trade.ws.model.dto.out.ResponseDto;
import com.troy.trade.ws.model.dto.out.TradeResponse;
import com.troy.trade.ws.model.dto.out.depth.DepthResponse;
import com.troy.trade.ws.model.dto.out.trades.TradeDataResponse;
import com.troy.trade.ws.model.enums.MethodEnum;
import com.troy.trade.ws.server.NotificationService;
import com.troy.trade.ws.server.SessionUtil;
import com.troy.trade.ws.streamingexchange.core.StreamingExchange;
import com.troy.trade.ws.util.WebSocketErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public abstract class BaseStreamingExchangeServiceImpl implements IStreamingExchangeService  {

    @Override
    public Boolean connect(ConnectDto connectDto) {
        Boolean resultBo = false;
        try{
            String sessionId = connectDto.getSessionId();
            if (SessionUtil.getSessions().containsKey(sessionId)) {
                return true;
            }
            StreamingExchange streamingExchange = this.getStreamingExchange();
            return saveStreamingExchange(sessionId, streamingExchange);
        }catch (Throwable throwable){
            log.error("订阅"+this.getExchCode().desc()+" ws异常，异常信息：",throwable);
        }
        return resultBo;
    }

    public abstract StreamingExchange getStreamingExchange(StreamingExchangeDto... args);

    @Override
    public Boolean disconnect(DisconnectDto disconnectDto) {
        log.info("用户断开连接，执行断开操作 入参：{} ", JSONObject.toJSONString(disconnectDto));
        Boolean resultBo = false;
        String exchCode = disconnectDto.getExchCode();
        String clientId = disconnectDto.getClientId();
        String sessionId = disconnectDto.getSessionId();
        if((ExchangeCode.BISS.code().equals(exchCode)
                ||ExchangeCode.BITTREX.code().equals(exchCode)
                ||ExchangeCode.BITMAX.code().equals(exchCode)
                ||ExchangeCode.ONBLOCK.code().equals(exchCode)
                ||ExchangeCode.MXC.code().equals(exchCode)
                ||ExchangeCode.BITHUMB.code().equals(exchCode)
                ||ExchangeCode.COINFINIT.code().equals(exchCode))){
            ExchangeCode exchangeCode = EnumUtils.getEnumByCode(exchCode,ExchangeCode.class);
            boolean futuresBo = NotificationService.isFutures(exchangeCode);
            SessionUtil.removeClient(clientId,sessionId,futuresBo);
            resultBo = true;
        }

        if (SessionUtil.getSessions().containsKey(sessionId)) {
            log.info("用户断开连接，执行断开操作 getSessions中包含此session,做移除处理，session={},getSessions={}", sessionId,SessionUtil.getSessions());
            SessionUtil.removeSession(sessionId, clientId);
            resultBo = true;
        }else{
            log.info("用户断开连接，执行断开操作 getSessions中不包含此session,session={},getSessions={}", sessionId,SessionUtil.getSessions());
        }
        return resultBo;
    }

    /**
     * 做盘口信息转换及发送
     * @param depthSubscribe
     * @param depthResponse
     */
    public void toSendDepth(DepthSubscribe depthSubscribe, DepthResponse depthResponse){

        if(null == depthResponse || (null == depthResponse.getAsks() && null == depthResponse.getBids())){//盘口推送空数据，不做处理
            return;
        }

        ExchangeCode exchCode = depthSubscribe.getExchangeCode();

        String symbol = depthSubscribe.getSymbol();//交易对名称，如：BTC/USDT

        String key = SessionUtil.genKey(depthSubscribe.getSessionId(), exchCode.code(), symbol);
        String destination = NotificationService.getDepthDestination(depthSubscribe);

        NotificationService notificationService = ApplicationContextUtil.getBean(NotificationService.class);
        ResponseDto responseDto = notificationService.turnNotification(depthResponse, MethodEnum.DEPTH_UPDATE.getType());
        notificationService.broadcast(key, destination, responseDto);
    }

    /**
     * 盘口数据订阅--错误信息发送
     * @param depthSubscribe
     * @param webSocketErrorCode
     */
    public void toSendDepthError(DepthSubscribe depthSubscribe,WebSocketErrorCode webSocketErrorCode){
        NotificationService notificationService = ApplicationContextUtil.getBean(NotificationService.class);
        ExchangeCode exchCode = depthSubscribe.getExchangeCode();

        String symbol = depthSubscribe.getSymbol();//交易对名称，如：BTC/USDT
        String key = SessionUtil.genKey(depthSubscribe.getSessionId(), exchCode.code(), symbol);
        String destination = NotificationService.getDepthDestination(depthSubscribe);
        ResponseDto responseDto = notificationService.turnFailNotification(webSocketErrorCode, MethodEnum.DEPTH_SUBSCRIBE);
        notificationService.broadcast(key,destination,responseDto);
    }

    /**
     * 做历史成交信息转换及发送
     * @param tradeSubscribe
     * @param tradeDataResponse
     */
    public void toSendTrades(TradeSubscribe tradeSubscribe, TradeDataResponse tradeDataResponse){

        if(null == tradeDataResponse){//历史成交信息推送空数据，不做处理
            return;
        }

        ExchangeCode exchCode = tradeSubscribe.getExchangeCode();

        String symbol = tradeSubscribe.getSymbol();//交易对名称，如：BTC/USDT
        String key = SessionUtil.genKey(tradeSubscribe.getSessionId(), exchCode.code(), symbol);

        List<TradeResponse> tradeResponseList = tradeDataResponse.getTradeResponseList();

        String destination = NotificationService.getTradesDestination(tradeSubscribe);

        NotificationService notificationService = ApplicationContextUtil.getBean(NotificationService.class);
        ResponseDto responseDto = notificationService.turnNotification(tradeResponseList, MethodEnum.TRADE_UPDATE.getType());
        notificationService.broadcast(key, destination, responseDto);
        log.debug("通知客户端-市场最新成交 exit key:{}", key);
    }

    /**
     * 做历史成交信息转换及发送
     * @param tradeSubscribe
     * @param webSocketErrorCode
     */
    public void toSendTradesError(TradeSubscribe tradeSubscribe,WebSocketErrorCode webSocketErrorCode){
        NotificationService notificationService = ApplicationContextUtil.getBean(NotificationService.class);
        ExchangeCode exchCode = tradeSubscribe.getExchangeCode();
        String symbol = tradeSubscribe.getSymbol();//交易对名称，如：BTC/USDT
        String key = SessionUtil.genKey(tradeSubscribe.getSessionId(), exchCode.code(), symbol);

        String destination = NotificationService.getTradesDestination(tradeSubscribe);
        ResponseDto responseDto = notificationService.turnFailNotification(webSocketErrorCode, MethodEnum.TRADE_SUBSCRIBE);
        notificationService.broadcast(key,destination,responseDto);
    }

    public TradeDataResponse turnTradeToTradeDataResponse(List<Trade> tradeList){
        int size = tradeList == null?0:tradeList.size();
        if(size<=0){
            return null;
        }

        if(size>=25){
            Collections.reverse(tradeList);
        }

        List<TradeResponse> tradeResponseList = new ArrayList<>();
        tradeList.stream().forEach(trade ->{
            OrderSideEnum orderSide = null;
            if(StringUtils.equals(trade.getType().toString(), OrderTypeEnum.BID.toString())){//买
                orderSide = OrderSideEnum.BID;
            }else{
                orderSide = OrderSideEnum.ASK;
            }
            //Integer type, String amount, String symbol, String price, Long timestamp, String id
            TradeResponse tempTradeResponse = new TradeResponse(orderSide.code(),trade.getOriginalAmount().toPlainString(),
                    trade.getCurrencyPair().toString(),trade.getPrice().toPlainString(),trade.getTimestamp().getTime(),
                    trade.getId());
            tradeResponseList.add(tempTradeResponse);
        });
        TradeDataResponse tradeDataResponse = new TradeDataResponse(tradeResponseList);
        return tradeDataResponse;
    }

    public boolean saveStreamingExchange(String sessionId, StreamingExchange streamingExchange){
        if (streamingExchange != null) {
            SessionUtil.getSessions().put(sessionId, streamingExchange);
            return true;
        }
        return false;
    }

    public boolean valiSession(String sessionId){
        if (SessionUtil.getSessions().containsKey(sessionId)) {
            return true;
        }
        return false;
    }

    /**
     * 推送全量买卖盘口数据
     * @author yanping
     * @param exchCode
     * @param symbol
     * @param depthSubscribe
     */
    public void toSendAllDepth(ExchangeCode exchCode, String symbol, DepthSubscribe depthSubscribe){
        try {
            //做全量数据查询并发送
            int orderBookRequestSize = Constant.depthDefaultLimit.get(exchCode.code());
            DepthSubscribe depthSubscribeTemp = new DepthSubscribe();
            depthSubscribeTemp.setLimit(orderBookRequestSize);
            depthSubscribeTemp.setExchangeCode(exchCode);
            depthSubscribeTemp.setExchCode(exchCode.code());
            depthSubscribeTemp.setSymbol(symbol);
            depthSubscribeTemp.setAlias(depthSubscribe.getAlias());
            String intervalOld = depthSubscribe.getInterval();//深度
            depthSubscribeTemp.setInterval(intervalOld);

            DepthResponse depthResponse = ApplicationContextUtil.getBean(RestExchangeServiceFactory.class)
                    .getRestExchangeService(exchCode).orderBook(depthSubscribeTemp);

            //截串
            int size = 21;
            List<List<String>> asks = depthResponse.getAsks();
            int askSize = asks == null?0:asks.size();
            if(askSize>size){
                asks = asks.subList(0,size);
                depthResponse.setAsks(asks);
            }

            List<List<String>> bids = depthResponse.getBids();
            int bidsSize = bids == null?0:bids.size();
            if(bidsSize>size){
                bids = bids.subList(0,size);
                depthResponse.setBids(bids);
            }
            log.info("调用 rest 接口查询"+exchCode+"盘口信息数据{}"+ JSONObject.toJSONString(depthResponse));
            this.toSendDepth(depthSubscribe,depthResponse);
        }catch (Exception e){
            log.error("调用 rest 接口查询"+exchCode+"盘口信息异常，异常信息：",e);
        }
    }


    /**
     * 推送全量买卖盘口数据
     * @author yanping
     * @param exchCode
     * @param symbol
     * @param depthSubscribe
     */
    public DepthResponse getAllDepth(ExchangeCode exchCode,String symbol,DepthSubscribe depthSubscribe){
        try {
            //做全量数据查询并发送
            int orderBookRequestSize = Constant.depthDefaultLimit.get(exchCode.code());
            DepthSubscribe depthSubscribeTemp = new DepthSubscribe();
            depthSubscribeTemp.setLimit(orderBookRequestSize);
            depthSubscribeTemp.setExchangeCode(exchCode);
            depthSubscribeTemp.setExchCode(exchCode.code());
            depthSubscribeTemp.setSymbol(symbol);
            String intervalOld = depthSubscribe.getInterval();//深度
            depthSubscribeTemp.setInterval(intervalOld);

            DepthResponse depthResponse = ApplicationContextUtil.getBean(RestExchangeServiceFactory.class)
                    .getRestExchangeService(exchCode).orderBook(depthSubscribeTemp);

            //截串
            int size = 21;
            List<List<String>> asks = depthResponse.getAsks();
            int askSize = asks == null?0:asks.size();
            if(askSize>size){
                asks = asks.subList(0,size);
                depthResponse.setAsks(asks);
            }

            List<List<String>> bids = depthResponse.getBids();
            int bidsSize = bids == null?0:bids.size();
            if(bidsSize>size){
                bids = bids.subList(0,size);
                depthResponse.setBids(bids);
            }
            log.info("调用 rest 接口查询"+exchCode+"盘口信息数据{}"+ JSONObject.toJSONString(depthResponse));
            return depthResponse;
        }catch (Exception e){
            log.error("调用 rest 接口查询"+exchCode+"盘口信息异常，异常信息：",e);
            return null;
        }
    }
}

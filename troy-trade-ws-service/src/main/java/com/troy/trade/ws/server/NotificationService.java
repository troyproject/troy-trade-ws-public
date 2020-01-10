package com.troy.trade.ws.server;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.enums.AliasEnum;
import com.troy.commons.exchange.model.enums.ExchTypeEnum;
import com.troy.trade.futures.api.model.dto.out.account.FuturesBalanceResDto;
import com.troy.trade.ws.api.model.dto.in.FuturesOpenOrderReqDto;
import com.troy.trade.ws.api.model.dto.in.SpotOpenOrderReqDto;
import com.troy.trade.ws.constants.Constant;
import com.troy.trade.ws.model.dto.in.*;
import com.troy.trade.ws.model.dto.out.ResponseDto;
import com.troy.trade.ws.model.enums.MethodEnum;
import com.troy.trade.ws.util.BusinessMethodsUtil;
import com.troy.trade.ws.util.WebSocketErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;


/**
 * NotificationService
 * 收到交易所的通知,发送给订阅者
 * @author yp
 */
@Service
@Slf4j
public class NotificationService {

    public final static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 通知客户端-买卖挂单
     *
     * @param key
     * @param destination
     * @param destination
     */
    public void broadcast(String key, String destination, Object notification) {
        messagingTemplate.convertAndSendToUser(key,
                destination,
                notification);
        log.debug("通知客户端-买卖挂单 entry,key:{},destination:{},notification{}",
                key, destination, JSONObject.toJSONString(notification));
        return;
    }

    /**
     * 做现货当前挂单变动信息推送
     *
     * @param privateParamDto
     */
    public void broadcastSpotOrder(PrivateParamDto<List<SpotOpenOrderReqDto>> privateParamDto) {
        broadcastSpotPrivate(privateParamDto,MethodEnum.ORDER_UPDATE,Constant.ORDER_DESTINATION_PREFIX);
    }


    /**
     * 做现货当前挂单变动信息推送
     *
     * @param privateParamDto
     */
    public void broadcastFuturesOrder(PrivateParamDto<List<FuturesOpenOrderReqDto>> privateParamDto) {
        broadcastFuturesPrivate(privateParamDto,MethodEnum.ORDER_UPDATE,Constant.ORDER_DESTINATION_PREFIX);
    }

    /**
     * 做合约余额变动信息推送
     *
     * @param privateParamDto
     */
    public void broadcastFuturesBalance(PrivateParamDto<FuturesBalanceResDto> privateParamDto) {
        broadcastFuturesPrivate(privateParamDto,MethodEnum.BALANCE_UPDATE,Constant.BALANCE_DESTINATION_PREFIX);
    }

    /**
     * 做现货当前挂单变动信息推送
     *
     * @param privateParamDto
     */
    private void broadcastSpotPrivate(PrivateParamDto privateParamDto,MethodEnum methodEnum,String destinationPrefix) {
        //遍历Map分别根据交易对推送
        //<accountId,<symbol,sessionSet>>
        ConcurrentMap<String, Map<String, Set<String>>> spotAccountSessionsMap = SessionUtil.getAccountSessionsMap(false);
        try {
            log.info("用户币币订阅，broadcastOrder方法做数据推送，入参：{},methodEnum={},destinationPrefix={},spotAccountSessionsKeyMap={}",
                    objectMapper.writeValueAsString(privateParamDto),
                    methodEnum,
                    destinationPrefix,
                    spotAccountSessionsMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (spotAccountSessionsMap.isEmpty()) {
            //订阅
            log.warn("用户币币订阅，做信息推送失败，失败原因：当前订阅信息为空");
            return;
        }

        //根据账户ID获取session列表
        String accountId = String.valueOf(privateParamDto.getAccountId());
        Map<String, Set<String>> sessionMap = spotAccountSessionsMap.get(accountId);
        if(CollectionUtils.isEmpty(sessionMap)){
            log.warn("用户币币订阅，做信息推送失败，失败原因：当前订阅信息为空,sessionMap为空");
            return;
        }

        String symbol = privateParamDto.getSymbol();
        if(!sessionMap.containsKey(symbol)){
            log.warn("用户币币订阅，做信息推送失败，失败原因：当前订阅信息为空,symbol为空");
            return;
        }

        List<String> sessionIdList = new ArrayList<>(sessionMap.get(symbol));
        try {
            log.info("用户币币订阅，推送范围sessionId列表 {}",objectMapper.writeValueAsString(sessionIdList));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //当前账户的sessionId列表为空则不做处理
        if (CollectionUtils.isEmpty(sessionIdList)) {
            log.warn("用户币币订阅，做信息推送失败，失败原因：当前账号的订阅信息为空,sessionIdSet为空");
            return;
        }

        String exchCode = privateParamDto.getExchCode();

        PrivateRequestDto privateRequestDto = new PrivateRequestDto();
        privateRequestDto.setAccountId(accountId);
        privateRequestDto.setExchCode(exchCode);
        privateRequestDto.setExchangeCode(privateParamDto.getExchangeCode());
        privateRequestDto.setSymbol(symbol);
        String destination = this.getPrivateDestination(destinationPrefix,privateRequestDto);

        ResponseDto responseDto = this.turnNotification(privateParamDto.getParams(), methodEnum.getType());
        try {
            log.info("用户币币订阅，信息转换为responseDto {}",objectMapper.writeValueAsString(responseDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String sessionId = null;
        int sessionIdSize = sessionIdList.size();
        //做session信息遍历 并 发送消息
        String key = null;
        for (int j = 0; j < sessionIdSize; j++) {
            sessionId = sessionIdList.get(j);
            key = SessionUtil.genKey(sessionId, exchCode, symbol);
            this.broadcast(key, destination, responseDto);
            log.info("用户币币订阅，通知客户端-账户当前挂单变动 key:{},destination:{},responseDto:{}", key, destination, responseDto);
        }
    }

    /**
     * 做合约变动信息推送
     * @param privateParamDto
     * @param methodEnum
     * @param destinationPrefix
     */
    private void broadcastFuturesPrivate(PrivateParamDto privateParamDto,MethodEnum methodEnum,String destinationPrefix) {
        //遍历Map分别根据交易对推送
        //<accountId,<symbol,sessionSet>>
        ConcurrentMap<String, Map<String, Set<String>>> futuresAccountSessionsMap = SessionUtil.getAccountSessionsMap(true);
        try {
            log.info("用户合约订阅，broadcastOrder方法做数据推送，入参：{},methodEnum={},destinationPrefix={},FuturesAccountSessionsMap={}",
                    objectMapper.writeValueAsString(privateParamDto),
                    methodEnum,
                    destinationPrefix,
                    futuresAccountSessionsMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (futuresAccountSessionsMap.isEmpty()) {
            //订阅
            log.warn("用户合约订阅，做信息推送失败，失败原因：当前订阅信息为空");
            return;
        }

        //根据账户ID获取session列表
        String accountId = String.valueOf(privateParamDto.getAccountId());
        Map<String, Set<String>> sessionMap = futuresAccountSessionsMap.get(accountId);
        if(CollectionUtils.isEmpty(sessionMap)){
            log.warn("用户合约订阅，做信息推送失败，失败原因：当前订阅信息为空,sessionMap为空");
            return;
        }

        String symbol = privateParamDto.getSymbol();
        AliasEnum aliasEnum = privateParamDto.getAlias();
        String symbolKey = SessionUtil.getAccountIdSessionMapKey(symbol,aliasEnum);
        if(!sessionMap.containsKey(symbolKey)){
            log.warn("用户合约订阅，做信息推送失败，" +
                            "失败原因：当前订阅信息为空,symbol为{},aliasEnum为{},symbolKey为{}",
                    symbol,aliasEnum,symbolKey);
            return;
        }

        List<String> sessionIdList = new ArrayList<>(sessionMap.get(symbolKey));
        try {
            log.info("用户合约订阅，推送范围sessionId列表 {}",objectMapper.writeValueAsString(sessionIdList));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //当前账户的sessionId列表为空则不做处理
        if (CollectionUtils.isEmpty(sessionIdList)) {
            log.warn("用户合约订阅，做信息推送失败，失败原因：当前账号的订阅信息为空,sessionIdSet为空");
            return;
        }


        String exchCode = privateParamDto.getExchCode();

        PrivateRequestDto privateRequestDto = new PrivateRequestDto();
        privateRequestDto.setAccountId(accountId);
        privateRequestDto.setExchCode(exchCode);
        privateRequestDto.setExchangeCode(privateParamDto.getExchangeCode());
        privateRequestDto.setSymbol(symbol);
        privateRequestDto.setAlias(aliasEnum);
        String destination = this.getPrivateDestination(destinationPrefix,privateRequestDto);

        ResponseDto responseDto = this.turnNotification(privateParamDto.getParams(), methodEnum.getType());
        try {
            log.info("用户合约订阅，信息转换为responseDto {}",objectMapper.writeValueAsString(responseDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String sessionId = null;
        int sessionIdSize = sessionIdList.size();
        //做session信息遍历 并 发送消息
        String key = null;
        for (int j = 0; j < sessionIdSize; j++) {
            sessionId = sessionIdList.get(j);
            key = SessionUtil.genKey(sessionId, exchCode, symbol);
            this.broadcast(key, destination, responseDto);
            log.info("用户合约订阅，通知客户端-变动 key:{},destination:{},responseDto:{}", key, destination, responseDto);
        }
    }

    public ResponseDto turnNotification(Object result, String method) {
        try {
            ResponseDto responseDto = new ResponseDto(WebSocketErrorCode.SUCCESS.getCode(), result, method);
            return responseDto;
        } catch (Throwable e) {
            log.error("信息转换成ResponseDto异常，异常信息：", e);
        }
        return null;
    }

    public ResponseDto turnFailNotification(WebSocketErrorCode webSocketErrorCode, MethodEnum methodEnum) {
        try {
            ResponseDto responseDto = BusinessMethodsUtil.failResponse(webSocketErrorCode, methodEnum.getType());
            return responseDto;
        } catch (Throwable e) {
            log.error("信息转换成Notification异常，异常信息：", e);
        }
        return null;
    }


    public static boolean isFutures(ExchangeCode exchCode){
        if(exchCode.getExchTypeEnum() == ExchTypeEnum.FUTURES_DELIVERY
                || exchCode.getExchTypeEnum() == ExchTypeEnum.FUTURES_SWAP){
            return true;
        }else{
            return false;
        }
    }


    /**
     * 获取depth订阅路径
     * @param depthSubscribe
     * @return
     */
    public static String getDepthDestination(DepthSubscribe depthSubscribe){
        //现货："/topic/depth/" + pairPath+"/"+limit+"/"+intervalOld
        //期货："/topic/depth/" + pairPath+"/"+ alias+"/"+limit+"/"+intervalOld

        String symbol = depthSubscribe.getSymbol();//交易对名称，如：BTC/USDT
        String pairPath = symbol.replace("/", "_").toLowerCase();

        Integer limit = depthSubscribe.getLimit();//盘口条数

        String intervalOld = depthSubscribe.getInterval();//深度
        ExchangeCode exchCode = depthSubscribe.getExchangeCode();
        boolean futuresBo = NotificationService.isFutures(exchCode);

        //做盘口信息发送
        StringBuffer destinationSb = new StringBuffer(Constant.DEPTH_DESTINATION_PREFIX);
        destinationSb.append(pairPath);

        AliasEnum alias = depthSubscribe.getAlias();
        if(futuresBo){
            destinationSb.append("/");
            destinationSb.append(alias);
        }
        destinationSb.append("/");
        destinationSb.append(limit);
        destinationSb.append("/");
        destinationSb.append(intervalOld);
        return destinationSb.toString();
    }

    /**
     * 获取最新成交订阅路径
     * @param tradeSubscribe
     * @return
     */
    public static String getTradesDestination(TradeSubscribe tradeSubscribe){
        //现货："/topic/xxxxxs/" + pairPath
        //期货："/topic/xxxxxs/" + pairPath+"/"+ alias

        AliasEnum alias = tradeSubscribe.getAlias();
        String symbol = tradeSubscribe.getSymbol();//交易对名称，如：BTC/USDT
        String pairPath = symbol.replace("/", "_").toLowerCase();

        //做历史成交信息发送
        StringBuffer destinationSb = new StringBuffer(Constant.TRADES_DESTINATION_PREFIX);
        destinationSb.append(pairPath);

        ExchangeCode exchCode = tradeSubscribe.getExchangeCode();
        boolean futuresBo = NotificationService.isFutures(exchCode);
        if(futuresBo){
            destinationSb.append("/");
            destinationSb.append(alias);
        }
        return destinationSb.toString();
    }

    /**
     * 获取订单订阅path
     * @param orderSubscribe
     * @return
     */
    public static String getOrderDestination(OrderSubscribe orderSubscribe){
        //现货："/topic/order/" + pairPath + "/"+accountId
        //期货："/topic/order/" + pairPath+"/"+ alias + "/"+accountId
        return getPrivateDestination(Constant.ORDER_DESTINATION_PREFIX, orderSubscribe);
    }

    /**
     * 获取余额订阅path
     * @param balanceSubscribe
     * @return
     */
    public static String getBalanceDestination(BalanceSubscribe balanceSubscribe){
        //现货："/topic/balance/" + pairPath + "/"+accountId
        //期货："/topic/balance/" + pairPath+"/"+ alias + "/"+accountId
        return getPrivateDestination(Constant.BALANCE_DESTINATION_PREFIX, balanceSubscribe);
    }

    private static String getPrivateDestination(String pathPrefix,PrivateRequestDto privateRequestDto){
        String symbol = privateRequestDto.getSymbol();
        String pairPath = symbol.replace("/", "_").toLowerCase();
        ExchangeCode exchCode = privateRequestDto.getExchangeCode();
        AliasEnum alias = privateRequestDto.getAlias();
        String accountId = privateRequestDto.getAccountId();

        StringBuffer destinationSb = new StringBuffer(pathPrefix);
        destinationSb.append(pairPath);
        boolean isFuturesBo = NotificationService.isFutures(exchCode);
        if(isFuturesBo){//是否为合约
            destinationSb.append("/");
            destinationSb.append(alias);
        }
        destinationSb.append("/");
        destinationSb.append(accountId);
        return destinationSb.toString();
    }
}

package com.troy.trade.ws.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.in.ReqHead;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResData;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.utils.ApplicationContextUtil;
import com.troy.trade.api.model.constant.account.AccountPermissionType;
import com.troy.trade.api.model.dto.in.account.AccountPermissionReqDto;
import com.troy.trade.ws.constants.Constant;
import com.troy.trade.ws.factory.StreamingExchangeServiceFactory;
import com.troy.trade.ws.feign.AccountApiFeign;
import com.troy.trade.ws.feign.MarketExchangeClient;
import com.troy.trade.ws.feign.MarketFuturesExchangeClient;
import com.troy.trade.ws.model.dto.in.ValidateDto;
import com.troy.trade.ws.model.dto.out.ResponseDto;
import com.troy.trade.ws.model.enums.MethodEnum;
import com.troy.trade.ws.server.NotificationService;
import com.troy.trade.ws.service.streaming.IStreamingExchangeService;
import com.troy.user.client.auth.TokenConverter;
import com.troy.user.dto.out.auth.token.CheckTokenResData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 *
 */
@Component
@Slf4j
public class BusinessMethodsUtil {

    @Autowired
    private TokenConverter tokenConverter;

    @Autowired
    private AccountApiFeign accountApiFeign;

    @Autowired
    private NotificationService notificationService;


    public static ResponseDto failResponse(String message){
        return new ResponseDto(WebSocketErrorCode.FAIL.getCode(),message,null);
    }

    public static ResponseDto failResponse(WebSocketErrorCode webSocketErrorCode){
        return new ResponseDto(webSocketErrorCode.getCode(),null,null);
    }

    public static ResponseDto failResponse(WebSocketErrorCode webSocketErrorCode,String method){
        //String code, String msg, String method, String result
        //String code, String result, String method
        return new ResponseDto(webSocketErrorCode.getCode(),webSocketErrorCode.getMsg(),method);
    }

    public static ResponseDto successResponse(String method){
        return new ResponseDto(WebSocketErrorCode.SUCCESS.getCode(),WebSocketErrorCode.SUCCESS.getMsg(),method,"");
    }

    /**
     * 建立连接前验证
     * @param sha
     * @return
     */
    public static boolean presenceChannelVali(StompHeaderAccessor sha){
        StompCommand stompCommand = sha.getCommand();
        if(stompCommand != StompCommand.CONNECT
                && stompCommand != StompCommand.DISCONNECT){
            return true;
        }

        /**
         * 验证交易所
         */
        ExchangeCode exchangeCode = valiExchCode(sha);
        if(null == exchangeCode){
            return false;
        }

        StreamingExchangeServiceFactory streamingExchangeServiceFactory = ApplicationContextUtil.getBean(StreamingExchangeServiceFactory.class);
        IStreamingExchangeService streamingExchangeService = streamingExchangeServiceFactory.getStreamingExchangeService(exchangeCode);
        ValidateDto validateDto = new ValidateDto();
        String symbol = BusinessMethodsUtil.getPair(sha);
        validateDto.setSymbol(symbol);
        return streamingExchangeService.validate(validateDto);
    }

    private static ExchangeCode valiExchCode(StompHeaderAccessor sha){
        List<String> exchCodes = sha.getNativeHeader("exchCode");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            log.info("调用BusinessMethodsUtil.valiExchCode 验证获取exchangeCode,入参：sha="+objectMapper.writeValueAsString(sha));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String exchCode = null;
        if (!CollectionUtils.isEmpty(exchCodes)) {
            exchCode = exchCodes.get(0);
            if (StringUtils.isBlank(exchCode)) {
                return null;
            }
        }else{
            return null;
        }
        ExchangeCode exchangeCode = ExchangeCode.getExchangeCode(exchCode.toLowerCase());
        return exchangeCode;
    }

    public static String getPair(StompHeaderAccessor sha){
        String pair = null;
        List<String> pairList = sha.getNativeHeader("pair");
        if (!CollectionUtils.isEmpty(pairList)) {
            pair = pairList.get(0);
        }
        return pair;
    }

    public static String getAccountId(StompHeaderAccessor sha){
        String clientId = null;
        List<String> clientids = sha.getNativeHeader("accountId");
        if (!CollectionUtils.isEmpty(clientids)) {
            clientId = clientids.get(0);
        }
        return clientId;
    }

    public static String getSessionId(SimpMessageHeaderAccessor sha){
        return sha.getSessionId();
    }

    public static String getExchCode(StompHeaderAccessor sha){
        List<String> exchCodes = sha.getNativeHeader("exchCode");
        String exchCode = null;
        if (!CollectionUtils.isEmpty(exchCodes)) {
            exchCode = exchCodes.get(0);
        }
        return exchCode;
    }

    /**
     * 获取Feign服务
     * @return
     */
    public static MarketExchangeClient fetchSpotFeignServer(){
        String beanName = null;
        MarketExchangeClient marketExchangeClientFeign = ApplicationContextUtil.getBean(MarketExchangeClient.class);
        log.info("待调用BeanName：{}", beanName);
        return marketExchangeClientFeign;
    }

    /**
     * 获取Feign服务
     * @return
     */
    public static MarketFuturesExchangeClient fetchFuturesFeignServer(){
        String beanName = null;
        MarketFuturesExchangeClient marketFuturesExchangeClientFeign = ApplicationContextUtil.getBean(MarketFuturesExchangeClient.class);
        log.info("待调用BeanName：{}", beanName);
        return marketFuturesExchangeClientFeign;
    }

    /**
     * 验证token并返回用户ID
     * @param token
     * @param key
     * @param destination
     * @return
     */
    public Long valiToken(String token,String key,String destination){
        CheckTokenResData checkTokenResData = this.valiToken(token);
        if(null == checkTokenResData
                || null == checkTokenResData.getUserId()){
            ResponseDto responseDto = notificationService.turnFailNotification(WebSocketErrorCode.TOKEN_FAIL, MethodEnum.ORDER_SUBSCRIBE);
            notificationService.broadcast(key,destination,responseDto);
            return null;
        }
        log.info("用户私有频道订阅，做token验证，验证token后用户信息："+ JSONObject.toJSONString(checkTokenResData));
        return checkTokenResData.getUserId();
    }

    /**
     * 验证token
     * @param token
     * @return
     */
    private CheckTokenResData valiToken(String token){
        CheckTokenResData checkTokenResData = null;
        try{
            log.info("用户私有频道订阅，做token验证入参token="+token);
            checkTokenResData = this.tokenConverter.convertAccessToken(token);
        }catch (Throwable throwable){
            log.error("用户私有频道订阅，做token验证，验证token异常，异常信息：",throwable);
        }
        return checkTokenResData;
    }

    /**
     * 账户信息验证
     * @param userId
     * @param accountId
     * @return
     */
    public boolean valiAccount(Long userId,String accountId,String token,String key,String destination){
        /**
         * 验证当前账户是否属于当前用户
         */
        AccountPermissionReqDto accountPermissionReqDto = new AccountPermissionReqDto();
        accountPermissionReqDto.setAccountId(Long.parseLong(accountId));
        accountPermissionReqDto.setUserId(userId);
        accountPermissionReqDto.setToken(token);
        accountPermissionReqDto.setAccountPermissionType(AccountPermissionType.READ);

        ReqHead reqHead = new ReqHead();
        reqHead.setClientId(Constant.CLIENTID);

        Req<AccountPermissionReqDto> req = ReqFactory.getInstance().createReq(reqHead,accountPermissionReqDto);
        log.info("用户私有频道订阅，当前用户对应的账户信息查询入参："+ JSONObject.toJSONString(req));
        Res<ResData> resDataRes = accountApiFeign.hasAccountPermission(req);
        log.info("用户私有频道订阅，当前用户对应的账户信息查询返回："+ JSONObject.toJSONString(resDataRes));

        if(null == resDataRes || !resDataRes.isSuccess()){
            ResponseDto responseDto = notificationService.turnFailNotification(WebSocketErrorCode.AUTHORIZATION_FAIL,MethodEnum.ORDER_SUBSCRIBE);
            notificationService.broadcast(key,destination,responseDto);
            return false;
        }
        return true;
    }

    /**
     * 获取合约信息存储map中的key
     * @param symbol - 交易对名称，如：BTC/USD
     * @param alias - 交易对类型，如：this_week
     * @return
     */
    public static String getContractKey(String symbol,String alias){
        return symbol+Constant.ACCOUNT_SESSION_KEY_SEPARATOR+alias;
    }


}

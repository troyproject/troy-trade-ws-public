package com.troy.trade.ws.server;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.commons.exception.enums.StateTypeSuper;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.utils.Assert;
import com.troy.commons.utils.EnumUtils;
import com.troy.trade.ws.factory.StreamingExchangeServiceFactory;
import com.troy.trade.ws.model.constant.Constant;
import com.troy.trade.ws.model.dto.in.*;
import com.troy.trade.ws.model.dto.out.ResponseDto;
import com.troy.trade.ws.service.IBalanceService;
import com.troy.trade.ws.service.IOpenOrderService;
import com.troy.trade.ws.service.streaming.IStreamingExchangeService;
import com.troy.trade.ws.util.BusinessMethodsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * WebSocketServer
 * webSocket 订阅
 */
@Controller
@Slf4j
public class WebSocketServer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private StreamingExchangeServiceFactory streamingExchangeServiceFactory;

    @Autowired
    private BusinessMethodsUtil businessMethodsUtil;

    @Autowired
    private IOpenOrderService openOrderService;

    @Autowired
    private IBalanceService balanceService;

    /**
     * 最新成交（实时成交）订阅
     *
     * @return
     */
    @MessageMapping("/topic/xxxxxs")
    public ResponseDto trades(RequestDto<TradeSubscribe> tradeSubscribeRequestBody, SimpMessageHeaderAccessor sha) {
        log.info("订阅最新成交信息，订阅成功，开始做数据推送====");
        try {
            log.info("订阅最新成交信息，订阅成功，入参：{}", objectMapper.writeValueAsString(tradeSubscribeRequestBody));
        }catch (Throwable throwable){
            log.error("订阅最新成交信息，订阅异常，异常信息：",throwable);
        }
        TradeSubscribe tradeSubscribe = tradeSubscribeRequestBody.getParams();
        tradeSubscribe.setSessionId(BusinessMethodsUtil.getSessionId(sha));
        String exchCode = tradeSubscribe.getExchCode();
        ExchangeCode exchangeCode = EnumUtils.getEnumByCode(exchCode.toLowerCase(),ExchangeCode.class);

        log.info("订阅最新成交信息，订阅成功，exchangeCode：{}", exchangeCode);

        Assert.notNull(exchangeCode, StateTypeSuper.FAIL_PARAMETER,"");
        tradeSubscribe.setExchangeCode(exchangeCode);
        IStreamingExchangeService streamingExchangeService = streamingExchangeServiceFactory.getStreamingExchangeService(exchangeCode);
        streamingExchangeService.tradeSubscribe(tradeSubscribeRequestBody);
        logger.info("历史成交记录订阅 exchCode:{},symbol:{}", exchCode, tradeSubscribe.getSymbol());
        ResponseDto responseDto = BusinessMethodsUtil.successResponse(Constant.SUCCESS);
        return responseDto;
    }

    /**
     * 最新价（买卖挂单）订阅
     */
    @MessageMapping("/topic/depth")
    public ResponseDto depthSubscribe(RequestDto<DepthSubscribe> depthSubscribeRequestBody, SimpMessageHeaderAccessor sha) {
        log.info("订阅盘口信息，订阅成功，开始做数据推送====");
        try {
            log.info("订阅盘口信息，订阅成功，入参：{}", objectMapper.writeValueAsString(depthSubscribeRequestBody));
        }catch (Throwable throwable){
            log.error("订阅盘口信息，订阅成功，异常信息：",throwable);
        }
        DepthSubscribe depthSubscribe = depthSubscribeRequestBody.getParams();
        depthSubscribe.setSessionId(BusinessMethodsUtil.getSessionId(sha));
        String exchCode = depthSubscribe.getExchCode();
        ExchangeCode exchangeCode = EnumUtils.getEnumByCode(exchCode.toLowerCase(),ExchangeCode.class);
        log.info("订阅最新成交信息，订阅成功，exchangeCode：{}", exchangeCode);
        Assert.notNull(exchangeCode, StateTypeSuper.FAIL_PARAMETER,"");
        depthSubscribe.setExchangeCode(exchangeCode);
        IStreamingExchangeService streamingExchangeService = streamingExchangeServiceFactory.getStreamingExchangeService(exchangeCode);
        streamingExchangeService.depthSubscribe(depthSubscribeRequestBody);
        logger.info("盘口订阅 exchCode:{},symbol:{},interval:{},limit:{}", exchCode, depthSubscribe.getSymbol(), depthSubscribeRequestBody.getParams().getInterval(), depthSubscribe.getLimit());
        ResponseDto responseDto = BusinessMethodsUtil.successResponse(Constant.SUCCESS);
        return responseDto;
    }

    /**
     * 我的委托订阅
     */
    @MessageMapping("/topic/order")
    public ResponseDto orderSubscribe(RequestDto<OrderSubscribe> orderSubscribeRequstBody, SimpMessageHeaderAccessor sha) {
        Assert.notNull(orderSubscribeRequstBody, "订阅当前挂单请求参数不能为空");

        OrderSubscribe orderSubscribe = orderSubscribeRequstBody.getParams();
        log.info("用户当前委托订阅，入参："+ JSONObject.toJSONString(orderSubscribe));
        String sessionId = BusinessMethodsUtil.getSessionId(sha);
        String exchCode = orderSubscribe.getExchCode();


        String symbol = orderSubscribe.getSymbol();
        String accountId = orderSubscribe.getAccountId();

        if(StringUtils.isBlank(exchCode)
                || StringUtils.isBlank(symbol)
                || StringUtils.isBlank(accountId)){
            log.error("用户当前委托订阅,订阅当前挂单信息失败，失败原因：必填参数为空");
            return new ResponseDto();
        }

        ExchangeCode exchangeCode = EnumUtils.getEnumByCode(exchCode,ExchangeCode.class);
        Assert.notNull(exchangeCode, StateTypeSuper.FAIL_PARAMETER,"");
        orderSubscribe.setExchangeCode(exchangeCode);
        /**
         * 验证订阅参数
         */
        boolean futuresBo = NotificationService.isFutures(exchangeCode);
        if(futuresBo){//是合约账户订阅，验证合约类型
            if(null == orderSubscribe.getAlias()){
                log.error("用户当前委托订阅,订阅当前挂单信息失败，失败原因：必填参数为空");
                return new ResponseDto();
            }
        }

        String key = SessionUtil.genKey(sessionId, exchCode, symbol);

        String destination = NotificationService.getOrderDestination(orderSubscribe);
        log.info("用户当前委托订阅,send目标地址 key={},destination={}",key,destination);

        /**
         * token验证
         */
        String token = orderSubscribe.getAuthorization();
        Long userId = businessMethodsUtil.valiToken(token,key,destination);
        if(null == userId){
            log.error("用户当前委托订阅,订阅当前挂单信息失败，失败原因：token验证失败");
            return new ResponseDto();
        }

        /**
         * 验证用户和账户关系
         */
        boolean valiResultBo = businessMethodsUtil.valiAccount(userId,accountId,token,key,destination);
        if(!valiResultBo){
            log.error("用户当前委托订阅,订阅当前挂单信息失败，失败原因：验证用户和账户关系失败");
            return new ResponseDto();
        }

        /**
         * 保存当前账户订阅
         */
        SessionUtil.addClient(accountId,symbol,orderSubscribe.getAlias(),sessionId,futuresBo);//订阅成功 保存

        /**
         * 发送初始挂单列表
         */
        openOrderService.subscribe(orderSubscribe, key,destination);
        return new ResponseDto();
    }

    /**
     * 账户余额订阅
     */
    @MessageMapping("/topic/balance")
    public ResponseDto balanceSubscribe(RequestDto<BalanceSubscribe> balanceSubscribeRequestDto, SimpMessageHeaderAccessor sha) {
        Assert.notNull(balanceSubscribeRequestDto, "订阅账户余额请求参数不能为空");

        BalanceSubscribe balanceSubscribe = balanceSubscribeRequestDto.getParams();
        log.info("用户当前余额订阅，入参："+ JSONObject.toJSONString(balanceSubscribe));

        String sessionId = BusinessMethodsUtil.getSessionId(sha);
        String exchCode = balanceSubscribe.getExchCode();

        String symbol = balanceSubscribe.getSymbol();
        String accountId = balanceSubscribe.getAccountId();

        if(StringUtils.isBlank(exchCode)
                || StringUtils.isBlank(symbol)
                || StringUtils.isBlank(accountId)){
            log.error("用户当前余额订阅,订阅账户余额信息失败，失败原因：必填参数为空");
            return new ResponseDto();
        }

        ExchangeCode exchangeCode = EnumUtils.getEnumByCode(exchCode,ExchangeCode.class);
        Assert.notNull(exchangeCode, StateTypeSuper.FAIL_PARAMETER,"");
        balanceSubscribe.setExchangeCode(exchangeCode);

        /**
         * 验证订阅参数
         */
        boolean futuresBo = NotificationService.isFutures(exchangeCode);
        if(futuresBo){//是合约账户订阅，验证合约类型
            if(null == balanceSubscribe.getAlias()){
                log.error("用户当前余额订阅,订阅账户余额信息失败，失败原因：必填参数为空");
                return new ResponseDto();
            }
        }

        String key = SessionUtil.genKey(sessionId, exchCode, symbol);

        String destination = NotificationService.getBalanceDestination(balanceSubscribe);
        log.info("用户当前余额订阅,send目标地址 key={},destination={}",key,destination);

        /**
         * token验证
         */
        String token = balanceSubscribe.getAuthorization();
        Long userId = businessMethodsUtil.valiToken(token,key,destination);
        if(null == userId){
            log.error("用户当前余额订阅,订阅账户余额信息失败，失败原因：token验证失败");
            return new ResponseDto();
        }

        /**
         * 验证用户和账户关系
         */
        boolean valiResultBo = businessMethodsUtil.valiAccount(userId,accountId,token,key,destination);
        if(!valiResultBo){
            log.error("用户当前余额订阅,订阅账户余额信息失败，失败原因：验证用户和账户关系失败");
            return new ResponseDto();
        }

        SessionUtil.addClient(accountId,symbol,balanceSubscribe.getAlias(), sessionId,futuresBo);//订阅成功 保存

        /**
         * 发送初始挂单列表
         */
        balanceService.subscribe(balanceSubscribe,userId, key,destination);
        return new ResponseDto();
    }

}

package com.troy.trade.ws.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.in.ReqHead;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResList;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.enums.AliasEnum;
import com.troy.commons.exchange.model.enums.FuturesOrderSideEnum;
import com.troy.commons.utils.ApplicationContextUtil;
import com.troy.commons.utils.EnumUtils;
import com.troy.redis.RedisUtil;
import com.troy.trade.api.model.dto.in.order.OpenOrderListReqData;
import com.troy.trade.api.model.dto.out.order.OrderDetails;
import com.troy.trade.futures.api.model.dto.in.order.FuturesOpenOrderListReqData;
import com.troy.trade.futures.api.model.dto.out.order.FuturesOrderDetails;
import com.troy.trade.ws.api.model.dto.in.FuturesOpenOrderReqDto;
import com.troy.trade.ws.api.model.dto.in.OpenOrderReqDto;
import com.troy.trade.ws.api.model.dto.in.SpotOpenOrderReqDto;
import com.troy.trade.ws.api.model.dto.out.OpenOrderResDto;
import com.troy.trade.ws.constants.Constant;
import com.troy.trade.ws.feign.OrderApiFuturesFeign;
import com.troy.trade.ws.feign.OrderApiSpotFeign;
import com.troy.trade.ws.model.dto.in.OrderSubscribe;
import com.troy.trade.ws.model.dto.in.PrivateParamDto;
import com.troy.trade.ws.model.dto.out.ResponseDto;
import com.troy.trade.ws.model.enums.MethodEnum;
import com.troy.trade.ws.scheduled.InstrumentsSyncUtil;
import com.troy.trade.ws.server.NotificationService;
import com.troy.trade.ws.service.IOpenOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 当前挂单服务
 * @author dp
 */
@Component
@Slf4j
public class OpenOrderServiceImpl implements IOpenOrderService {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    InstrumentsSyncUtil instrumentsSyncUtil;

    @Autowired
    private OrderApiSpotFeign orderApiSpotFeign;

    @Autowired
    private OrderApiFuturesFeign orderApiFuturesFeign;

    @Override
    public OpenOrderResDto sendSpotOpenOrders(OpenOrderReqDto<SpotOpenOrderReqDto> openOrderReqDto) {
        if(null == openOrderReqDto){
            log.error("当前挂单状态信息推送失败，必填参数为空");
            return new OpenOrderResDto(false);
        }

        Long accountId = openOrderReqDto.getAccountId();
        ExchangeCode exchCode = openOrderReqDto.getExchCode();
        List<SpotOpenOrderReqDto> openOrders = openOrderReqDto.getOpenOrders();
        log.debug("当前挂单状态信息推送，入参：{}", JSONObject.toJSONString(openOrderReqDto));
        if (accountId == null
                || null == exchCode
                || CollectionUtils.isEmpty(openOrders)) {
            log.error("当前挂单状态信息推送失败，必填参数为空，accountId={},exchCode={}", accountId, exchCode);
            return new OpenOrderResDto(false);
        }

        PrivateParamDto<List<SpotOpenOrderReqDto>> privateParamDto = new PrivateParamDto();
        privateParamDto.setAccountId(openOrderReqDto.getAccountId());
        privateParamDto.setExchCode(exchCode.code());
        privateParamDto.setExchangeCode(exchCode);
        privateParamDto.setParams(openOrderReqDto.getOpenOrders());
        privateParamDto.setSymbol(openOrderReqDto.getOpenOrders().get(0).getSymbol());

        NotificationService notificationService = ApplicationContextUtil.getBean(NotificationService.class);
        notificationService.broadcastSpotOrder(privateParamDto);
        return new OpenOrderResDto(true);
    }

    @Override
    public OpenOrderResDto sendFuturesOpenOrders(OpenOrderReqDto<FuturesOpenOrderReqDto> openOrderReqDto) {
        if(null == openOrderReqDto){
            log.error("当前挂单状态信息推送失败，必填参数为空");
            return new OpenOrderResDto(false);
        }

        Long accountId = openOrderReqDto.getAccountId();
        ExchangeCode exchCode = openOrderReqDto.getExchCode();
        List<FuturesOpenOrderReqDto> openOrders = openOrderReqDto.getOpenOrders();
        log.debug("当前挂单状态信息推送，入参：{}", JSONObject.toJSONString(openOrderReqDto));
        if (accountId == null
                || null == exchCode
                || CollectionUtils.isEmpty(openOrders)) {
            log.error("当前挂单状态信息推送失败，必填参数为空，accountId={},exchCode={}", accountId, exchCode);
            return new OpenOrderResDto(false);
        }

        PrivateParamDto<List<FuturesOpenOrderReqDto>> privateParamDto = new PrivateParamDto();
        privateParamDto.setAccountId(openOrderReqDto.getAccountId());
        privateParamDto.setExchCode(exchCode.code());
        privateParamDto.setExchangeCode(exchCode);
        privateParamDto.setParams(openOrderReqDto.getOpenOrders());


        /**
         * 初始化币对类型字段
         */
        /**
         * 1、从缓存中获取币对名称及币对类型字段
         */
        FuturesOpenOrderReqDto futuresOpenOrderReqDto = openOrderReqDto.getOpenOrders().get(0);
        privateParamDto.setSymbol(futuresOpenOrderReqDto.getSymbol());

        String instrumentId = futuresOpenOrderReqDto.getFuturesCode();
        AliasEnum aliasEnum = instrumentsSyncUtil.getAliasCode(exchCode,instrumentId);
        privateParamDto.setAlias(aliasEnum);

        /**
         * 2、做数据推送
         */
        NotificationService notificationService = ApplicationContextUtil.getBean(NotificationService.class);
        notificationService.broadcastFuturesOrder(privateParamDto);
        return new OpenOrderResDto(true);
    }

    /**
     * 当前挂单列表初始列表推送
     * @param orderSubscribe
     */
    @Override
    public void subscribe(OrderSubscribe orderSubscribe,String key,String destination){
        try {
            ExchangeCode exchCode = orderSubscribe.getExchangeCode();
            boolean futuresBo = NotificationService.isFutures(exchCode);
            if(futuresBo){//合约
                futuresSubSendOpenOrder(orderSubscribe,key,destination);
            }else{//非合约
                spotSubSendOpenOrder(orderSubscribe,key,destination);
            }
        }catch (Throwable throwable){
            log.error("用户当前委托订阅，查询用户初始当前挂单列表异常，异常信息：",throwable);
        }
    }

    /**
     * 做现货当前挂单推送处理
     * @param orderSubscribe
     */
    private void futuresSubSendOpenOrder(OrderSubscribe orderSubscribe,String key,String destination){
        ExchangeCode exchCode = orderSubscribe.getExchangeCode();
        String symbol = orderSubscribe.getSymbol();
        AliasEnum aliasEnum = orderSubscribe.getAlias();
        String accountId = orderSubscribe.getAccountId();
        //查询当前挂单列表并推送
        String token = orderSubscribe.getAuthorization();

        //根据symbol和alias获取合约ID
        String instrumentId = instrumentsSyncUtil.getInstrumentId(exchCode,symbol,aliasEnum);

        FuturesOpenOrderListReqData futuresOpenOrderListReqData = new FuturesOpenOrderListReqData();
        futuresOpenOrderListReqData.setUserAcctId(Long.parseLong(accountId));
        futuresOpenOrderListReqData.setSymbol(symbol);
        futuresOpenOrderListReqData.setFuturesCode(instrumentId);
        futuresOpenOrderListReqData.setExchCode(exchCode.code());
        futuresOpenOrderListReqData.setToken(token);

        ReqHead reqHead = new ReqHead();
        reqHead.setClientId(Constant.CLIENTID);

        Req<FuturesOpenOrderListReqData> openOrderListReqDataReq = ReqFactory.getInstance().createReq(reqHead, futuresOpenOrderListReqData);
        log.info("用户当前委托订阅，调用当前订单列表接口入参：" + JSONObject.toJSONString(openOrderListReqDataReq));
        Res<ResList<FuturesOrderDetails>> order = orderApiFuturesFeign.queryCurrentOrder(openOrderListReqDataReq);

        boolean successBo = true;
        if (null == order) {
            successBo = false;
            log.info("调用trade系统查询当前委托列表失败，trade系统返回空");
        }else if (!order.isSuccess()) {
            successBo = false;
            try {
                log.info("调用trade系统查询当前委托列表失败，trade系统返回：" + objectMapper.writeValueAsString(order));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        if(!successBo){
            NotificationService notificationService = ApplicationContextUtil.getBean(NotificationService.class);
            ResponseDto responseDto = notificationService.turnNotification("", MethodEnum.ORDER_SUBSCRIBE.getType());
            notificationService.broadcast(key,destination,responseDto);
            return;
        }

        log.info("用户当前委托订阅，调用当前订单列表接口返回：" + JSONObject.toJSONString(order));
        ResList<FuturesOrderDetails> orderDetailsResList = order.getData();
        List<FuturesOrderDetails> orderDetailsList = orderDetailsResList.getList();
        int size = orderDetailsList == null ? 0 : orderDetailsList.size();
        if(size<=0){
            NotificationService notificationService = ApplicationContextUtil.getBean(NotificationService.class);
            ResponseDto responseDto = notificationService.turnNotification("", MethodEnum.ORDER_SUBSCRIBE.getType());
            notificationService.broadcast(key,destination,responseDto);
            return;
        }

        OpenOrderReqDto<FuturesOpenOrderReqDto> openOrderReqDto = new OpenOrderReqDto<>();
        Long accountIdLong = Long.parseLong(accountId);
        openOrderReqDto.setAccountId(accountIdLong);
        openOrderReqDto.setExchCode(exchCode);

        FuturesOrderDetails futuresOrderDetails = null;

        List<FuturesOpenOrderReqDto> openOrders = new ArrayList<>();
        FuturesOpenOrderReqDto openOrder = null;
        for (int i = 0; i < size; i++) {
            futuresOrderDetails = orderDetailsList.get(i);
            openOrder = new FuturesOpenOrderReqDto();
            openOrder.setFuturesTransId(futuresOrderDetails.getFuturesTransId());
            openOrder.setExchCode(exchCode.code());
            openOrder.setAccountId(accountIdLong);
            openOrder.setOrderId(futuresOrderDetails.getOrderId());
            openOrder.setCreateTime(futuresOrderDetails.getThirdCreateTime());
            openOrder.setSymbol(futuresOrderDetails.getSymbol());
            openOrder.setFuturesCode(futuresOrderDetails.getFuturesCode());
            openOrder.setDirection(EnumUtils.getEnumByCode(futuresOrderDetails.getDirection(), FuturesOrderSideEnum.class));
            openOrder.setSize(futuresOrderDetails.getSize());
            openOrder.setPrice(futuresOrderDetails.getPrice());
            openOrder.setFilledSize(futuresOrderDetails.getFilledSize());
            openOrder.setFilledPrice(futuresOrderDetails.getFilledPrice());
            openOrder.setStatus(futuresOrderDetails.getStatus());
            openOrder.setDeposit(futuresOrderDetails.getDeposit());

            openOrders.add(openOrder);
        }

        openOrderReqDto.setOpenOrders(openOrders);
        this.sendFuturesOpenOrders(openOrderReqDto);
    }

    /**
     * 做现货当前挂单推送处理
     * @param orderSubscribe
     */
    private void spotSubSendOpenOrder(OrderSubscribe orderSubscribe,String key,String destination){
        ExchangeCode exchCode = orderSubscribe.getExchangeCode();
        String symbol = orderSubscribe.getSymbol();
        String accountId = orderSubscribe.getAccountId();
        //查询当前挂单列表并推送
        String token = orderSubscribe.getAuthorization();
        OpenOrderListReqData openOrderListReqData = new OpenOrderListReqData();
        openOrderListReqData.setUserAcctId(Long.parseLong(accountId));
        openOrderListReqData.setSymbol(symbol);
        openOrderListReqData.setExchCode(exchCode.code());
        openOrderListReqData.setToken(token);

        ReqHead reqHead = new ReqHead();
        reqHead.setClientId(Constant.CLIENTID);

        Req<OpenOrderListReqData> openOrderListReqDataReq = ReqFactory.getInstance().createReq(reqHead, openOrderListReqData);
        log.info("用户当前委托订阅，调用当前订单列表接口入参：" + JSONObject.toJSONString(openOrderListReqDataReq));
        Res<ResList<OrderDetails>> order = orderApiSpotFeign.queryCurrentOrder(openOrderListReqDataReq);

        boolean successBo = true;
        if (null == order) {
            successBo = false;
            log.info("调用trade系统查询当前委托列表失败，trade系统返回空");
        }else if (!order.isSuccess()) {
            successBo = false;
            try {
                log.info("调用trade系统查询当前委托列表失败，trade系统返回：" + objectMapper.writeValueAsString(order));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        if(!successBo){
            NotificationService notificationService = ApplicationContextUtil.getBean(NotificationService.class);
            ResponseDto responseDto = notificationService.turnNotification("", MethodEnum.ORDER_SUBSCRIBE.getType());
            notificationService.broadcast(key,destination,responseDto);
            return;
        }

        log.info("用户当前委托订阅，调用当前订单列表接口返回：" + JSONObject.toJSONString(order));
        ResList<OrderDetails> orderDetailsResList = order.getData();
        List<OrderDetails> orderDetailsList = orderDetailsResList.getList();
        int size = orderDetailsList == null ? 0 : orderDetailsList.size();
        if(size<=0){
            NotificationService notificationService = ApplicationContextUtil.getBean(NotificationService.class);
            ResponseDto responseDto = notificationService.turnNotification("", MethodEnum.ORDER_SUBSCRIBE.getType());
            notificationService.broadcast(key,destination,responseDto);
            return;
        }

        OpenOrderReqDto openOrderReqDto = new OpenOrderReqDto();
        openOrderReqDto.setAccountId(Long.parseLong(accountId));
        openOrderReqDto.setExchCode(exchCode);

        OrderDetails orderDetails = null;
        List<SpotOpenOrderReqDto> openOrders = new ArrayList<>();
        SpotOpenOrderReqDto openOrder = null;
        for (int i = 0; i < size; i++) {
            orderDetails = orderDetailsList.get(i);
            openOrder = new SpotOpenOrderReqDto();
            openOrder.setSpotTransId(orderDetails.getSpotTransId());
            openOrder.setOrderId(orderDetails.getOrderId());
            openOrder.setThirdCreateTime(orderDetails.getThirdCreateTime());
            openOrder.setSide(orderDetails.getSide());
            openOrder.setSymbol(orderDetails.getSymbol());
            openOrder.setPrice(orderDetails.getPrice());
            openOrder.setAmount(orderDetails.getAmount());
            openOrder.setTotalCashAmount(orderDetails.getTotalCashAmount());
            openOrder.setFilledAmount(orderDetails.getFilledAmount());
            openOrder.setLeftAmount(orderDetails.getLeftAmount());
            openOrder.setStatus(orderDetails.getStatus());
            openOrder.setFilledAmount(orderDetails.getFilledAmount());

            openOrders.add(openOrder);
        }

        openOrderReqDto.setOpenOrders(openOrders);
        this.sendSpotOpenOrders(openOrderReqDto);
    }
}

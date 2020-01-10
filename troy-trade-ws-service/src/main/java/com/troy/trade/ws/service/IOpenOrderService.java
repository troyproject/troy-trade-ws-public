package com.troy.trade.ws.service;

import com.troy.trade.ws.api.model.dto.in.FuturesOpenOrderReqDto;
import com.troy.trade.ws.api.model.dto.in.OpenOrderReqDto;
import com.troy.trade.ws.api.model.dto.in.SpotOpenOrderReqDto;
import com.troy.trade.ws.api.model.dto.out.OpenOrderResDto;
import com.troy.trade.ws.model.dto.in.OrderSubscribe;

/**
 * 当前挂单服务
 * @author dp
 */
public interface IOpenOrderService {

    /**
     * 发送消息
     * @param openOrderReqDto
     * @return
     */
    OpenOrderResDto sendSpotOpenOrders(OpenOrderReqDto<SpotOpenOrderReqDto> openOrderReqDto);

    /**
     * 发送消息
     * @param openOrderReqDto
     * @return
     */
    OpenOrderResDto sendFuturesOpenOrders(OpenOrderReqDto<FuturesOpenOrderReqDto> openOrderReqDto);

    /**
     * 当前挂单列表推送
     * @param orderSubscribe
     *        key
     *        destination
     */
    void subscribe(OrderSubscribe orderSubscribe,String key,String destination);
}

package com.troy.trade.ws.service.rest;

import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.trade.ws.model.dto.in.DepthSubscribe;
import com.troy.trade.ws.model.dto.in.TradeSubscribe;
import com.troy.trade.ws.model.dto.out.depth.DepthResponse;
import com.troy.trade.ws.model.dto.out.trades.TradeDataResponse;

public interface IRestExchangeService {

    ExchangeCode getExchCode();

    DepthResponse orderBook(DepthSubscribe depthSubscribe) throws Exception ;

    TradeDataResponse trade(TradeSubscribe tradeSubscribe) throws Exception ;
}

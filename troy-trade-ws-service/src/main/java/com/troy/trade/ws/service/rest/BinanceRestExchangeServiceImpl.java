package com.troy.trade.ws.service.rest;

import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.out.Res;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.in.OrderBookReqDto;
import com.troy.commons.exchange.model.in.TradeHistoryReqDto;
import com.troy.commons.exchange.model.out.OrderBookResDto;
import com.troy.commons.exchange.model.out.TradeHistoryListResDto;
import com.troy.trade.ws.util.BusinessMethodsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BinanceRestExchangeServiceImpl extends BaseRestExchangeServiceImpl {


    @Override
    public ExchangeCode getExchCode() {
        return ExchangeCode.BINANCE;
    }

    @Override
    public Res<OrderBookResDto> getFeignOrderBook(Req<OrderBookReqDto> orderBookReqDtoReq) throws Exception {
        return BusinessMethodsUtil.fetchSpotFeignServer().getOrderBook(orderBookReqDtoReq);
    }

    @Override
    Res<TradeHistoryListResDto> getFeignTrades(Req<TradeHistoryReqDto> tradeHistoryReqDtoReq) throws Exception {
        return BusinessMethodsUtil.fetchSpotFeignServer().getTrades(tradeHistoryReqDtoReq);
    }
}

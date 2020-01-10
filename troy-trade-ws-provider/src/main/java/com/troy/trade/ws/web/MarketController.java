package com.troy.trade.ws.web;

import com.troy.commons.BaseController;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.out.Res;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.proxy.GreedyRequestProxy;
import com.troy.commons.utils.EnumUtils;
import com.troy.trade.ws.factory.RestExchangeServiceFactory;
import com.troy.trade.ws.model.dto.in.DepthSubscribe;
import com.troy.trade.ws.model.dto.in.TradeSubscribe;
import com.troy.trade.ws.model.dto.out.depth.DepthResponse;
import com.troy.trade.ws.model.dto.out.trades.TradeDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 深度信息查询接口
 */
@Slf4j
@RestController
public class MarketController extends BaseController {

    @Autowired
    RestExchangeServiceFactory restExchangeServiceFactory;

    @RequestMapping(value = "/xxxxx/dddddd/orderBook", method = RequestMethod.POST)
    public Res<DepthResponse> orderBook(@RequestBody Req<DepthSubscribe> req) {
        return super.process(req, (GreedyRequestProxy<DepthSubscribe, DepthResponse>) (reqHead, reqData) -> {
            String exchCode = reqData.getExchCode();
            ExchangeCode exchangeCode = EnumUtils.getEnumByCode(exchCode,ExchangeCode.class);
            reqData.setExchangeCode(exchangeCode);
            DepthResponse depthResponse = this.restExchangeServiceFactory.getRestExchangeService(exchangeCode).orderBook(reqData);
            return depthResponse;
        });
    }

    @RequestMapping(value = "/xxxxx/dddddd/xxxxxs", method = RequestMethod.POST)
    public Res<TradeDataResponse> trades(@RequestBody Req<TradeSubscribe> req) {
        return super.process(req, (GreedyRequestProxy<TradeSubscribe, TradeDataResponse>) (reqHead, reqData) -> {
            String exchCode = reqData.getExchCode();
            ExchangeCode exchangeCode = EnumUtils.getEnumByCode(exchCode,ExchangeCode.class);
            reqData.setExchangeCode(exchangeCode);
            TradeDataResponse tradeDataResponse = this.restExchangeServiceFactory.getRestExchangeService(exchangeCode).trade(reqData);
            return tradeDataResponse;
        });
    }
}

package com.troy.trade.ws.service.rest;

import com.alibaba.fastjson.JSONObject;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResHead;
import com.troy.commons.exception.business.BusinessException;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.in.OrderBookReqDto;
import com.troy.commons.exchange.model.out.OrderBookResDto;
import com.troy.trade.ws.constants.Constant;
import com.troy.trade.ws.model.dto.in.DepthSubscribe;
import com.troy.trade.ws.model.dto.out.depth.DepthResponse;
import com.troy.trade.ws.util.BusinessMethodsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class BinanceRestExchangeServiceImplTest {

    @Test
    public void orderBook() {

        DepthSubscribe depthSubscribe = new DepthSubscribe();
        depthSubscribe.setExchCode(ExchangeCode.HUOBI.code());
        depthSubscribe.setExchangeCode(ExchangeCode.HUOBI);
        depthSubscribe.setSymbol("BTC/USDT");

        ExchangeCode exchangeCode = depthSubscribe.getExchangeCode();
        Integer limit = depthSubscribe.getLimit();
        if(null == limit){
            limit = Constant.depthDefaultLimit.get(exchangeCode.code());
        }

        String symbol = depthSubscribe.getSymbol();
        OrderBookReqDto orderBookReqDto = new OrderBookReqDto();
        orderBookReqDto.setExchCode(exchangeCode);
        orderBookReqDto.setLimit(limit);
        orderBookReqDto.setSymbol(symbol);
        Req<OrderBookReqDto> orderBookReqDtoReq = ReqFactory.getInstance().createReq(orderBookReqDto);
        Res<OrderBookResDto> orderBookResDtoRes = BusinessMethodsUtil.fetchSpotFeignServer().getOrderBook(orderBookReqDtoReq);
        if(orderBookResDtoRes.isSuccess()){
            //截串
            OrderBookResDto orderBookResDto = orderBookResDtoRes.getData();
            List<List<String>> asks = orderBookResDto.getAsks();
            List<List<String>> bids = orderBookResDto.getBids();

            //String symbol, boolean fullData, List<List<String>> asks,List<List<String>> bids
            DepthResponse depthResponse = new DepthResponse(symbol,true,asks,bids);
            System.out.println(JSONObject.toJSONString(depthResponse));
        }else{
            log.error("调用"+exchangeCode.code()+"查询买卖挂单失败，失败原因：",orderBookResDtoRes.getHead().getDepict());
            ResHead head = orderBookResDtoRes.getHead();
            throw new BusinessException(head.getDepict());
        }
    }

}
package com.troy.trade.ws.service.rest;

import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResHead;
import com.troy.commons.exception.business.BusinessException;
import com.troy.commons.exception.enums.StateTypeSuper;
import com.troy.commons.exception.verification.VerificationException;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.enums.AliasEnum;
import com.troy.commons.exchange.model.in.OrderBookReqDto;
import com.troy.commons.exchange.model.in.TradeHistoryReqDto;
import com.troy.commons.exchange.model.out.OrderBookResDto;
import com.troy.commons.exchange.model.out.TradeHistoryListResDto;
import com.troy.commons.exchange.model.out.TradeHistoryResDto;
import com.troy.trade.ws.constants.Constant;
import com.troy.trade.ws.model.dto.in.DepthSubscribe;
import com.troy.trade.ws.model.dto.in.TradeSubscribe;
import com.troy.trade.ws.model.dto.out.TradeResponse;
import com.troy.trade.ws.model.dto.out.depth.DepthResponse;
import com.troy.trade.ws.model.dto.out.trades.TradeDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class BaseRestExchangeServiceImpl implements IRestExchangeService {

    /**
     * 调用feign中的orderBook查询方法
     * @param orderBookReqDtoReq
     * @return
     * @throws Exception
     */
    abstract Res<OrderBookResDto> getFeignOrderBook(Req<OrderBookReqDto> orderBookReqDtoReq) throws Exception;

    /**
     * 调用feign中的trades查询方法
     * @param tradeHistoryReqDtoReq
     * @return
     * @throws Exception
     */
    abstract Res<TradeHistoryListResDto> getFeignTrades(Req<TradeHistoryReqDto> tradeHistoryReqDtoReq) throws Exception;

    @Override
    public DepthResponse orderBook(DepthSubscribe depthSubscribe) throws Exception {
        if(null == depthSubscribe
                || null == depthSubscribe.getExchangeCode()
                || StringUtils.isBlank(depthSubscribe.getSymbol())){
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        ExchangeCode exchangeCode = depthSubscribe.getExchangeCode();
        Integer limit = depthSubscribe.getLimit();
        if(null == limit){
            limit = Constant.depthDefaultLimit.get(exchangeCode.code());
        }

        String symbol = depthSubscribe.getSymbol();

        AliasEnum aliasEnum = depthSubscribe.getAlias();
        String alias = null;
        if(null != aliasEnum){
            alias = aliasEnum.code();
        }

        OrderBookReqDto orderBookReqDto = new OrderBookReqDto();
        orderBookReqDto.setExchCode(exchangeCode);
        orderBookReqDto.setLimit(limit);
        orderBookReqDto.setSymbol(symbol);
        orderBookReqDto.setAlias(alias);
        Req<OrderBookReqDto> orderBookReqDtoReq = ReqFactory.getInstance().createReq(orderBookReqDto);
        Res<OrderBookResDto> orderBookResDtoRes = this.getFeignOrderBook(orderBookReqDtoReq);
        if(orderBookResDtoRes.isSuccess()){
            //截串
            OrderBookResDto orderBookResDto = orderBookResDtoRes.getData();
            List<List<String>> asks = orderBookResDto.getAsks();
            List<List<String>> bids = orderBookResDto.getBids();

            int asksSize = asks == null?0:asks.size();
            if(asksSize>limit){
                asks = asks.subList((asksSize-limit),asksSize);
            }

            int bidSize = bids == null?0:bids.size();
            if(bidSize>limit){
                bids = bids.subList(0,limit);
            }

            //String symbol, boolean fullData, List<List<String>> asks,List<List<String>> bids
            DepthResponse depthResponse = new DepthResponse(symbol,true,asks,bids);
            return depthResponse;
        }else{
            log.error("调用"+exchangeCode.code()+"查询买卖挂单失败，失败原因：",orderBookResDtoRes.getHead().getDepict());
            ResHead head = orderBookResDtoRes.getHead();
            throw new BusinessException(head.getDepict());
        }
    }

    @Override
    public TradeDataResponse trade(TradeSubscribe tradeSubscribe) throws Exception {
        if(null == tradeSubscribe
                || null == tradeSubscribe.getExchangeCode()
                || StringUtils.isBlank(tradeSubscribe.getSymbol())){
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        ExchangeCode exchangeCode = tradeSubscribe.getExchangeCode();
        Integer limit = tradeSubscribe.getLimit();
        if(null == limit){
            limit = Constant.depthDefaultLimit.get(exchangeCode.code());
        }

        String symbol = tradeSubscribe.getSymbol();
        AliasEnum aliasEnum = tradeSubscribe.getAlias();
        String alias = null;
        if(null != aliasEnum){
            alias = aliasEnum.code();
        }

        TradeHistoryReqDto tradeHistoryReqDto = new TradeHistoryReqDto();
        tradeHistoryReqDto.setExchCode(exchangeCode);
        tradeHistoryReqDto.setSymbol(symbol);
        tradeHistoryReqDto.setLimit(limit);
        tradeHistoryReqDto.setAlias(alias);
        Req<TradeHistoryReqDto> tradeHistoryReqDtoReq = ReqFactory.getInstance().createReq(tradeHistoryReqDto);

        Res<TradeHistoryListResDto> orderBookResDtoRes = this.getFeignTrades(tradeHistoryReqDtoReq);
        if(orderBookResDtoRes.isSuccess()){
            TradeHistoryListResDto tradeHistoryListResDto = orderBookResDtoRes.getData();
            List<TradeHistoryResDto> tradeHistoryResDtoList = tradeHistoryListResDto.getTradeHistoryResDtoList();
            List<TradeResponse> tradeResponseList = new ArrayList<>();
            tradeHistoryResDtoList.stream().forEach(tradeHistoryResDto -> {
                //Integer type, String amount, String symbol, String price, Long timestamp, String id
                tradeResponseList.add(new TradeResponse(tradeHistoryResDto.getOrderSide().code(),
                        tradeHistoryResDto.getAmount(),
                        tradeHistoryResDto.getSymbol(),tradeHistoryResDto.getPrice(),
                        tradeHistoryResDto.getTimestamp(),tradeHistoryResDto.getId()));
            });

            int tradeResponseSize = tradeResponseList == null?0:tradeResponseList.size();
            List<TradeResponse> tempTradeResponseList;
            if(tradeResponseSize>limit){
                tempTradeResponseList = tradeResponseList.subList(0,limit);
            }else{
                tempTradeResponseList = tradeResponseList;
            }
            return new TradeDataResponse(tempTradeResponseList);
        }else{
            log.error("调用"+exchangeCode.code()+"查询最新成交失败，失败原因：",orderBookResDtoRes.getHead().getDepict());
            ResHead head = orderBookResDtoRes.getHead();
            throw new BusinessException(head.getDepict());
        }
    }
}

package com.troy.trade.ws.thread;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.out.Res;
import com.troy.commons.exchange.model.enums.AliasEnum;
import com.troy.commons.utils.ApplicationContextUtil;
import com.troy.commons.utils.DateUtils;
import com.troy.trade.api.model.constant.account.TradeConstant;
import com.troy.trade.futures.api.model.dto.in.account.FuturesBalanceReqDto;
import com.troy.trade.futures.api.model.dto.out.account.FuturesAccountInfoResDto;
import com.troy.trade.futures.api.model.dto.out.account.FuturesBalanceResDto;
import com.troy.trade.ws.feign.FuturesAccountClient;
import com.troy.trade.ws.model.dto.in.BalancePushReqDto;
import com.troy.trade.ws.server.SessionUtil;
import com.troy.trade.ws.util.BalancePushUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 交易所交易对信息同步
 */
@Slf4j
public class FuturesBalancePushSyncExecute extends Action {

    private Long accountId;//当前要推送的账户ID

    private String accountIdStr;//当前要推送的账户ID字符串

    private String symbol;//当前要推送的交易对

    private AliasEnum alias;//当前交易对类型

    private FuturesAccountClient futuresAccountClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    public FuturesBalancePushSyncExecute() {
        super();
    }

    public FuturesBalancePushSyncExecute(Long accountId,String symbol,AliasEnum alias) {
        this.accountId = accountId;
        this.accountIdStr = String.valueOf(accountId);
        this.symbol = symbol;
        this.alias = alias;
    }

    public static FuturesBalancePushSyncExecute getInstance(Long accountId,String symbol,AliasEnum alias) {
        return new FuturesBalancePushSyncExecute(accountId,symbol,alias);
    }

    private void initParam(){
        this.futuresAccountClient = ApplicationContextUtil.getBean(FuturesAccountClient.class);
    }

    @Override
    public void execute() {

        try {
            long startTime = System.currentTimeMillis();
            log.info("订阅用户余额信息变动推送，做余额变动推送，入参：accountId={},symbol={},alias={}",accountId,symbol,alias);

            /**
             * 1、初始化参数信息
             */
            initParam();

            /**
             * 2、做数据验证，验证当前用户是否订阅
             */
            ConcurrentMap<String, Map<String,Set<String>>> accountSessionsMap = SessionUtil.getAccountSessionsMap(true);
            log.info("订阅用户余额信息变动推送，做余额变动推送，accountSessionsMap={}",accountSessionsMap);
            if(!accountSessionsMap.containsKey(accountIdStr)){//当前用户未订阅
                log.warn("订阅用户余额信息变动推送，当前用户未订阅，不做余额变动推送，accountId={},symbol={},alias={}",accountId,symbol,alias);
                return;
            }

            /**
             * 3、查询账户余额信息
             */
            FuturesBalanceReqDto futuresBalanceReqDto = FuturesBalanceReqDto.getInstance(accountId,symbol,alias, TradeConstant.ADMIN_USERID);
            Req<FuturesBalanceReqDto> futuresBalanceReqDtoReq = ReqFactory.getInstance().createReq(futuresBalanceReqDto);

            log.info("订阅用户余额信息变动推送，调用当前账户余额接口入参：" + JSONObject.toJSONString(futuresBalanceReqDtoReq));

            Res<FuturesAccountInfoResDto> futuresAccountInfoResDtoRes = futuresAccountClient.balance(futuresBalanceReqDtoReq);

            boolean successBo = true;
            if (null == futuresAccountInfoResDtoRes) {
                successBo = false;
                log.info("订阅用户余额信息变动推送，调用tradeFutures系统查询当前用户余额信息失败，tradeFutures系统返回空");
            }else if (!futuresAccountInfoResDtoRes.isSuccess()) {
                successBo = false;
                try {
                    log.info("订阅用户余额信息变动推送，调用tradeFutures系统查询当前用户余额信息失败，tradeFutures系统返回："
                            + objectMapper.writeValueAsString(futuresAccountInfoResDtoRes));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            if(!successBo){
                log.warn("订阅用户余额信息变动推送，查询当前用户余额信息失败，不做余额变动推送，accountId={},symbol={},alias={}",accountId,symbol,alias);
                return;
            }

            /**
             * 4、做账户余额信息推送
             */
            FuturesAccountInfoResDto futuresAccountInfoResDto = futuresAccountInfoResDtoRes.getData();
            BalancePushReqDto<FuturesBalanceResDto> balancePushReqDto = new BalancePushReqDto();
            balancePushReqDto.setAccountId(accountId);
            balancePushReqDto.setExchCode(futuresAccountInfoResDto.getExchCode());
            balancePushReqDto.setSymbol(symbol);
            balancePushReqDto.setAlias(alias);
            balancePushReqDto.setBalance(futuresAccountInfoResDto.getFuturesBalanceResDto());
            BalancePushUtil.sendFuturesBalance(balancePushReqDto);

            long endTime = System.currentTimeMillis();
            log.info("订阅用户余额信息变动推送------结束，入参：accountId={},symbol={},alias={}，当前时间:{}。耗时：{}毫秒。",
                    accountId,symbol,alias,
                    DateUtils.formatDate(new Date(), DateUtils.FORMAT_DATE_TIME),
                    (endTime - startTime));
        }catch (Throwable e) {
            String temp = "订阅用户余额信息变动推送：数据推送异常。当前时间 " + DateUtils.formatDate(new Date(), DateUtils.FORMAT_DATE_TIME);
            log.error(temp + "，异常信息：", e);
        }
    }
}

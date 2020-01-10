package com.troy.trade.ws.util;

import com.alibaba.fastjson.JSONObject;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.utils.ApplicationContextUtil;
import com.troy.trade.futures.api.model.dto.out.account.FuturesBalanceResDto;
import com.troy.trade.ws.api.model.dto.out.BalanceResDto;
import com.troy.trade.ws.model.dto.in.BalancePushReqDto;
import com.troy.trade.ws.model.dto.in.PrivateParamDto;
import com.troy.trade.ws.server.NotificationService;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class BalancePushUtil {

    /**
     * 合约余额变动推送
     * @param balancePushReqDto
     * @return
     */
    public static BalanceResDto sendFuturesBalance(BalancePushReqDto<FuturesBalanceResDto> balancePushReqDto) {
        if(null == balancePushReqDto){
            log.error("用户余额信息委托推送失败，必填参数为空");
            return new BalanceResDto(false);
        }

        Long accountId = balancePushReqDto.getAccountId();
        ExchangeCode exchCode = balancePushReqDto.getExchCode();
        if (accountId == null
                || null == exchCode) {
            log.error("用户余额信息委托推送失败，必填参数为空，accountId={},exchCode={}", accountId, exchCode);
            return new BalanceResDto(false);
        }

        FuturesBalanceResDto futuresBalanceResDto = balancePushReqDto.getBalance();
        PrivateParamDto<FuturesBalanceResDto> privateParamDto = new PrivateParamDto();
        privateParamDto.setAccountId(balancePushReqDto.getAccountId());
        privateParamDto.setExchCode(exchCode.code());
        privateParamDto.setExchangeCode(exchCode);
        privateParamDto.setParams(futuresBalanceResDto);

        /**
         * 初始化币对类型字段
         */
        privateParamDto.setSymbol(balancePushReqDto.getSymbol());
        privateParamDto.setAlias(balancePushReqDto.getAlias());

        /**
         * 2、做数据推送
         */
        NotificationService notificationService = ApplicationContextUtil.getBean(NotificationService.class);
        notificationService.broadcastFuturesBalance(privateParamDto);
        return new BalanceResDto(true);
    }

}

package com.troy.trade.ws.service;

import com.troy.trade.ws.api.model.dto.in.BalanceChangeReqDto;
import com.troy.trade.ws.api.model.dto.in.FuturesAccountInfoReqDto;
import com.troy.trade.ws.api.model.dto.out.BalanceResDto;
import com.troy.trade.ws.model.dto.in.BalanceSubscribe;

public interface IBalanceService {

    /**
     * 余额变动推送
     * @param futuresAccountInfoReqDto
     * @return
     */
    BalanceResDto sendSingleFuturesBalance(FuturesAccountInfoReqDto futuresAccountInfoReqDto);

    /**
     * 合约账户余额变化触发--消息
     * @return
     */
    BalanceResDto sendAllFuturesBalance();

    /**
     * 币币账户余额变化触发--消息
     * @param balanceChangeReqDto
     * @return
     */
    BalanceResDto sendSpotBalance(BalanceChangeReqDto balanceChangeReqDto);

    /**
     * 余额消息订阅
     * @param balanceSubscribe
     *        key
     *        destination
     */
    void subscribe(BalanceSubscribe balanceSubscribe,Long userId, String key, String destination);
}

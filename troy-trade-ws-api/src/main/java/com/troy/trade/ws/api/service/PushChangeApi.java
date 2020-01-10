package com.troy.trade.ws.api.service;

import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResFactory;
import com.troy.trade.ws.api.model.dto.in.*;
import com.troy.trade.ws.api.model.dto.out.BalanceResDto;
import com.troy.trade.ws.api.model.dto.out.OpenOrderResDto;
import org.springframework.web.bind.annotation.PostMapping;

public interface PushChangeApi {

    /**
     * 绑定详情
     *
     * @param req
     * @return
     */
    @PostMapping(value = "/private/send/spotOpenOrder")
    Res<OpenOrderResDto> sendSpotOpenOrderChange(Req<OpenOrderReqDto<SpotOpenOrderReqDto>> req);

    /**
     * 绑定详情
     *
     * @param req
     * @return
     */
    @PostMapping(value = "/private/send/futuresOpenOrder")
    Res<OpenOrderResDto> sendFuturesOpenOrderChange(Req<OpenOrderReqDto<FuturesOpenOrderReqDto>> req);

    /**
     * 余额变更推送
     *
     * @param req
     * @return
     */
    @PostMapping(value = "/private/send/spotBalance")
    Res<BalanceResDto> sendSpotBalanceChange(Req<BalanceChangeReqDto> req);

    /**
     * 余额变更推送
     *
     * @return
     */
    @PostMapping(value = "/private/send/allFuturesBalance")
    Res<BalanceResDto> sendAllFuturesBalanceChange(Req req);

    /**
     * 合约账户余额变动推送
     * @param futuresAccountInfoReqDtoReq
     * @return
     */
    @PostMapping(value = "/private/send/singleFuturesBalance")
    Res<BalanceResDto> sendSingleFuturesBalanceChange(Req<FuturesAccountInfoReqDto> futuresAccountInfoReqDtoReq);
}

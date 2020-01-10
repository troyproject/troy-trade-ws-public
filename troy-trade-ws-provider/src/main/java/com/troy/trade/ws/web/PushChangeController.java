package com.troy.trade.ws.web;

import com.troy.commons.BaseController;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResFactory;
import com.troy.trade.ws.api.model.dto.in.*;
import com.troy.trade.ws.api.model.dto.out.BalanceResDto;
import com.troy.trade.ws.api.model.dto.out.OpenOrderResDto;
import com.troy.trade.ws.api.service.PushChangeApi;
import com.troy.trade.ws.service.IBalanceService;
import com.troy.trade.ws.service.IOpenOrderService;
import com.troy.trade.ws.service.impl.OpenOrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 当前挂单服务
 *
 * @author dp
 */
@RestController
public class PushChangeController extends BaseController implements PushChangeApi {

    @Autowired
    IOpenOrderService openOrderService;

    @Autowired
    IBalanceService balanceService;

    @Override
    public Res<OpenOrderResDto> sendSpotOpenOrderChange(@RequestBody Req<OpenOrderReqDto<SpotOpenOrderReqDto>> req) {
        return ResFactory.getInstance().success(openOrderService.sendSpotOpenOrders(req.getData()));
    }

    @Override
    public Res<OpenOrderResDto> sendFuturesOpenOrderChange(@RequestBody Req<OpenOrderReqDto<FuturesOpenOrderReqDto>> req) {
        return ResFactory.getInstance().success(openOrderService.sendFuturesOpenOrders(req.getData()));
    }

    @Override
    public Res<BalanceResDto> sendSpotBalanceChange(@RequestBody Req<BalanceChangeReqDto> req) {
        return ResFactory.getInstance().success(balanceService.sendSpotBalance(req.getData()));
    }

    @Override
    public Res<BalanceResDto> sendAllFuturesBalanceChange(@RequestBody Req req) {
        return ResFactory.getInstance().success(balanceService.sendAllFuturesBalance());
    }

    @Override
    public Res<BalanceResDto> sendSingleFuturesBalanceChange(@RequestBody Req<FuturesAccountInfoReqDto> req) {
        return ResFactory.getInstance().success(balanceService.sendSingleFuturesBalance(req.getData()));
    }


}

package com.troy.trade.ws.service.streaming;

import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.trade.ws.model.dto.in.*;

public interface IStreamingExchangeService {

    ExchangeCode getExchCode();

    Boolean validate(ValidateDto validateDto);

    Boolean connect(ConnectDto connectDto);

    Boolean disconnect(DisconnectDto disconnectDto);

    void depthSubscribe(RequestDto<DepthSubscribe> depthSubscribeRequestBody);

    void tradeSubscribe(RequestDto<TradeSubscribe> tradeSubscribeRequestBody);

}

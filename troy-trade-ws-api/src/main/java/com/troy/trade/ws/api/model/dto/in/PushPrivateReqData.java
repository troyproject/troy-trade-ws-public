package com.troy.trade.ws.api.model.dto.in;

import com.troy.commons.dto.in.ReqData;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 私有推送请求数据
 *
 * @author dp
 */
@Setter
@Getter
public abstract class PushPrivateReqData extends ReqData {

    /**
     * 用户ID
     */
    private Long accountId;

    /**
     * 交易所CODE
     */
    private ExchangeCode exchCode;

}

package com.troy.trade.ws.model.dto.in;

import com.troy.commons.exchange.model.enums.AliasEnum;
import com.troy.trade.ws.api.model.dto.in.PushPrivateReqData;
import lombok.Getter;
import lombok.Setter;

/**
 * 当前账户余额DTO
 * @author dp
 */
@Setter
@Getter
public class BalancePushReqDto<T> extends PushPrivateReqData {

    private T balance;

    /**
     * 交易对名称,如 btc/usdt
     */
    private String symbol;

    /**
     * 交易对类型
     * 本周 this_week
     * 次周 next_week
     * 季度 quarter
     * 交割合约 必传
     */
    private AliasEnum alias;

}

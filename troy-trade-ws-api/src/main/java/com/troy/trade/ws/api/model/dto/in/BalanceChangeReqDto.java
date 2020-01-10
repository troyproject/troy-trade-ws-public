package com.troy.trade.ws.api.model.dto.in;

import com.troy.commons.exchange.model.enums.AliasEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 余额变动推送
 */
@Setter
@Getter
public class BalanceChangeReqDto extends PushPrivateReqData {

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

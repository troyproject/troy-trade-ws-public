package com.troy.trade.ws.api.model.dto.in;

import com.troy.commons.dto.in.ReqData;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.enums.AliasEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 合约账户余额查询结果实体
 * @author yanping
 */
@Getter
@Setter
public class FuturesAccountInfoReqDto extends ReqData {

    /**
     * 交易所code
     */
    private ExchangeCode exchCode;

    /**
     * 账户
     */
    private Long accountId;

    /**
     * 余额的币对名称
     */
    private String symbol;

    /**
     * 币对类型
     */
    private AliasEnum alias;

    /**
     * 账户余额信息
     */
    private FuturesBalanceInfoReqDto futuresBalanceInfoReqDto;

    public FuturesAccountInfoReqDto() {
        super();
    }

    public FuturesAccountInfoReqDto(ExchangeCode exchCode, Long accountId,
                                    String symbol, AliasEnum alias,
                                    FuturesBalanceInfoReqDto futuresBalanceInfoReqDto) {
        this.exchCode = exchCode;
        this.accountId = accountId;
        this.symbol = symbol;
        this.alias = alias;
        this.futuresBalanceInfoReqDto = futuresBalanceInfoReqDto;
    }

    public static FuturesAccountInfoReqDto getInstance(ExchangeCode exchCode, Long accountId,
                                                String symbol, AliasEnum alias,
                                                FuturesBalanceInfoReqDto futuresBalanceInfoReqDto){
        return new FuturesAccountInfoReqDto(exchCode, accountId,
                symbol, alias, futuresBalanceInfoReqDto);
    }
}

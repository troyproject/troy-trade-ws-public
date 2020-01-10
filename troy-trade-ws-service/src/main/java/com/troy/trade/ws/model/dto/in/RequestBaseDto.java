package com.troy.trade.ws.model.dto.in;

import com.troy.commons.dto.in.ReqData;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.enums.AliasEnum;

/**
 * base DTO
 */
public class RequestBaseDto extends ReqData {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7490521744859208716L;

	/**
     * 交易对名称,如 btc/usdt
     */
    private String symbol;

    /**
     * 交易所code
     */
    private String exchCode;

    /**
     * 交易所code
     */
    private ExchangeCode exchangeCode;

    /**
     * 交易对类型
     * 本周 this_week
     * 次周 next_week
     * 季度 quarter
     * 交割合约 必传
     */
    private AliasEnum alias;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExchCode() {
        return exchCode;
    }

    public void setExchCode(String exchCode) {
        this.exchCode = exchCode;
    }

    public ExchangeCode getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(ExchangeCode exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public AliasEnum getAlias() {
        return alias;
    }

    public void setAlias(AliasEnum alias) {
        this.alias = alias;
    }
}

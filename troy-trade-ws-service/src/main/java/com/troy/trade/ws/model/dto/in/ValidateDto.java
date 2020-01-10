package com.troy.trade.ws.model.dto.in;

import com.troy.commons.dto.in.ReqData;

public class ValidateDto extends ReqData {

    /**
     * 交易对名称
     */
    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}

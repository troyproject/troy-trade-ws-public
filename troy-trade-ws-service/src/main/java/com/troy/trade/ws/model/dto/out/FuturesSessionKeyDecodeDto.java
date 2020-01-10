package com.troy.trade.ws.model.dto.out;

import com.troy.commons.exchange.model.enums.AliasEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FuturesSessionKeyDecodeDto {

    private String symbol;

    private AliasEnum alias;

    public FuturesSessionKeyDecodeDto() {
        super();
    }

    public FuturesSessionKeyDecodeDto(String symbol, AliasEnum alias) {
        this.symbol = symbol;
        this.alias = alias;
    }

    public static FuturesSessionKeyDecodeDto getInstance(String symbol, AliasEnum alias){
        return new FuturesSessionKeyDecodeDto(symbol, alias);
    }

}

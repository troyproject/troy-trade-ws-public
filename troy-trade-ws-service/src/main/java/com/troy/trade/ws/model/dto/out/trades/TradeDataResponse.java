package com.troy.trade.ws.model.dto.out.trades;

import com.troy.commons.dto.out.ResData;
import com.troy.trade.ws.model.dto.out.TradeResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * TradeDataResponse
 *
 * @au yanping
 * @date 2019/8/05
 */
@Setter
@Getter
public class TradeDataResponse extends ResData {
    private List<TradeResponse> tradeResponseList;

    public TradeDataResponse(List<TradeResponse> tradeResponseList) {
        super();
        if(null == tradeResponseList){
            tradeResponseList = new ArrayList<>();
        }
        this.tradeResponseList = tradeResponseList;
    }
}

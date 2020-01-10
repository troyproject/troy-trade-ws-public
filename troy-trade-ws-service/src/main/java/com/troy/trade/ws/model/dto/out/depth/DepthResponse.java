package com.troy.trade.ws.model.dto.out.depth;

import com.troy.commons.dto.out.ResData;

import java.util.ArrayList;
import java.util.List;

/**
 * DepthResponse
 *
 * @author yanping
 * @date 2019/8/05
 */
public class DepthResponse extends ResData {
    private String symbol;
    private boolean fullData;
    private List<List<String>> asks;//卖单,0-价格，1-数量
    private List<List<String>> bids;//买单,0-价格，1-数量

    public DepthResponse(String symbol, boolean fullData, List<List<String>> asks,List<List<String>> bids) {
        super();
        if(null == symbol){
            symbol = "";
        }
        if(null == asks){
            asks = new ArrayList<>();
        }
        if(null == bids){
            bids = new ArrayList<>();
        }
        this.symbol = symbol;
        this.asks = asks;
        this.bids = bids;
        this.fullData = fullData;
    }

    public String getSymbol() {
        return symbol;
    }

    public List<List<String>> getAsks() {
        return asks;
    }

    public List<List<String>> getBids() {
        return bids;
    }

    public boolean isFullData() {
        return fullData;
    }

    public void setAsks(List<List<String>> asks) {
        this.asks = asks;
    }

    public void setBids(List<List<String>> bids) {
        this.bids = bids;
    }
}

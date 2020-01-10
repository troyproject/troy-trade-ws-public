package com.troy.trade.ws.model.dto.out.depth;

import com.troy.commons.dto.out.ResData;

import java.util.ArrayList;
import java.util.List;

/**
 * OrderBook
 * @author yanping
 * @date 2019/8/05
 */
public class OrderBookResponse extends ResData {

    private List<List<String>> asks;//卖单,0-价格，1-数量
    private List<List<String>> bids;//买单,0-价格，1-数量

    public OrderBookResponse(List<List<String>> asks, List<List<String>> bids) {
        super();
        if(null == asks){
            asks = new ArrayList<>();
        }
        if(null == bids){
            bids = new ArrayList<>();
        }
        this.asks = asks;
        this.bids = bids;
    }

    public List<List<String>> getAsks() {
        return asks;
    }

    public void setAsks(List<List<String>> asks) {
        this.asks = asks;
    }

    public List<List<String>> getBids() {
        return bids;
    }

    public void setBids(List<List<String>> bids) {
        this.bids = bids;
    }
}

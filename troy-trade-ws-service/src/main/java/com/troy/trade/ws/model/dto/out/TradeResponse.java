package com.troy.trade.ws.model.dto.out;

import com.troy.commons.dto.out.ResData;

/**
 * 市场成交记录
 */
public class TradeResponse extends ResData {
    /**
     * 订单类型，1-买、2-卖
     */
    private Integer type;

    /** 下单个数 */
    private String amount;

    /** 交易对名称 */
    private String symbol;

    /** 下单价格 */
    private String price;

    /** 成交时间戳，可能为空 */
    private Long timestamp;

    /** 交易所的 tradeId */
    private String id;

    public TradeResponse() {
        super();
    }

    public TradeResponse(Integer type, String amount, String symbol, String price, Long timestamp, String id) {
        if(null == type){
            type = -1;
        }

        if(null == amount){
            amount = "0";
        }

        if(null == symbol){
            symbol = "";
        }

        if(null == price){
            price = "0";
        }

        if(null == timestamp){
            timestamp = 0L;
        }

        if(null == id){
            id = "";
        }

        this.type = type;
        this.amount = amount;
        this.symbol = symbol;
        this.price = price;
        this.timestamp = timestamp;
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

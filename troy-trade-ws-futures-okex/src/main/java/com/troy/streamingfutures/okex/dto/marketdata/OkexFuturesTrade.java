package com.troy.streamingfutures.okex.dto.marketdata;

public class OkexFuturesTrade {
    private String timestamp;
    private String price;
    private String qty;
    private String tradeId;
    private String side;
    private String instrumentId;

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPrice() {
        return price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getTradeId() {
        return tradeId;
    }

    public String getSide() {
        return side;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

}

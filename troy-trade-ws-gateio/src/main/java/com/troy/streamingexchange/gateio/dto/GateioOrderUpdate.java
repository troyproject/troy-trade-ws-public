package com.troy.streamingexchange.gateio.dto;

import com.troy.trade.ws.dto.currency.CurrencyPair;

import java.math.BigDecimal;

/**
 * GateioOrderUpdate
 *
 * @author liuxiaocheng
 * @date 2018/7/2
 */
public class GateioOrderUpdate {
    private CurrencyPair currencyPair;
    private Integer eventType;//1 create 2 update 3 finish
    private String orderId;
    private String orderType;//order type, 1: limit, 2: market
    private String type;//type, 1: sell, 2: buy
    private long ctime;
    private BigDecimal price;
    private BigDecimal amount;
    private BigDecimal left;
    private BigDecimal filledAmount;
    private BigDecimal filledTotal;
    private BigDecimal dealFee;

    public GateioOrderUpdate() {
    }

    public GateioOrderUpdate(CurrencyPair currencyPair, Integer eventType, String orderId, String orderType, String type, long ctime,
                             BigDecimal price, BigDecimal amount, BigDecimal left, BigDecimal filledAmount,
                             BigDecimal filledTotal, BigDecimal dealFee) {
        this.currencyPair = currencyPair;
        this.eventType = eventType;
        this.orderId = orderId;
        this.orderType = orderType;
        this.type = type;
        this.ctime = ctime;
        this.price = price;
        this.amount = amount;
        this.left = left;
        this.filledAmount = filledAmount;
        this.filledTotal = filledTotal;
        this.dealFee = dealFee;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getLeft() {
        return left;
    }

    public void setLeft(BigDecimal left) {
        this.left = left;
    }

    public BigDecimal getFilledAmount() {
        return filledAmount;
    }

    public void setFilledAmount(BigDecimal filledAmount) {
        this.filledAmount = filledAmount;
    }

    public BigDecimal getFilledTotal() {
        return filledTotal;
    }

    public void setFilledTotal(BigDecimal filledTotal) {
        this.filledTotal = filledTotal;
    }

    public BigDecimal getDealFee() {
        return dealFee;
    }

    public void setDealFee(BigDecimal dealFee) {
        this.dealFee = dealFee;
    }

    @Override
    public String toString() {
        return "GateioOrderUpdate{" +
                "currencyPair=" + currencyPair +
                ", eventType=" + eventType +
                ", orderId='" + orderId + '\'' +
                ", orderType='" + orderType + '\'' +
                ", type='" + type + '\'' +
                ", ctime=" + ctime +
                ", price=" + price +
                ", amount=" + amount +
                ", left=" + left +
                ", filledAmount=" + filledAmount +
                ", filledTotal=" + filledTotal +
                ", dealFee=" + dealFee +
                '}';
    }
}

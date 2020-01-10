package com.troy.trade.ws.model.domain;

public class StreamingExchangeDto {
    private String symbol;
    private boolean isRobot;

    public StreamingExchangeDto() {
        super();
    }

    public StreamingExchangeDto(String symbol, boolean isRobot) {
        this.symbol = symbol;
        this.isRobot = isRobot;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean isRobot() {
        return isRobot;
    }

    public void setRobot(boolean robot) {
        isRobot = robot;
    }
}

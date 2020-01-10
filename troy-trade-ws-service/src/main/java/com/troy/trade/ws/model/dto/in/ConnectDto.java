package com.troy.trade.ws.model.dto.in;

import com.troy.commons.dto.in.ReqData;

public class ConnectDto extends ReqData {

    /**
     * 交易对名称
     */
    private String symbol;

    /**
     * sessionId
     */
    private String sessionId;

    /**
     * 是否机器人
     */
    private Boolean robot;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getRobot() {
        return robot;
    }

    public void setRobot(Boolean robot) {
        this.robot = robot;
    }
}

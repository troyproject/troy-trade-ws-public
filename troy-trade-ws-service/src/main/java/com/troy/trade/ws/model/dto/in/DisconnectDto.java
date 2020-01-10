package com.troy.trade.ws.model.dto.in;

import com.troy.commons.dto.in.ReqData;

public class DisconnectDto extends ReqData {

    private String exchCode;
    private String clientId;
    private String sessionId;

    public String getExchCode() {
        return exchCode;
    }

    public void setExchCode(String exchCode) {
        this.exchCode = exchCode;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

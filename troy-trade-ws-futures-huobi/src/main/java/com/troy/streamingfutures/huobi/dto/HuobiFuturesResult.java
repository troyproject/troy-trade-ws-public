package com.troy.streamingfutures.huobi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * yanping
 * @param <V>
 */
public class HuobiFuturesResult<V> {

    private final String status;
    private final String errCode;
    private final String errMsg;
    private final V result;

    @JsonCreator
    public HuobiFuturesResult(String status,String errCode,String errMsg,
            V result) {
        this.status = status;
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.result = result;
    }

//    public boolean isSuccess() {
//        return getStatus().equals("ok");
//    }

    public String getStatus() {
        return status;
    }

//    public String getError() {
//        return (errMsg.length() == 0) ? errCode : errMsg;
//    }

    public V getResult() {
        return result;
    }

    @Override
    public String toString() {
        return String.format(
                "HuobiResult [%s, %s]", status, errMsg);
    }
}
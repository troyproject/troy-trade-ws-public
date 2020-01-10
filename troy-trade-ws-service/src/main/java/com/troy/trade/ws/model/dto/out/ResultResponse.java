package com.troy.trade.ws.model.dto.out;

import java.io.Serializable;

/**
 * Result
 *
 * @author yanping
 * @date 2019/8/05
 */
public class ResultResponse implements Serializable {
    private String status="success";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

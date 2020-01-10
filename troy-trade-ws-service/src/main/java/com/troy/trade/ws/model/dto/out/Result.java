package com.troy.trade.ws.model.dto.out;

import java.io.Serializable;

/**
 * Result
 *
 * @author liuxiaocheng
 * @date 2018/6/29
 */
public class Result implements Serializable {
    private String status="success";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

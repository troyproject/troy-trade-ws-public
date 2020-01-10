package com.troy.streamingexchange.gateio.service.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GateioError
 *
 * @author liuxiaocheng
 * @date 2018/6/28
 */
public class GateioError {
    private final String code;
    private final String message;

    public GateioError(@JsonProperty("code") String code, @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return "GateioError{code='" + this.code + '\'' + ", message='" + this.message + '\'' + '}';
    }
}

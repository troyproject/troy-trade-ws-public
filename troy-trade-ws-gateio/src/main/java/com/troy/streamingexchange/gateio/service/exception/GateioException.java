package com.troy.streamingexchange.gateio.service.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GateioException
 *
 * @author liuxiaocheng
 * @date 2018/6/28
 */
public class GateioException extends RuntimeException {
    GateioError gateioError;

    public GateioException(@JsonProperty("error") GateioError gateioError) {
        super(gateioError.getMessage());
    }

}

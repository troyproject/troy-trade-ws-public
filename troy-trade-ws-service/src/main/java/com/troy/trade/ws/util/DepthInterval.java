package com.troy.trade.ws.util;

import org.apache.commons.lang3.StringUtils;

/**
 * DepthInterval
 */
public enum DepthInterval {
    D0("step0", "0"),
    D1("step1", "0.1"),
    D2("step2", "0.01"),
    D3("step3", "0.001"),
    D4("step4", "0.0001"),
    D5("step5", "0.00001"),
    ;
    private final String code;
    private final String depth;

    DepthInterval(String code, String depth) {
        this.code = code;
        this.depth = depth;
    }

    public static DepthInterval fromDepthIntervalCode(String code) {
        for (DepthInterval depthInterval : DepthInterval.values()) {
            if (StringUtils.equals(code, depthInterval.getCode())) {
                return depthInterval;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDepth() {
        return depth;
    }
}

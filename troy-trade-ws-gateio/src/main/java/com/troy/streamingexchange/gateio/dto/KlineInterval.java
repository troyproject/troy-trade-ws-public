package com.troy.streamingexchange.gateio.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * KlineInterval
 *
 */
public enum KlineInterval {
    m1("1m", TimeUnit.MINUTES.toMillis(1L), 1),
    m3("3m", TimeUnit.MINUTES.toMillis(3L), 1),
    m5("5m", TimeUnit.MINUTES.toMillis(5L), 12),
    m15("15m", TimeUnit.MINUTES.toMillis(15L), 24),
    m30("30m", TimeUnit.MINUTES.toMillis(30L), 48),
    h1("1h", TimeUnit.HOURS.toMillis(1L), 96),
    h2("2h", TimeUnit.HOURS.toMillis(2L), 192),
    h4("4h", TimeUnit.HOURS.toMillis(4L), 384),
    h6("6h", TimeUnit.HOURS.toMillis(6L), 576),
    h8("8h", TimeUnit.HOURS.toMillis(8L), 768),
    h12("12h", TimeUnit.HOURS.toMillis(12L), 1152),
    d1("1d", TimeUnit.DAYS.toMillis(1L), 2304),
    d3("3d", TimeUnit.DAYS.toMillis(3L), 6912),
    w1("1w", TimeUnit.DAYS.toMillis(7L), 16128),
    M1("1M", TimeUnit.DAYS.toMillis(30L), 64512);

    private final String code;
    private final Long millis;
    private final Integer hourRange;

    KlineInterval(String code, Long millis, Integer hourRange) {
        this.code = code;
        this.millis = millis;
        this.hourRange = hourRange;
    }

    public static KlineInterval fromKlineIntervalCode(String code) {
        for (KlineInterval klineInterval : KlineInterval.values()) {
            if (StringUtils.equals(code, klineInterval.code())) {
                return klineInterval;
            }
        }
        return null;
    }


    public Long getMillis() {
        return this.millis;
    }

    public String code() {
        return this.code;
    }

    public Integer getHourRange() {
        return hourRange;
    }
}

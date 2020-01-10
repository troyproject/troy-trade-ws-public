package com.troy.streamingexchange.gateio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * GateioNofityTicker
 *
 * @author liuxiaocheng
 * @date 2018/6/28
 */
public class GateioNofityTicker {

    private final Integer period;
    private final BigDecimal open;
    private final BigDecimal close;
    private final BigDecimal high;
    private final BigDecimal low;
    private final BigDecimal last;
    private final String change;
    private final BigDecimal quoteVolume;
    private final BigDecimal baseVolume;

    public GateioNofityTicker(@JsonProperty Integer period, @JsonProperty BigDecimal open, @JsonProperty BigDecimal close,
                              @JsonProperty BigDecimal high, @JsonProperty BigDecimal low, @JsonProperty BigDecimal last,
                              @JsonProperty String change, @JsonProperty BigDecimal quoteVolume, @JsonProperty BigDecimal baseVolume) {
        this.period = period;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.last = last;
        this.change = change;
        this.quoteVolume = quoteVolume;
        this.baseVolume = baseVolume;
    }

    public Integer getPeriod() {
        return period;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getLast() {
        return last;
    }

    public String getChange() {
        return change;
    }

    public BigDecimal getQuoteVolume() {
        return quoteVolume;
    }

    public BigDecimal getBaseVolume() {
        return baseVolume;
    }

    @Override
    public String toString() {
        return "GateioNofityTicker{" +
                "period=" + period +
                ", open=" + open +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", last=" + last +
                ", change='" + change + '\'' +
                ", quoteVolume=" + quoteVolume +
                ", baseVolume=" + baseVolume +
                '}';
    }
}

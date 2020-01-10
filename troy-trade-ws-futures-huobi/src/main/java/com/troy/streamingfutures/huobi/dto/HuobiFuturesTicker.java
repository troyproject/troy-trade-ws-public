package com.troy.streamingfutures.huobi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * HuobiFuturesTicker
 */
public final class HuobiFuturesTicker {
    private final long id;
    private final BigDecimal amount;
    private final long count;
    private final BigDecimal open;
    private final BigDecimal close;
    private final BigDecimal low;
    private final BigDecimal high;
    private final long version;
    private final BigDecimal vol;

    public HuobiFuturesTicker(
            @JsonProperty("id") long id,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("count") long count,
            @JsonProperty("open") BigDecimal open,
            @JsonProperty("close") BigDecimal close,
            @JsonProperty("low") BigDecimal low,
            @JsonProperty("high") BigDecimal high,
            @JsonProperty("version") long version,
            @JsonProperty("vol") BigDecimal vol) {
        this.id = id;
        this.amount = amount;
        this.count = count;
        this.open = open;
        this.close = close;
        this.low = low;
        this.high = high;
        this.version = version;
        this.vol = vol;

    }

    public long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public long getCount() {
        return count;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public long getVersion() {
        return version;
    }

    public BigDecimal getVol() {
        return vol;
    }

    @Override
    public String toString() {
        return "HuobiFuturesTicker{" +
                "id=" + id +
                ", amount=" + amount +
                ", count=" + count +
                ", open=" + open +
                ", close=" + close +
                ", low=" + low +
                ", high=" + high +
                ", version=" + version +
                ", vol=" + vol +
                '}';
    }
}

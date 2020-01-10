package com.troy.streamingfutures.okex.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

public class OkexFuturesDepth {
    private final BigDecimal[][] asks;
    private final BigDecimal[][] bids;
    private final Date timestamp;
    private final String instrument_id;
    private final String checksum;

    public OkexFuturesDepth(@JsonProperty("asks") BigDecimal[][] asks, @JsonProperty("bids") BigDecimal[][] bids, @JsonProperty(required = false,value = "timestamp") Date timestamp,
                       @JsonProperty("instrument_id") String instrument_id,@JsonProperty("checksum") String checksum) {
        this.asks = asks;
        this.bids = bids;
        this.timestamp = timestamp;
        this.instrument_id = instrument_id;
        this.checksum = checksum;
    }

    public BigDecimal[][] getAsks() {
        return this.asks;
    }

    public BigDecimal[][] getBids() {
        return this.bids;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getInstrument_id() {
        return instrument_id;
    }

    public String getChecksum() {
        return checksum;
    }

    public String toString() {
        return "OkCoinDepth [asks=" + Arrays.toString(this.asks) + ", bids=" + Arrays.toString(this.bids) + "]";
    }
}

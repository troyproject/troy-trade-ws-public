package com.troy.streamingfutures.okex.dto;

import java.math.BigDecimal;

public class OkexFuturesTicker {

    private String instrument_id;
    private BigDecimal last;
    private BigDecimal open_24h;
    private BigDecimal high_24h;
    private BigDecimal low_24h;
    private BigDecimal base_volume_24h;
    private BigDecimal quote_volume_24h;

    public String getInstrument_id() {
        return instrument_id;
    }

    public void setInstrument_id(String instrument_id) {
        this.instrument_id = instrument_id;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public BigDecimal getOpen_24h() {
        return open_24h;
    }

    public void setOpen_24h(BigDecimal open_24h) {
        this.open_24h = open_24h;
    }

    public BigDecimal getHigh_24h() {
        return high_24h;
    }

    public void setHigh_24h(BigDecimal high_24h) {
        this.high_24h = high_24h;
    }

    public BigDecimal getLow_24h() {
        return low_24h;
    }

    public void setLow_24h(BigDecimal low_24h) {
        this.low_24h = low_24h;
    }

    public BigDecimal getBase_volume_24h() {
        return base_volume_24h;
    }

    public void setBase_volume_24h(BigDecimal base_volume_24h) {
        this.base_volume_24h = base_volume_24h;
    }

    public BigDecimal getQuote_volume_24h() {
        return quote_volume_24h;
    }

    public void setQuote_volume_24h(BigDecimal quote_volume_24h) {
        this.quote_volume_24h = quote_volume_24h;
    }
}

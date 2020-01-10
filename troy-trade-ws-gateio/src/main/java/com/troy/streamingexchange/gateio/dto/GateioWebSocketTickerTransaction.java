package com.troy.streamingexchange.gateio.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.troy.streamingexchange.gateio.dto.marketdata.GateioTicker;
import com.troy.trade.ws.dto.Ticker;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Pavel Chertalev on 15.03.2018.
 */
public class GateioWebSocketTickerTransaction extends GateioWebSocketBaseTransaction {

    private final Integer id;
    private final List<Object> params;

    public GateioWebSocketTickerTransaction(@JsonProperty("method") String method, @JsonProperty("id") Integer id,
                                            @JsonProperty("params") List<Object> params) {
        super(method);
        this.id = id;
        this.params = params;
    }

    public Integer getId() {
        return id;
    }


    public List<Object> getParams() {
        return params;
    }

    public GateioTicker toGateioTicker() {
        String json = params.get(1).toString();
        GateioNofityTicker nofityTicker = JSONObject.parseObject(json, GateioNofityTicker.class);
        String changeStr = nofityTicker.getChange();
        BigDecimal percentChange;
        if (changeStr.contains("-")) {
            changeStr = changeStr.replace("-", "");
            percentChange = new BigDecimal(changeStr).negate();
        } else {
            percentChange = new BigDecimal(changeStr);
        }
        GateioTicker gateioTicker = new GateioTicker(true, null,
                null, nofityTicker.getLow(), nofityTicker.getClose(), nofityTicker.getHigh(),
                percentChange, null, nofityTicker.getQuoteVolume(), nofityTicker.getBaseVolume());
        return gateioTicker;
    }

    /**
     * 1492358400, time
     * "7000.00",  open
     * "8000.0",   close
     * "8100.00",  highest
     * "6800.00",  lowest
     * "1000.00"   volume
     * "123456.00" amount
     * "BTC_USDT"  market name
     *
     * @return
     */
    public List<Ticker> toTickers() {
        List<Ticker> tickers = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(params)) {
            for (Object item : params) {
                JSONArray array = (JSONArray) item;
                Ticker ticker = new Ticker.Builder().open(array.getBigDecimal(1))
                        .last(array.getBigDecimal(2))
                        .high(array.getBigDecimal(3))
                        .low(array.getBigDecimal(4))
                        .volume(array.getBigDecimal(5))
                        .vwap(array.getBigDecimal(6))
                        .timestamp(array.getTimestamp(0))
                        .currencyPair(new CurrencyPair(array.getString(7).replace("_", "/")))
                        .build();
                tickers.add(ticker);
            }
        }
        return tickers;
    }
}

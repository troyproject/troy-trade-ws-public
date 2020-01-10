package com.troy.streamingexchange.okex.dto;

import com.troy.streamingexchange.okex.dto.marketdata.OkexTrade;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class OkexWebSocketTrade extends OkexTrade {
    public OkexWebSocketTrade(String date, String price, String size, String tradeId, String side, String instrumentId) {
        super(date, price, size, tradeId, side, instrumentId);
    }
//    public OkexWebSocketTrade(String[] items) throws ParseException {
////        @JsonProperty("timestamp") String date, @JsonProperty("price") String price, @JsonProperty("amount") String size,
////        @JsonProperty("trade_id") String tradeId, @JsonProperty("side") String side, @JsonProperty("instrument_id") String instrumentId)
//
////        "instrument_id":"ETH-USDT",
////        "price":"162.12",
////        "side":"buy",
////        "size":"11.085",
////        "timestamp":"2019-04-16T10:58:17.122Z",
////        "trade_id":"1210447366"
//
//        super(getDate(items[3]).getTime() / 1000, new BigDecimal(items[1]), new BigDecimal(items[2]), Long.valueOf(items[0]), items[4]);
//    }

    private static Date getDate(String exchangeTime) throws ParseException {
        DateFormat tdf = new SimpleDateFormat("yyyy-MM-dd");
        tdf.setTimeZone(TimeZone.getTimeZone("Hongkong"));
        Date today = Calendar.getInstance(TimeZone.getDefault()).getTime();
        String exchangeToday = tdf.format(today);

        SimpleDateFormat fdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X");
        fdf.setTimeZone(TimeZone.getDefault());
        return fdf.parse(exchangeToday + " " + exchangeTime + " +0800");
    }
}

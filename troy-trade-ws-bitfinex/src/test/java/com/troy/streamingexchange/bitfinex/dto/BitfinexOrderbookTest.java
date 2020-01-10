package com.troy.streamingexchange.bitfinex.dto;

import com.troy.streamingexchange.bitfinex.BitfinexAdapters;
import com.troy.streamingexchange.bitfinex.dto.marketdata.BitfinexDepth;
import com.troy.trade.ws.dto.OrderBook;
import com.troy.trade.ws.dto.currency.CurrencyPair;
import org.junit.Test;

import java.util.Date;

import static java.math.BigDecimal.ONE;
import static org.hamcrest.MatcherAssert.assertThat;

public class BitfinexOrderbookTest {

    @Test
    public void timestampShouldBeInSeconds() {
        BitfinexDepth depth = new BitfinexOrderbook(new BitfinexOrderbookLevel[]{
                new BitfinexOrderbookLevel(ONE, ONE, ONE),
                new BitfinexOrderbookLevel(ONE, ONE, ONE)
        }).toBitfinexDepth();

        CurrencyPair currencyPair = new CurrencyPair("BTC","USD");
        OrderBook orderBook = BitfinexAdapters.adaptOrderBook(depth, currencyPair);

        // What is the time now... after order books created?
        assertThat("The timestamp should be a value less than now, but was: " + orderBook.getTimeStamp(),
                !orderBook.getTimeStamp().after(new Date()));
    }
}
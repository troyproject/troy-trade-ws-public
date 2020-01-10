package com.troy.streamingfutures.huobi.dto;

/**
 * HuobiPongMessage
 *
 * @author liuxiaocheng
 * @date 2018/7/12
 */
public class HuobiFuturesPongMessage {
    private Long pong;

    public HuobiFuturesPongMessage(Long pong) {
        this.pong = pong;
    }

    public Long getPong() {
        return pong;
    }

    public void setPong(Long pong) {
        this.pong = pong;
    }
}

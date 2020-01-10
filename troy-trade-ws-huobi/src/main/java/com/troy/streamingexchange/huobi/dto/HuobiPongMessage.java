package com.troy.streamingexchange.huobi.dto;

/**
 * HuobiPongMessage
 *
 * @author liuxiaocheng
 * @date 2018/7/12
 */
public class HuobiPongMessage {
    private Long pong;

    public HuobiPongMessage(Long pong) {
        this.pong = pong;
    }

    public Long getPong() {
        return pong;
    }

    public void setPong(Long pong) {
        this.pong = pong;
    }
}

package com.troy.streamingexchange.gateio.dto;

/**
 * EventType
 *
 * @author liuxiaocheng
 * @date 2018/7/2
 */
public enum EventType {
    CREATED(1),
    UPDATE(2),
    FINISH(3);

    private final Integer type;

    EventType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}

package com.troy.trade.ws.model.dto.in;

import lombok.Getter;
import lombok.Setter;

/**
 * 发送给前台的私有参数
 * @param <T>
 * @author ydp
 */
@Setter
@Getter
public class PrivateParamDto<T> extends RequestBaseDto {

    private Long accountId;

    /**
     * 入参
     */
    private T params;

}

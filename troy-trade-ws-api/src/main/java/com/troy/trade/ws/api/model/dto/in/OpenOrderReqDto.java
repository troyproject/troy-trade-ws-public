package com.troy.trade.ws.api.model.dto.in;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 当前挂单DTO
 * @author dp
 */
@Setter
@Getter
public class OpenOrderReqDto<T> extends PushPrivateReqData {

    private List<T> openOrders;
}

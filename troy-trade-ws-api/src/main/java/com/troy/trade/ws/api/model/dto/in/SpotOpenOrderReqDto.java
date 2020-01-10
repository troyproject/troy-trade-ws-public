package com.troy.trade.ws.api.model.dto.in;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 当前挂单
 * @author dp
 */
@Setter
@Getter
@ToString
public class SpotOpenOrderReqDto implements Serializable {

    /**
     * 交易流水号
     */
    private Long spotTransId;

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 挂单时间（到s的时间戳）
     */
    private Long thirdCreateTime;

    /**
     * 挂单方向（1-买 2-卖）
     */
    private Integer side;

    /**
     * 交易对名称
     */
    private String symbol;

    /**
     * 交易所交易对名称
     */
    private String tradeSymbol;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private BigDecimal amount;

    /**
     * 交易额
     */
    private BigDecimal totalCashAmount;

    /**
     * 成交数量
     */
    private BigDecimal filledAmount;

    /**
     * 未成交数量
     */
    private BigDecimal leftAmount;

    /**
     * 状态
     */
    private Integer status;
}

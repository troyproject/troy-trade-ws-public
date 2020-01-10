package com.troy.trade.ws.api.model.dto.in;

import com.troy.commons.exchange.model.enums.FuturesOrderSideEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class FuturesOpenOrderReqDto implements Serializable {

    //订单主键
    private Long futuresTransId;

    //交易所code
    private String exchCode;

    //用户账户ID
    private Long accountId;

    //订单ID
    private String orderId;

    //交易所创建时间
    private Date createTime;

    /**
     * 交易对名称
     */
    private String symbol;

    //合约ID,如：BTC-USD-180213
    private String futuresCode;

    //买卖方向，1:开多2:开空3:平多4:平空
    private FuturesOrderSideEnum direction;

    //下单数量
    private BigDecimal size;

    //下单价
    private BigDecimal price;

    //成交数量
    private BigDecimal filledSize;

    //成交均价
    private BigDecimal filledPrice;

    //状态：
    private Integer status;

    //保证金
    private BigDecimal deposit;

}

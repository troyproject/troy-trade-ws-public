package com.troy.trade.ws.model.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.troy.commons.dto.out.ResData;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * TODO 需要明确rest接口的对象与此对象的关系
 *
 * 当前挂单信息返回实体
 * 前台更新数据方法：（为了防止后台错误推送造成的数据混乱，提供rest接口供查询使用）
 *      根据交易流水号进行查找
 *      -- 如果当前列表中有该流水号，则根据状态进行更新
 *          -- 状态为1（部分成交）、10（已挂单）状态，则直接替换本条数据
 *          -- 状态为非1、10的状态，则将该条记录从列表中删除
 *      -- 如果当前列表中没有该流水号，则直接添加该记录到列表中
 *
 * @author dp
 */
@Setter
@Getter
public class OpenOrderResponse extends ResData {

    /**
     * 交易流水号
     */
    @JsonProperty("i")
    private String transFlowId;

    /**
     * 订单号
     */
    @JsonProperty("o")
    private String orderId;

    /**
     * 挂单时间（到s的时间戳）
     */
    @JsonProperty("T")
    private long time;

    /**
     * 挂单方向（1-买 2-卖）
     */
    @JsonProperty("s")
    private int side;

    /**
     * 交易对名称
     */
    @JsonProperty("S")
    private String symbol;

    /**
     * 价格
     */
    @JsonProperty("p")
    private String price;

    /**
     * 数量
     */
    @JsonProperty("a")
    private BigDecimal amount;

    /**
     * 交易额
     */
    @JsonProperty("t")
    private BigDecimal total;

    /**
     * 成交数量
     */
    @JsonProperty("e")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String execAmt = "0";

    /**
     * 未成交数量
     */
    @JsonProperty("u")
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String unExecAmt= "0";

    /**
     * 状态
     */
    @JsonProperty("ss")
    private int status;

}

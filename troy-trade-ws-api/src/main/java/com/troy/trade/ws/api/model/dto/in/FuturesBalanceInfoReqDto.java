package com.troy.trade.ws.api.model.dto.in;

import com.troy.commons.dto.in.ReqData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 合约账户余额查询结果实体
 * @author yanping
 */
@Getter
@Setter
public class FuturesBalanceInfoReqDto extends ReqData {

    /**
     * 账户权益
     */
    private String equity;

    /**
     * 已实现盈亏
     */
    private String realizedPnl;

    /**
     * 未实现盈亏
     */
    private String unrealizedPnl;

    /**
     * 今日参考盈亏
     */
    private String referencePnl;

    /**
     * 可用余额
     */
    private String available;

    /**
     * 已用保证金（挂单冻结+持仓已用）
     */
    private String margin;

    /**
     * 持仓已用保证金
     */
    private String marginFrozen;

    /**
     * 挂单冻结保证金
     */
    private String marginUnfilled;

    /**
     * 保证金率，乘100之后的值，不带%
     */
    private String marginRatio;

    /**
     * 持仓列表
     */
    private List<FuturesPositionInfoReqDto> futuresPositionInfoReqDtos;

    public FuturesBalanceInfoReqDto() {
        super();
    }

    public FuturesBalanceInfoReqDto(String equity,
                                String realizedPnl, String unrealizedPnl,
                                String referencePnl, String available, String margin, String marginFrozen,
                                String marginUnfilled, String marginRatio, List<FuturesPositionInfoReqDto> futuresPositionInfoReqDtos) {
        this.equity = equity;
        this.realizedPnl = realizedPnl;
        this.unrealizedPnl = unrealizedPnl;
        this.referencePnl = referencePnl;
        this.available = available;
        this.margin = margin;
        this.marginFrozen = marginFrozen;
        this.marginUnfilled = marginUnfilled;
        this.marginRatio = marginRatio;
        this.futuresPositionInfoReqDtos = futuresPositionInfoReqDtos;
    }

    public static FuturesBalanceInfoReqDto getInstance(String equity,
                                                   String realizedPnl, String unrealizedPnl,
                                                   String referencePnl, String available, String margin, String marginFrozen,
                                                   String marginUnfilled, String marginRatio, List<FuturesPositionInfoReqDto> futuresPositionInfoReqDtos){
        return new FuturesBalanceInfoReqDto(equity, realizedPnl, unrealizedPnl,
                referencePnl, available, margin, marginFrozen,
                marginUnfilled, marginRatio, futuresPositionInfoReqDtos);
    }
}

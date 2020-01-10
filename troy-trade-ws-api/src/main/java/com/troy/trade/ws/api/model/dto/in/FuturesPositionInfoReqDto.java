package com.troy.trade.ws.api.model.dto.in;

import com.troy.commons.dto.in.ReqData;
import com.troy.commons.exchange.model.enums.AliasEnum;
import com.troy.commons.exchange.model.enums.PositionSideEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 合约账户余额查询结果实体
 * @author yanping
 */
@Getter
@Setter
public class FuturesPositionInfoReqDto extends ReqData {

    /**
     * 交易对名称，如：BTC/USD
     */
    private String symbol;

    /**
     * 合约类型，当周、次周、季度、永续
     */
    private AliasEnum alias;

    /**
     * 交易所币对名称
     */
    private String tradeSymbol;

    /**
     * 多空方向，1-多、2-空
     */
    private PositionSideEnum positionSide;

    /**
     * 杠杆倍数
     */
    private String leverage;

    /**
     * 持仓可平
     */
    private String availQty;

    /**
     * 收益
     */
    private String pnl;

    /**
     * 已实现盈亏
     */
    private String realisedPnl;

    /**
     * 未实现盈亏
     */
    private String unrealisedPnl;

    /**
     * 结算基准价
     */
    private String settlementPrice;

    /**
     * 保证金
     */
    private String margin;

    /**
     * 收益率
     */
    private String pnlRatio;

    /**
     * 预估强平价
     */
    private String liquidationPrice;

    /**
     * 开仓均价
     */
    private String avgPrice;

    /**
     * 已结算收益
     */
    private String settledPnl;

    public FuturesPositionInfoReqDto() {
        super();
    }

    public FuturesPositionInfoReqDto(String symbol,AliasEnum alias,String tradeSymbol,
                                 PositionSideEnum positionSide, String leverage, String availQty,
                                 String realisedPnl, String unrealisedPnl, String settlementPrice,
                                 String margin, String pnlRatio, String liquidationPrice,
                                 String avgPrice, String settledPnl,String pnl) {
        this.symbol = symbol;
        this.alias = alias;
        this.tradeSymbol = tradeSymbol;
        this.positionSide = positionSide;
        this.leverage = leverage;
        this.availQty = availQty;
        this.realisedPnl = realisedPnl;
        this.unrealisedPnl = unrealisedPnl;
        this.settlementPrice = settlementPrice;
        this.margin = margin;
        this.pnlRatio = pnlRatio;
        this.liquidationPrice = liquidationPrice;
        this.avgPrice = avgPrice;
        this.settledPnl = settledPnl;
        this.pnl = pnl;
    }

    public static FuturesPositionInfoReqDto getInstance(String symbol,AliasEnum alias,String tradeSymbol,
                                                    PositionSideEnum positionSide, String leverage, String availQty,
                                                    String realisedPnl, String unrealisedPnl, String settlementPrice,
                                                    String margin, String pnlRatio, String liquidationPrice,
                                                    String avgPrice, String settledPnl,String pnl){
        return new FuturesPositionInfoReqDto(symbol, alias, tradeSymbol,
                positionSide, leverage, availQty, realisedPnl, unrealisedPnl, settlementPrice,
                margin, pnlRatio, liquidationPrice, avgPrice, settledPnl,pnl);
    }
}

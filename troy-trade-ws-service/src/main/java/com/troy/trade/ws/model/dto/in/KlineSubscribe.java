package com.troy.trade.ws.model.dto.in;

/**
 * Kline 订阅实体
 *
 * @author yanping
 * @date 2019/8/05
 */
public class KlineSubscribe extends RequestBaseDto {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1018626169425693625L;
	/**
     * k线周期，周期数字，1、5、30
     */
    private Integer interval;

    /**
     * k线周期类型，min、hour、day、week、year
     */
    private String intervalType;

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }
}

package com.troy.trade.ws.model.dto.in;

/**
 * 市场成交记录订阅实体
 *
 * @author yanping
 * @date 2019/8/06
 */
public class TradeSubscribe extends RequestBaseDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6130385707667833946L;

	private Integer limit;

	/**
	 * sessionId
	 */
	private String sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
}

package com.troy.trade.ws.model.dto.in;

/**
 * 盘口订阅实体 DepthSubscribe
 *
 * @author yanping
 * @date 2019/8/05
 */
public class DepthSubscribe extends RequestBaseDto {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5873613507924464151L;

	/**
     * 返回数据大小
     */
    private Integer limit;

    /**
     * 深度
     */
    private String interval;

    /**
     * sessionId
     */
    private String sessionId;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

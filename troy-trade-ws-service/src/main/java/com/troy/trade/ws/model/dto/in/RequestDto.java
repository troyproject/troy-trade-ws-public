package com.troy.trade.ws.model.dto.in;

import java.io.Serializable;

/**
 * RequestDto
 *
 * @author yanping
 * @date 2019/8/05
 */
public class RequestDto<T> implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8394170810596619897L;

	/**
     * 客户客户端ID
     */
    private String id;

    /**
     * 推送消息类型
     */
    private String method;

    /**
     * 入参
     */
    private T params;

    public RequestDto() {
        super();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

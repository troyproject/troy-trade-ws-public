package com.troy.trade.ws.model.dto.in;

/**
 * RequestDto
 *
 * @author yanping
 * @date 2019/8/05
 */
public class SendParamDto<T> extends RequestBaseDto {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8394170810596619897L;

	/**
     * 客户用户ID
     */
    private String userId;

    /**
     * 入参
     */
    private T params;

    public SendParamDto() {
        super();
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }
}

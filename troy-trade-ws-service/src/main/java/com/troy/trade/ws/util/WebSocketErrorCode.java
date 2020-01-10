package com.troy.trade.ws.util;

/**
 * 
 * @author yanping
 *
 * 错误码
 */
public enum WebSocketErrorCode {

	/**
	 * 成功
	 */
	SUCCESS("200", "success"),

	/**
	 * 失败
	 */
	FAIL("201", "fail"),


	/**
	 * 权限验证失败
	 */
	TOKEN_FAIL("30001", "token fail"),

	/**
	 * 权限验证失败
	 */
	AUTHORIZATION_FAIL("30002", "Authorization fail"),

	/**
	 * 权限验证失败
	 */
	PARAMETER_FAIL("30003", "The required parameter is null"),

	/**
	 * 权限验证失败
	 */
	PARAMETER_ERROR("30004", "The required parameter error"),

	/**
	 * 自定义错误类型
	 */
	FAIL_99999("99999", "");

	private String code;
	private String msg;

	/**
	 * 是否成功
	 *
	 * @return
	 */
	public boolean isSuccess() {
		return SUCCESS.code.equals(code);
	}

	/**
	 * Constructor
	 *
	 * @param code
	 * @param msg
	 */
	private WebSocketErrorCode(String code, String msg){
		this.setCode(code);
		this.setMsg(msg);
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	@Override
    public String toString() {
        return "[" + this.code + "]" + this.msg;
    }
	
}


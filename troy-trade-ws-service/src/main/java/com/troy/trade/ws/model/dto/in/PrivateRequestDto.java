package com.troy.trade.ws.model.dto.in;

/**
 * 私有账户订阅实体
 */
public class PrivateRequestDto extends RequestBaseDto {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8774602214440431417L;

	/**
     * 账户ID
     */
    private String accountId;

    private String authorization;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
}

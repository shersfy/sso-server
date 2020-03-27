package org.young.sso.server.config.prelogin;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(PreLoginProperties.PREFIX)
public class PreLoginProperties {
	
	public static final String PREFIX ="prelogin";
	
	/**
	 * 测试用户ID
	 */
	private long userId = 1;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	

}

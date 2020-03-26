package org.young.sso.server.prelogin;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(PreLoginProperties.PREFIX)
public class PreLoginProperties {
	
	public static final String PREFIX ="prelogin";

	/**
	 * 是否开启(默认false关闭)
	 */
	private boolean enabled = false;
	
	/**
	 * 测试用户ID
	 */
	private long userId = 1;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	

}

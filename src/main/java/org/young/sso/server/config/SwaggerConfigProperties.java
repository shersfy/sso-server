package org.young.sso.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(SwaggerConfigProperties.PREFIX)
public class SwaggerConfigProperties {
	
	public static final String PREFIX = "sso.server.swagger";
	
	/**
	 * 是否开启(默认false关闭)
	 */
	private boolean enabled = false;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}

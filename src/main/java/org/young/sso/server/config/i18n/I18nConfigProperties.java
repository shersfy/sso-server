package org.young.sso.server.config.i18n;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author pengy
 * 2018年10月22日
 */
@ConfigurationProperties(I18nConfigProperties.PREFIX)
public class I18nConfigProperties {
	
	public static final String PREFIX ="i18n.config";
	
	/**
	 * Default false.
	 * Flag i18n is enabled
	 */
	private boolean enabled = false;
	
	/**
	 * Default classpath:i18n/*.properties. 
	 * messages_*.properties files location. 
	 */
	private String location = "classpath:i18n/*.properties";
	/**
	 * Default "lang". 
	 * Set request header language parameter. 
	 */
	private String headerKey = "lang";

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getHeaderKey() {
		return headerKey;
	}

	public void setHeaderKey(String headerKey) {
		this.headerKey = headerKey;
	}
}

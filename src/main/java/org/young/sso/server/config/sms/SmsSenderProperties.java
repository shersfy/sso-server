package org.young.sso.server.config.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(SmsSenderProperties.PREFIX)
public class SmsSenderProperties {
	
	public static final String PREFIX ="sms";
	
	/**
	 * 是否开启(默认true开启)
	 */
	private boolean enabled = true;

	/**
	 * 短信应用SDK AppID
	 */
	private int appid = 1400146417;
	
	/**
	 * 短信应用SDK AppKey
	 */
	private String appkey = "32681bea45f347686b295e2d4be4f373";
	
	/**
	 * 短信签名
	 */
	private String smsSign = "";

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getSmsSign() {
		return smsSign;
	}

	public void setSmsSign(String smsSign) {
		this.smsSign = smsSign;
	}
	
}

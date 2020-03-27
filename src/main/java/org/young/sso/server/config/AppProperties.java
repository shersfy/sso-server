package org.young.sso.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(AppProperties.PREFIX)
public class AppProperties {
	
	public static final String PREFIX = "sso.server.app";
	
	private long requestKeyMaxLimit = 10;
	
	private long requestKeyMaxAgeSeconds = 60;
	
	/**
	 * 发送验证码间隔时间
	 */
	private int sendVerifyCodeIntervalMinutes = 1;
	/**
	 * 发送验证码验证码的生命值
	 */
	private int sendVerifyCodeMaxAgeMinutes  = 5;
	
	/**
	 * 发送验证码短消息模板[参数1：验证码，参数2：用户，参数3：有效时间]
	 */
	private String sendVerifyCodeTemplate = "";
	
	private String dfsServer = "https://edpfs.edmpglobal.com";

	public long getRequestKeyMaxLimit() {
		return requestKeyMaxLimit;
	}

	public void setRequestKeyMaxLimit(long requestKeyMaxLimit) {
		this.requestKeyMaxLimit = requestKeyMaxLimit;
	}

	public long getRequestKeyMaxAgeSeconds() {
		return requestKeyMaxAgeSeconds;
	}

	public void setRequestKeyMaxAgeSeconds(long requestKeyMaxAgeSeconds) {
		this.requestKeyMaxAgeSeconds = requestKeyMaxAgeSeconds;
	}

	public int getSendVerifyCodeIntervalMinutes() {
		return sendVerifyCodeIntervalMinutes;
	}

	public void setSendVerifyCodeIntervalMinutes(int sendVerifyCodeIntervalMinutes) {
		this.sendVerifyCodeIntervalMinutes = sendVerifyCodeIntervalMinutes;
	}

	public int getSendVerifyCodeMaxAgeMinutes() {
		return sendVerifyCodeMaxAgeMinutes;
	}

	public void setSendVerifyCodeMaxAgeMinutes(int sendVerifyCodeMaxAgeMinutes) {
		this.sendVerifyCodeMaxAgeMinutes = sendVerifyCodeMaxAgeMinutes;
	}

	public String getSendVerifyCodeTemplate() {
		return sendVerifyCodeTemplate;
	}

	public void setSendVerifyCodeTemplate(String sendVerifyCodeTemplate) {
		this.sendVerifyCodeTemplate = sendVerifyCodeTemplate;
	}

	public String getDfsServer() {
		return dfsServer;
	}

	public void setDfsServer(String dfsServer) {
		this.dfsServer = dfsServer;
	}
}

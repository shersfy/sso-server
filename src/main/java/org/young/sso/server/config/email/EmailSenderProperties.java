package org.young.sso.server.config.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(EmailSenderProperties.PREFIX)
public class EmailSenderProperties {
	
	public static final String PREFIX ="sso.server.email";
	
	/**
	 * 是否开启(默认true关闭)
	 */
	private boolean enabled = true;
	/**
	 * 发送者账户
	 */
	private String username;

	/**
	 * 邮件模板html文件
	 */
	private Resource template;
	
	/**
	 * 默认主题
	 */
	private String defaultSubject = "System Information - SSO";

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Resource getTemplate() {
		return template;
	}

	public void setTemplate(Resource template) {
		this.template = template;
	}

	public String getDefaultSubject() {
		return defaultSubject;
	}

	public void setDefaultSubject(String defaultSubject) {
		this.defaultSubject = defaultSubject;
	}
	
	
}

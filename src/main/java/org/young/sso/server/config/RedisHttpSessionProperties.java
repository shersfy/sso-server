package org.young.sso.server.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties
public class RedisHttpSessionProperties {
	
	@Value("${spring.session.timeout.seconds}")
	private int timeoutSeconds = 300;

	@Value("${spring.session.redis.namespace}")
	private String namespace = "spring:session";
	
	public String getSessionKey(String sessionId) {
		String key = "%s:sessions:%s";
		key = String.format(key, namespace, sessionId);
		return key;
	}

	public String getSessionExpiresKey(String sessionId) {
		String key = "%s:sessions:expires:%s";
		key = String.format(key, namespace, sessionId);
		return key;
	}

	public String getSessionAttrKey(String name) {
		if (StringUtils.isNotBlank(name)) {
			name = "sessionAttr:"+name;
		}
		return name;
	}

	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

}

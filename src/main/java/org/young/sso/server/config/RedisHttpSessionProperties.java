package org.young.sso.server.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;

@RefreshScope
@ConfigurationProperties
public class RedisHttpSessionProperties {

	@Value("${spring.session.timeout.seconds}")
	private int timeoutSeconds = 300;

	@Value("${spring.session.redis.namespace}")
	private String namespace = "spring:session";

	@Autowired
	private StringRedisTemplate redis;

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

	public Map<String, Object> getSessionAttrs(String sessionId){

		Map<String, Object> attrs = new HashMap<>();
		if (StringUtils.isBlank(sessionId)) {
			return attrs;
		}

		String sessionKey = getSessionKey(sessionId);
		redis.opsForHash().entries(sessionKey).forEach((k, v)->{
			attrs.put(k.toString(), v);
		});

		return attrs;
	}


	public Object getSessionAttr(Map<String, Object> attrs, String name) {
		if (attrs==null) {
			return null;
		}
		return attrs.get(getSessionAttrKey(name));
	}

	public void setSessionAttr(String sessionId, String name , Object value) {
		String sessionKey = getSessionKey(sessionId);
		String attrKey    = getSessionAttrKey(name);
		redis.opsForHash().put(sessionKey, attrKey, value);
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

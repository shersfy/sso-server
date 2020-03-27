package org.young.sso.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.young.sso.sdk.autoconfig.SsoAutoConfiguration;

@Configuration
@EnableRedisHttpSession
@EnableConfigurationProperties(RedisHttpSessionProperties.class)
@ConditionalOnClass(SsoAutoConfiguration.class)
public class RedisHttpSessionConfig {
	
	@Autowired
	private RedisHttpSessionProperties sessionProperties;
	
	@Autowired
	private RedisHttpSessionConfiguration redisHttpSessionConfiguration;
	
	@Bean
	public RedisOperationsSessionRepository sessionRepository() {
		RedisOperationsSessionRepository sessionRepository = redisHttpSessionConfiguration.sessionRepository();
		sessionRepository.setRedisKeyNamespace(sessionProperties.getNamespace());
		sessionRepository.setDefaultMaxInactiveInterval(sessionProperties.getTimeoutSeconds());
		return sessionRepository;
	}
	
	@Bean
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		serializer.setCookiePath("/");
		serializer.setCookieName("_edp_");
		return serializer;
	}
	
}

package org.young.sso.server.config;

import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.young.sso.sdk.autoconfig.SsoAutoConfiguration;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.sdk.listener.SessionSharedListener;
import org.young.sso.sdk.utils.SsoUtil;
import org.young.sso.server.beans.Const;
import org.young.sso.server.service.impl.SignOutService;
import org.young.sso.server.service.kdc.TicketGrantingTicket;

@Configuration
@EnableRedisHttpSession
@EnableConfigurationProperties(RedisHttpSessionProperties.class)
@ConditionalOnClass(SsoAutoConfiguration.class)
public class RedisHttpSessionConfig implements SessionSharedListener{
	
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SsoProperties ssoconf;
	
	@Lazy
	@Autowired
	private StringRedisTemplate redis;
	
	@Autowired
	private SignOutService signOutService;
	
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
		serializer.setCookieName(ssoconf.getCookie().getSidname());
		if (StringUtils.isNotBlank(ssoconf.getCookie().getDomain())) {
			serializer.setDomainName(ssoconf.getCookie().getDomain());
		}
		serializer.setUseHttpOnlyCookie(ssoconf.getCookie().isHttpOnly());
		serializer.setUseSecureCookie(ssoconf.getCookie().isSecure());
		return serializer;
	}

	@Override
	public void removeWebapps(String sessionId) {
		signOutService.signOut(sessionId, ssoconf.getRequestRemoteRetry());
	}
	
	@Override
	public void invalidateSession(String sessionId) {
		if (ssoconf.isAutoRemoveWebappFromServer()) {
			removeWebapps(sessionId);
		}
		// 删除缓存
		String sessionKey = sessionProperties.getSessionKey(sessionId);
		redis.delete(sessionKey);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		String sessionId = event.getSession().getId();
		
		Object obj = event.getSession().getAttribute(Const.TICKET_PREFIX_TGT);
		invalidateSession(sessionId);
		
		if (obj==null) {
			return;
		}
		// 2.删除缓存
		TicketGrantingTicket tgt = signOutService.removeTGT(sessionId);
		if (tgt==null) {
			LOGGER.info("destroyed session successful. session={}, TGT=null", sessionId);
			return;
		}
		LOGGER.info("destroyed session successful by user '{}'. session={}, TGT={}", 
				tgt.getUser().getUsername(), sessionId, SsoUtil.hiddenTicket(obj.toString()));
	}
	
}

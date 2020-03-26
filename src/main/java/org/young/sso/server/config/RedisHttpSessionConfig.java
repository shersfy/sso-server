package org.young.sso.server.config;

import java.util.List;

import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
import org.young.sso.sdk.autoconfig.ConstSso;
import org.young.sso.sdk.autoconfig.SsoAutoConfiguration;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.sdk.listener.SessionSharedListener;
import org.young.sso.sdk.resource.LoginWebapp;
import org.young.sso.sdk.utils.HttpUtil;
import org.young.sso.sdk.utils.SsoUtil;
import org.young.sso.server.beans.TicketGrantingCookie;
import org.young.sso.server.service.impl.SignOutService;
import org.young.sso.server.utils.AesUtil;

import com.alibaba.fastjson.JSON;

@Configuration
@EnableRedisHttpSession
@EnableConfigurationProperties(RedisHttpSessionProperties.class)
@ConditionalOnBean(SsoAutoConfiguration.class)
public class RedisHttpSessionConfig implements SessionSharedListener{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private SsoProperties ssoProperties;
	
	@Autowired
	private RedisHttpSessionProperties sessionProperties;
	
	@Autowired
	private SignOutService signOutService;
	
	@Lazy
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Lazy
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
	
	@Override
	public void removeWebapps(String sessionId) {
		if (StringUtils.isBlank(sessionId)) {
			return;
		}
		// 销毁登录应用session
		List<LoginWebapp> loginWebapps = signOutService.getLoginWebapps(sessionId);
		loginWebapps.forEach(app->{
			app.getSessions().forEach(webappSession->{
				String webappSignout = app.getApplogout();
				if (!StringUtils.startsWithIgnoreCase(webappSignout, "http")) {
					webappSignout = HttpUtil.concatUrl(app.getAppserver(), webappSignout);
				}
				signOutService.signOut(webappSignout, webappSession, signOutService.getRetry());
			});
		});
	}
	
	@Override
	public void invalidateSession(String sessionId) {
		if (ssoProperties.isAutoRemoveWebappFromServer()) {
			removeWebapps(sessionId);
		}
		// 删除缓存
		String sessionKey = sessionProperties.getSessionKey(sessionId);
		redisTemplate.delete(sessionKey);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		String sessionId = event.getSession().getId();
		Object tgc = event.getSession().getAttribute(ConstSso.LOGIN_TGC_KEY);
		if (tgc==null) {
			return;
		}
		// 2.删除缓存
		this.invalidateSession(sessionId);
		TicketGrantingCookie info = null;
		try {
			String text = AesUtil.decryptHexStr(tgc.toString(), AesUtil.AES_SEED);
			info = JSON.parseObject(text, TicketGrantingCookie.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		
		logger.info("invalidate session '{}' successful by user '{}'. t={}", sessionId, info.getUsername(), SsoUtil.hiddenToken(tgc.toString()));
		
	}
	
}

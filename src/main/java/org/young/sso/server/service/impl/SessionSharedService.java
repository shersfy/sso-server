package org.young.sso.server.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.young.sso.sdk.autoconfig.ConstSso;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.sdk.listener.SessionSharedListener;
import org.young.sso.sdk.resource.LoginWebapp;
import org.young.sso.sdk.utils.SsoAESUtil;
import org.young.sso.sdk.utils.HttpUtil;
import org.young.sso.sdk.utils.HttpUtil.HttpResult;
import org.young.sso.sdk.utils.SsoUtil;
import org.young.sso.server.beans.Const;
import org.young.sso.server.beans.TicketGrantingCookie;
import org.young.sso.server.config.RedisHttpSessionProperties;

import com.alibaba.fastjson.JSON;

@Component
public class SessionSharedService implements SessionSharedListener{

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private StringRedisTemplate redis;
	
	@Autowired
	private SsoProperties ssoProperties;

	@Autowired
	private RedisHttpSessionProperties sessionProperties;

	public int getRetry() {
		return ssoProperties.getRequestRemoteRetry();
	}

	/**
	 * 退出已登录应用
	 * @param webappSignoutFull
	 * @param webappSession
	 */
	@Async
	public void signOut(String webappSignoutFull, String webappSession, int retry) {
		if (retry<=0) {
			return;
		}

		Map<String, Object> param  = new HashMap<>();
		param.put(ConstSso.LOGIN_SESSION_ID, webappSession);
		HttpResult res = HttpUtil.post(webappSignoutFull, param, null);
		if (res.getCode()==HttpStatus.SC_OK) {
			LOGGER.info("sign out webapp successful '{}' by session '{}'", webappSignoutFull, webappSession);
			return;
		}

		int seconds = retry >3? 3:retry;
		seconds = 4-seconds;
		seconds = seconds*10;

		LOGGER.warn("sign out failed. code={}, body={}", res.getCode(), res.getBody());
		LOGGER.info("sign out webapp '{}' by session '{}', await {} seconds retry ...", webappSignoutFull, webappSession, seconds);
		try {
			Thread.sleep(seconds*1000);
		} catch (Exception e) {
		}
		signOut(webappSignoutFull, webappSession, retry-1);
	}

	/**
	 * 从SSO服务器session获取已登录应用的注册信息
	 * @param sessionId
	 * @return
	 */
	public List<LoginWebapp> getLoginWebapps(String sessionId){
		Map<String, Object> attrs = sessionProperties.getSessionAttrs(sessionId);

		String webappKey = Const.SESSION_LOGIN_WEBAPPS;
		List<LoginWebapp> loginWebapps = null;
		Object obj = sessionProperties.getSessionAttr(attrs, webappKey);
		if (obj!=null) {
			loginWebapps =  JSON.parseArray(obj.toString(), LoginWebapp.class);
		} else {
			loginWebapps = new ArrayList<>();
		}
		LOGGER.info("== getLoginWebapps sessionId={}, text={}", sessionId, loginWebapps);
		return loginWebapps;
	}

	/**
	 * 已登录应用注册信息存储到SSO服务器session
	 * @param sessionId
	 * @param loginWebapps
	 */
	public void setLoginWebapps(String sessionId, List<LoginWebapp> loginWebapps){
		// 设置新值
		String key  = Const.SESSION_LOGIN_WEBAPPS;
		String text = JSON.toJSONString(loginWebapps);
		LOGGER.info("== setSessionAttr sessionId={}, key={}, text={}", sessionId, key, text);
		sessionProperties.setSessionAttr(sessionId, key, text);
	}

	@Override
	public void removeWebapps(String sessionId) {
		if (StringUtils.isBlank(sessionId)) {
			return;
		}
		// 销毁登录应用session
		List<LoginWebapp> loginWebapps = getLoginWebapps(sessionId);
		loginWebapps.forEach(app->{
			app.getSessions().forEach(webappSession->{
				String webappSignout = app.getApplogout();
				if (!StringUtils.startsWithIgnoreCase(webappSignout, "http")) {
					webappSignout = HttpUtil.concatUrl(app.getAppserver(), webappSignout);
				}
				signOut(webappSignout, webappSession, getRetry());
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
		redis.delete(sessionKey);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		String sessionId = event.getSession().getId();
		Object tgc = event.getSession().getAttribute(ConstSso.LOGIN_TICKET_KEY);
		if (tgc==null) {
			return;
		}
		// 2.删除缓存
		this.invalidateSession(sessionId);
		TicketGrantingCookie info = null;
		try {
			String text = SsoAESUtil.decryptHexStr(tgc.toString(), SsoAESUtil.AES_SEED);
			info = JSON.parseObject(text, TicketGrantingCookie.class);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		
		LOGGER.info("invalidate session '{}' successful by user '{}'. t={}", sessionId, info.getUsername(), SsoUtil.hiddenToken(tgc.toString()));
		
	}
}

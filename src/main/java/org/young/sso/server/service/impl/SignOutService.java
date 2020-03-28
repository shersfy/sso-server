package org.young.sso.server.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.young.sso.sdk.resource.LoginWebapp;
import org.young.sso.sdk.utils.HttpUtil;
import org.young.sso.sdk.utils.HttpUtil.HttpResult;
import org.young.sso.server.beans.Const;
import org.young.sso.server.config.RedisHttpSessionProperties;
import org.young.sso.server.service.kdc.KeyDistributionCenter;
import org.young.sso.server.service.kdc.TicketGrantingTicket;

import com.alibaba.fastjson.JSON;

@Component
public class SignOutService {

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private KeyDistributionCenter kdc;
	
	@Autowired
	private RedisHttpSessionProperties sessionProperties;

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
		param.put(Const.LOGIN_SESSION_ID, webappSession);
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
		
		LOGGER.debug("== getLoginWebapps sessionId={}, text={}", sessionId, loginWebapps);
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
		
		LOGGER.debug("== setSessionAttr sessionId={}, key={}, text={}", sessionId, key, text);
		sessionProperties.setSessionAttr(sessionId, key, text);
	}


	public TicketGrantingTicket removeTGT(String session) {
		TicketGrantingTicket obj = kdc.getTGT(session);
		if (obj!=null && kdc.removeTGT(session)) {
			LOGGER.info("removed TGT '{}'", kdc.getTGTKey(session));
		}
		return obj;
	}
}

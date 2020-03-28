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
import org.young.sso.server.service.kdc.KeyDistributionCenter;
import org.young.sso.server.service.kdc.TicketGrantingTicket;

@Component
public class SignOutService {

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private KeyDistributionCenter kdc;
	
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
	 * 从TGT获取已登录应用的注册信息
	 * @param sessionId sso server session id
	 * @return
	 */
	public List<LoginWebapp> getLoginWebapps(String sessionId){
		
		TicketGrantingTicket tgt = kdc.findTGT(sessionId);
		if (tgt == null) {
			return new ArrayList<>();
		}
		
		return tgt.getWebapps();
	}

	/**
	 *  已登录应用注册信息存储到TGT
	 * @param sessionId sso server session id
	 * @param webapp
	 */
	public void addLoginWebapp(String sessionId, LoginWebapp webapp){
		// 设置新值
		TicketGrantingTicket tgt = kdc.findTGT(sessionId);
		if (tgt == null) {
			return;
		}
		
		tgt.getWebapps().add(webapp);
		kdc.updateTGT(tgt);
		
		LOGGER.info("webapp register successful. appsession={}, appserver={}, applogout={}", 
				webapp.getSession(), webapp.getAppserver(), webapp.getApplogout());
	}


	public TicketGrantingTicket removeTGT(String session) {
		TicketGrantingTicket obj = kdc.findTGT(session);
		if (obj!=null && kdc.removeTGT(session)) {
			LOGGER.info("removed TGT '{}'", kdc.getTGTKey(session));
		}
		return obj;
	}
}

package org.young.sso.server.controller;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.young.sso.sdk.autoconfig.ConstSso;
import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.sdk.resource.SsoResult.ResultCode;
import org.young.sso.server.beans.Const;
import org.young.sso.server.config.AppProperties;
import org.young.sso.server.config.I18nCodes;

import com.alibaba.fastjson.JSON;

public class BaseController implements I18nCodes{

	protected static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
	protected static final int SUCESS = ResultCode.SUCESS;
	protected static final int FAIL	  = ResultCode.FAIL;

	private static ThreadLocal<HttpServletRequest> THREAD_LOCAL_REQUEST = new ThreadLocal<>();
	private static ThreadLocal<HttpServletResponse> THREAD_LOCAL_RESPONSE = new ThreadLocal<>();

	@Autowired
	protected AppProperties properties;
	
	@ModelAttribute
	public void setRequestAndResponse(HttpServletRequest request, HttpServletResponse response) {
		THREAD_LOCAL_REQUEST.set(request);
		THREAD_LOCAL_RESPONSE.set(response);
	}

	public HttpServletRequest getRequest() {
		return THREAD_LOCAL_REQUEST.get();
	}

	public HttpServletResponse getResponse() {
		return THREAD_LOCAL_RESPONSE.get();
	}

	public String getRemoteAddr() {
		String remoteAddr = getRequest().getRemoteAddr();
		remoteAddr = "0:0:0:0:0:0:0:1".equals(remoteAddr)?"127.0.0.1":remoteAddr;
		return remoteAddr;
	}
	public String getRemoteHost() {
		String remoteAddr = getRequest().getRemoteHost();
		remoteAddr = "0:0:0:0:0:0:0:1".equals(remoteAddr)?"localhost":remoteAddr;
		return remoteAddr;
	}

	public String getBasePath() {
		return getRequest().getAttribute(ConstSso.BASE_PATH).toString();
	}

	public LoginUser getLoginUser() {
		Object user = getRequest().getSession().getAttribute(Const.SESSION_LOGIN_USER);
		return user==null?null:JSON.parseObject(user.toString(), LoginUser.class);
	}
	
	public String getTGC() {
		Object tgc = getRequest().getSession().getAttribute(ConstSso.LOGIN_TGC_KEY);
		return tgc==null?null:tgc.toString();
	}

	protected String getBaseServerUrl(String url) throws MalformedURLException {
		URL base = new URL(url);

		StringBuilder serverUrl = new StringBuilder(0);
		serverUrl.append(base.getProtocol()).append("://");
		serverUrl.append(base.getHost());
		if (-1!=base.getPort() && 80!=base.getPort() && 433!=base.getPort()) {
			serverUrl.append(":").append(base.getPort());
		}

		return serverUrl.toString();
	}

}

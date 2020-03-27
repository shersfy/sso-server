package org.young.sso.server.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.young.sso.sdk.autoconfig.ConstSso;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.sdk.resource.SsoResult.ResultCode;
import org.young.sso.server.beans.Const;
import org.young.sso.server.config.AppProperties;
import org.young.sso.server.config.I18nCodes;
import org.young.sso.server.config.i18n.I18nMessages;

import com.alibaba.fastjson.JSON;

public class BaseController implements I18nCodes{

	protected static final int SUCESS = ResultCode.SUCESS;
	protected static final int FAIL	  = ResultCode.FAIL;

	private static ThreadLocal<HttpServletRequest> THREAD_LOCAL_REQUEST = new ThreadLocal<>();
	private static ThreadLocal<HttpServletResponse> THREAD_LOCAL_RESPONSE = new ThreadLocal<>();

	@Autowired
	protected AppProperties properties;
	
	@Autowired
	protected SsoProperties ssoProperties;
	
	@Autowired
	private I18nMessages i18n;
	
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
	
	protected void saveLoginUser(LoginUser loginUser) {
		if (loginUser==null) {
			return;
		}
		getRequest().getSession().setAttribute(Const.SESSION_LOGIN_USER, loginUser.toString());
	}

	public LoginUser getLoginUser() {
		Object user = getRequest().getSession().getAttribute(Const.SESSION_LOGIN_USER);
		return user==null?null:JSON.parseObject(user.toString(), LoginUser.class);
	}
	
	public String getTGC() {
		Object tgc = getRequest().getSession().getAttribute(ConstSso.LOGIN_TICKET_KEY);
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
	
	public String errors(List<ObjectError> list) {
		StringBuffer msg = new StringBuffer(0);
		String lang = getRequest().getHeader("lang");
		lang = StringUtils.isBlank(lang) ?Locale.CHINA.toString() :lang;
		if (list!=null){
			for (ObjectError err: list){

				String code		= err.getCode();
				String name		= err.getObjectName();
				Object[] args	= err.getArguments();
				Object rejected = null;

				List<Object> argList = new ArrayList<>();
				if (err instanceof FieldError){
					FieldError fErr = (FieldError) err;
					name = fErr.getField();
					code = fErr.getDefaultMessage();
					rejected = fErr.getRejectedValue();
				}

				argList.add(name);
				if (args!=null){
					List<Object> subargs = new ArrayList<>();
					for(Object arg :args){
						if (arg instanceof Pattern.Flag[]) {
							subargs.add(rejected);
							continue;
						}
						if (arg instanceof DefaultMessageSourceResolvable){
							continue;
						}
						if (arg instanceof Boolean
								&& (DecimalMin.class.getSimpleName().equals(code)
										|| DecimalMax.class.getSimpleName().equals(code))){
							continue;
						}
						subargs.add(arg);
					}
					if (MSGC000006.equals(err.getDefaultMessage())) {
						subargs.sort((o1, o2)->o1.toString().compareTo(o2.toString()));
					}
					argList.addAll(subargs);
				}

				if (rejected!=null){
					argList.add(rejected);
				}

				String imsg = i18n!=null && i18n.getI18n(lang)!=null ?i18n.getI18n(lang).getProperty(err.getDefaultMessage()) :"";
				imsg = StringUtils.isNotBlank(imsg) ?String.format(imsg, argList.toArray()): imsg;

				msg.append(imsg).append(";");
			}
		}
		return msg.toString();
	}

	protected String getI18nMsg(String i18nCode, Object ...args) {
		String lang = getRequest().getHeader("lang");
		lang = StringUtils.isBlank(lang) ?Locale.CHINA.toString() :lang;
		String imsg = i18n.getI18n(lang)!=null ?i18n.getI18n(lang).getProperty(i18nCode, args) :"";
		return imsg;
	}


	protected Set<String> getI18nKeys() {
		String lang = getRequest().getHeader("lang");
		lang = StringUtils.isBlank(lang) ?Locale.CHINA.toString() :lang;
		Set<Object> keys = i18n.getI18n(lang)!=null ?i18n.getI18n(lang).keySet() :null;

		Set<String> i18nKeys = new HashSet<>();
		if (keys==null) {
			return i18nKeys;
		}

		keys.forEach(key->i18nKeys.add(key.toString()));
		return i18nKeys;
	}

}

package org.young.sso.server.config.prelogin;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.sdk.utils.SsoUtil;
import org.young.sso.server.beans.Const;
import org.young.sso.server.beans.TicketGrantingCookie;
import org.young.sso.server.model.UserInfo;
import org.young.sso.server.service.UserInfoService;
import org.young.sso.server.utils.AesUtil;

public class PreLoginFilter implements Filter {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private UserInfoService userInfoService;
	
	private SsoProperties ssoProperties;
	
	private PreLoginProperties preLoginProperties;
	
	private String tgcStr;
	
	private LoginUser loginUser;

	public PreLoginFilter(UserInfoService userInfoService, SsoProperties ssoProperties, PreLoginProperties preLoginProperties) {
		this.userInfoService = userInfoService;
		this.ssoProperties   = ssoProperties;
		this.preLoginProperties = preLoginProperties;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info("PreLoginFilter init ...");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req  = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		LOGGER.info("pre login request url:{}", req.getRequestURL());
		if (req.getSession().getAttribute(Const.SESSION_LOGIN_USER)==null) {
			UserInfo testUser = userInfoService.findById(preLoginProperties.getUserId());
			loginUser = userInfoService.poToLoginUser(testUser);
			
			TicketGrantingCookie tgc = new TicketGrantingCookie(loginUser.getUserId(), loginUser.getUsername(), req.getSession().getId());
			tgc.setLoginTimestamp(System.currentTimeMillis());
			tgc.setRandom(RandomStringUtils.random(6));
			tgc.setLoginId(String.valueOf(System.currentTimeMillis()));
			
			// 存储token
			tgcStr = AesUtil.encryptHexStr(tgc.toString(), AesUtil.AES_SEED);
			SsoUtil.saveTGC(req, res, ssoProperties, tgcStr);
			SsoUtil.saveLanguage(req, res, ssoProperties, Locale.CHINA.toString());
			req.getSession().setAttribute(Const.SESSION_LOGIN_USER, loginUser);
			
			LOGGER.debug("dev debug login user: {}, token: {}", loginUser.toString(), tgcStr);
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}
	
}

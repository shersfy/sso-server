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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.sso.sdk.autoprop.SsoProperties;
import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.sdk.utils.SsoUtil;
import org.young.sso.server.beans.Const;
import org.young.sso.server.model.UserInfo;
import org.young.sso.server.service.UserInfoService;

public class PreLoginFilter implements Filter {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private UserInfoService userInfoService;
	
	private SsoProperties ssoProperties;
	
	private PreLoginProperties preLoginProperties;
	
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
			loginUser.setLoginId(String.valueOf(System.currentTimeMillis()));
			
			req.getSession().invalidate();
			String tgc = req.getSession().getId();
			String tgt = userInfoService.generateTGT(loginUser, tgc);
			
			// 存储登录用户信息
			req.getSession().setAttribute(Const.TICKET_PREFIX_TGT, tgt);
			SsoUtil.saveLoginUser(req, loginUser);
			SsoUtil.saveLanguage(req, res, ssoProperties, Locale.CHINA.toString());
			
			LOGGER.debug("dev debug login user: {}, TGC: {}", loginUser.toString(), tgc);
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}
	
}

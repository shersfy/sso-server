package org.young.sso.server.controller;

import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.young.sso.sdk.utils.SsoAESUtil;
import org.young.sso.server.beans.Const;
import org.young.sso.server.service.kdc.KeyDistributionCenter;
import org.young.sso.server.service.kdc.TicketGrantingTicket;

@Controller
public class PageController extends BaseController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private KeyDistributionCenter kdc;
	
	@GetMapping("/")
	public ModelAndView index(String webapp) {
		ModelAndView mv = new ModelAndView("redirect:/goto");
		if (StringUtils.isBlank(webapp) || "#".equals(webapp)) {
			// 默认应用首页为空，重定向到sso server首页
			webapp = ssoProperties.getWebappServer();
			if (StringUtils.isBlank(webapp) || "#".equals(webapp)) {
				mv.setViewName("index");
				return mv;
			}
		}
		mv.addObject("webapp", webapp);
		return mv;
	}

	@GetMapping("/index")
	public ModelAndView index2() {
		ModelAndView mv = new ModelAndView("redirect:/goto");
		return mv;
	}

	@GetMapping("/index.html")
	public ModelAndView index3() {
		ModelAndView mv = new ModelAndView("redirect:/goto");
		return mv;
	}

	@GetMapping("/goto")
	public ModelAndView gotoWebapp(String webapp) {
		
		ModelAndView mv = new ModelAndView();
		if (StringUtils.isBlank(webapp) || "#".equals(webapp)) {
			// 默认应用首页为空，重定向到sso server首页
			webapp = ssoProperties.getWebappServer();
			if (StringUtils.isBlank(webapp) || "#".equals(webapp)) {
				mv.setViewName("redirect:/");
				return mv;
			}
		}
		
		try {
			webapp = URLDecoder.decode(webapp, "UTF-8");
			String apphost = new URL(webapp).getHost();
			
			String tgc = getRequest().getSession().getId();
			TicketGrantingTicket tgt = new TicketGrantingTicket(getLoginUser(), tgc);
			String rk = tgt.getRandom();
			String st = kdc.generateST(tgt, apphost);
			rk = String.format("%s-%s", Const.TICKET_PREFIX_RK, SsoAESUtil.encryptHexStr(rk, apphost));
			
			StringBuilder redirect = new StringBuilder(0);
			redirect.append(webapp);
			redirect.append(webapp.contains("?")? "&" :"?");
			redirect.append(Const.LOGIN_TICKET_KEY).append("=").append(st);
			redirect.append("&").append(Const.LOGIN_REQUEST_KEY).append("=").append(rk);
			mv.setViewName("redirect:"+redirect.toString());
			
			return mv;
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		
		return mv;
	}
	
	@GetMapping("/login")
	public ModelAndView login1(String webapp) {
		ModelAndView mv = new ModelAndView("redirect:/login/");
		mv.addObject("webapp", webapp);
		return mv;
	};
	
	@GetMapping("/login/")
	public ModelAndView login2(String webapp) {
		ModelAndView mv = new ModelAndView("login");
		if (StringUtils.isNotBlank(webapp)) {
			mv.addObject("webapp", webapp);
		}
		// 已登录
		if (getTGT()!=null) {
			if (StringUtils.isNotBlank(webapp)) {
				mv.setViewName("redirect:/goto");
			} else {
				mv.setViewName("redirect:/");
			}
		}
		
		return mv;
	}

}

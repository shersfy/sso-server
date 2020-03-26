package org.young.sso.server.controller;

import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.young.sso.server.service.UserInfoService;

@Controller
public class PageController extends BaseController {

	@Autowired
	private UserInfoService userInfoService;
	
	@GetMapping("/")
	public ModelAndView index(String webapp) {
		ModelAndView mv = new ModelAndView("index");
		return mv;
	}

	@GetMapping("/index")
	public ModelAndView index2() {
		ModelAndView mv = new ModelAndView("redirect:/");
		return mv;
	}

	@GetMapping("/index.html")
	public ModelAndView index3() {
		ModelAndView mv = new ModelAndView("redirect:/");
		return mv;
	}

	@GetMapping("/goto")
	public ModelAndView gotoWebapp(String webapp, String tenant) {
		
		ModelAndView mv = new ModelAndView("redirect:/");
		if (StringUtils.isBlank(webapp) || "#".equals(webapp)) {
			return mv;
		}
		
		try {
			webapp = URLDecoder.decode(webapp, "UTF-8");
			String webappServer = getBaseServerUrl(webapp);
			
			// 移除首页地址不匹配
			webapp = webapp.replaceFirst("http://|https://", "");
			webapp = webapp.contains("?") ?webapp.substring(0, webapp.indexOf("?")) :webapp;
			
			String key = userInfoService.generateRequestKey(getRemoteAddr());
			StringBuilder redirect = new StringBuilder(0);
//			redirect.append(appinfo.getWebappHome());
//			redirect.append(appinfo.getWebappHome().contains("?")?"&":"?").append(ConstSso.LOGIN_TOKEN_KEY).append("=").append(getToken());
//			redirect.append("&").append(ConstSso.LOGIN_REQUEST_KEY).append("=").append(key);
//			redirect.append("&").append(ConstSso.WEBAPP_ID).append("=").append(apps.get(0).getId());
//			if (tenant!=null) {
//				redirect.append("&tenant=").append(tenant);
//			}
//			if (state!=null) {
//				redirect.append("&").append(state.toString());
//			}
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
		if (getTGC()!=null) {
			if (StringUtils.isNotBlank(webapp)) {
				mv.setViewName("redirect:/goto");
			} else {
				mv.setViewName("redirect:/");
			}
		}
		
		return mv;
	}

}

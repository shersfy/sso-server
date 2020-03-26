package org.young.sso.server.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BaseErrorController extends BaseController 
	implements ErrorController{
	
	@Override
	public String getErrorPath() {
		return "/error";
	}
	
	@RequestMapping("/error")
	public ModelAndView error() {
		ModelAndView mv = new ModelAndView("error");
		String code = getRequest().getParameter("code");
		String msg  = getRequest().getParameter("msg");
		if (code==null) {
			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			try {
				status = HttpStatus.valueOf(getResponse().getStatus());
			} catch (Exception e) {
				LOGGER.error("", e);
			}
			msg = status.getReasonPhrase();
			Object error = getRequest().getAttribute("javax.servlet.error.message");
			if (error!=null && StringUtils.isNotBlank(error.toString())) {
				StringBuffer buff = new StringBuffer(msg);
				buff.append("; ").append(error.toString());
				msg = buff.toString();
			}
			code = String.valueOf(status.value());
		}
		
		mv.addObject("code", code);
		mv.addObject("msg",  msg);
		
		switch (code) {
		case "404":
			mv.setViewName("404");
			break;
		case "500":
			mv.setViewName("500");
			break;
		default:
			break;
		}
		return mv;
	}
	
}

package org.young.sso.server.controller;

import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.young.sso.sdk.resource.LoginType;
import org.young.sso.sdk.resource.SsoResult;
import org.young.sso.sdk.utils.HttpUtil;
import org.young.sso.sdk.utils.HttpUtil.HttpResult;
import org.young.sso.server.beans.IdInfo;
import org.young.sso.server.config.I18nCodes;
import org.young.sso.server.config.RsaKeyPair;
import org.young.sso.server.config.i18n.I18nModel;
import org.young.sso.server.service.UserInfoService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Controller
public class WeixinController extends BaseController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Lazy
	@Autowired
	private RsaKeyPair keyPair;

	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private UserInfoController userInfoController;
	
	@GetMapping("/login/wx_{rk}")
	public ModelAndView login(@PathVariable("rk") String requestKey) {
		ModelAndView mv = new ModelAndView("redirect:/");
		
		String wxAuthCode = getRequest().getParameter("auth_code");
		String webapp     = getRequest().getParameter("webapp");
		if (isLogined() 
				|| StringUtils.isBlank(requestKey)
				|| StringUtils.isBlank(wxAuthCode)) {
			return mv;
		}
		
		// 校验requestKey
		SsoResult res = userInfoService.checkRequestKey(requestKey, getRemoteAddr());
		if (res.getCode()!=SUCESS) {
			I18nModel i18n = (I18nModel) res.getModel();
			i18n = i18n==null ?new I18nModel(MSGE000008) :i18n;
			
			int code = I18nCodes.getCode(i18n.getKey());
			String msg = getI18nMsg(i18n.getKey(), i18n.getArgs());
			
			setErrorView(mv, code, msg);
			return mv;
		}
		
		// 校验wxAuthCode
		res = getWxLoginInfo(wxAuthCode);
		if (res.getCode()!=SUCESS) {
			int code = res.getCode();
			String msg = res.getMsg();
			
			setErrorView(mv, code, msg);
			return mv;
		}
		
		// 校验用户登录
		JSONObject json = (JSONObject) res.getModel();
		String corpid   = json.getJSONObject("corp_info").getString("corpid");
		String userid   = json.getJSONObject("user_info").getString("userid");
		try {
			String id = mockId(userid);
			res = userInfoController.signIn(id);
		} catch (Exception e) {
			LOGGER.error("", e);
			res.setCode(FAIL);
		}
		
		if (res.getCode()!=SUCESS) {
			I18nModel i18n = (I18nModel) res.getModel();
			i18n = i18n==null ?new I18nModel(MSGE000008) :i18n;
			
			int code = I18nCodes.getCode(i18n.getKey());
			String msg = getI18nMsg(i18n.getKey(), i18n.getArgs());
			
			setErrorView(mv, code, msg);
			return mv;
		}
		
		if (StringUtils.isNotBlank(webapp)) {
			
			// 截取重定向地址webapp
			String search = "webapp=";
			if (webapp.contains(search)) {
				webapp = webapp.substring(webapp.indexOf(webapp)+webapp.length());
			}
			
			// 追加corpid参数
			String[] arr = webapp.split("#");
			arr[0] = arr[0].contains("?") ?(arr[0]+"&corpid="+corpid):(arr[0]+"?corpid="+corpid);
			webapp = StringUtils.join(arr, "#");
			
			mv.setViewName("redirect:/goto");
			mv.addObject("webapp", webapp);
		}
		
		return mv;
	}
	
	private void setErrorView(ModelAndView mv, int code, String msg) {
		LOGGER.error("session '{}' loginByWeixin() error: {}", getRequest().getSession().getId(), msg);
		try {
			msg = URLEncoder.encode(msg, "UTF-8");
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		
		mv.setViewName(String.format("redirect:/error?code=%s&msg=%s", code, msg));
	}
	

	private SsoResult getWxLoginInfo(String authCode) {
		
		SsoResult res = new SsoResult();
		
		// 获取服务商provider_access_token
		// 参考API: https://work.weixin.qq.com/api/doc/90001/90142/90593#%E6%9C%8D%E5%8A%A1%E5%95%86%E7%9A%84token
		String url = "https://qyapi.weixin.qq.com/cgi-bin/service/get_provider_token";
		JSONObject body = new JSONObject();
		body.put("corpid", ssoconf.getWeixin().getCorpid());
		body.put("provider_secret", ssoconf.getWeixin().getProviderSecret());
		
		HttpResult httpRes = retryPostJson(url, body.toJSONString(), null, ssoconf.getRequestRemoteRetry());
		if (httpRes.getCode()!=200) {
			res.setCode(httpRes.getCode());
			res.setMsg(httpRes.getBody());
			return res;
		}
		
		JSONObject json = JSON.parseObject(httpRes.getBody());
		if (json.getIntValue("errcode")!=SUCESS) {
			res.setCode(json.getIntValue("errcode"));
			res.setMsg(json.getString("errmsg"));
			return res;
		}
		
		// 获取登录用户信息
		// 参考API： https://work.weixin.qq.com/api/doc/90001/90143/91125
		String accessToken = json.getString("provider_access_token");
		url = "https://qyapi.weixin.qq.com/cgi-bin/service/get_login_info?access_token="+accessToken;
		body.clear();
		body.put("auth_code", authCode);
		
		httpRes = retryPostJson(url, body.toJSONString(), null, ssoconf.getRequestRemoteRetry());
		if (httpRes.getCode()!=200) {
			res.setCode(httpRes.getCode());
			res.setMsg(httpRes.getBody());
			return res;
		}
		
		json = JSON.parseObject(httpRes.getBody());
		if (json.getIntValue("errcode")!=SUCESS) {
			res.setCode(json.getIntValue("errcode"));
			res.setMsg(json.getString("errmsg"));
			return res;
		}
		
		res.setModel(json);
		return res;
	}
	
	private String mockId(String username) throws Exception {
		String rk = userInfoService.generateRequestKey(getRemoteAddr());
		IdInfo id = new IdInfo();
		id.setUsername(username);
		id.setType(LoginType.qrcode);
		id.setK(rk);
		
		byte[] bytes = keyPair.getRsaJsEncryptor().encrypt(id.toString().getBytes());
		return new String(bytes);
	}
	
	
	private HttpResult retryPostJson(String url, String body, 
			Map<String, String> header, int retry) {

		if (retry<1) {
			HttpResult error = new HttpResult(520, url);
			error.setBody("retry timeout");
			return error;
		}

		LOGGER.info("POST http request: {}", url);
		HttpResult res = HttpUtil.postJson(url, body, header);
		if (res.getCode()!=200) {
			LOGGER.error(res.toString());
			sleep();
			return retryPostJson(url, body, header, retry-1);
		}

		return res;
	}
	
	private void sleep() {
		int seconds = RandomUtils.nextInt(1, 5);
		LOGGER.info("await {} seconds retry ...", seconds);
		try {
			Thread.sleep(seconds*1000);
		} catch (Exception e) {
			LOGGER.error("", "");
		}
	}

}

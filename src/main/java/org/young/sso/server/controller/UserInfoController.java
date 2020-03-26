package org.young.sso.server.controller;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.sdk.resource.ServiceTicket;
import org.young.sso.sdk.resource.SsoResult;
import org.young.sso.sdk.resource.SsoResult.ResultCode;
import org.young.sso.sdk.utils.SsoUtil;
import org.young.sso.server.beans.Const;
import org.young.sso.server.beans.IdInfo;
import org.young.sso.server.beans.RequestKey;
import org.young.sso.server.beans.TicketGrantingCookie;
import org.young.sso.server.config.I18nCodes;
import org.young.sso.server.config.RsaKeyPair;
import org.young.sso.server.config.RsaKeyPair.RsaJsEncryptor;
import org.young.sso.server.config.i18n.I18nModel;
import org.young.sso.server.controller.form.PasswordEditForm;
import org.young.sso.server.controller.form.PasswordForgetForm;
import org.young.sso.server.logs.LogManager;
import org.young.sso.server.logs.LoginLog;
import org.young.sso.server.service.UserInfoService;
import org.young.sso.server.utils.AesUtil;

import com.alibaba.fastjson.JSON;

@RestController
public class UserInfoController extends BaseController {

	@Lazy
	@Autowired
	private RsaKeyPair keyPair;

	@Autowired
	private SsoProperties ssoProperties;

	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private LogManager logManager;

	@PostMapping("/login/k")
	public SsoResult loginKey() {

		SsoResult res = new SsoResult();
		String prefix = userInfoService.getCacheKeyPrefix(Const.LOGIN_CACHE_KEY_PREFIX);
		prefix = String.format("%s_%s", prefix, getRemoteAddr());
		// 限制次数
		if (userInfoService.countRequestKey(prefix) >= properties.getRequestKeyMaxLimit()) {
			// 操作过于频繁，请稍后重试
			res.setCode(I18nCodes.getCode(MSGE000010));
			res.setModel(new I18nModel(MSGE000010));
			return res;
		}

		String key = userInfoService.generateRequestKey(getRemoteAddr());
		res.setModel(new RequestKey(key, keyPair.getRsaJsEncryptor().getPemPubKey()));
		return res;
	}

	@PostMapping("/login/send")
	public SsoResult loginSendTo(@RequestParam(required=true) Integer type) {
		SsoResult res = new SsoResult();
		Object tmpUser = getRequest().getSession().getAttribute("tmpUser");
		LoginUser loginUser = getLoginUser();
		if (tmpUser==null && loginUser==null) {
			res.setCode(ResultCode.NOT_LOGIN);
			res.setModel(new I18nModel(MSGE000013));
			return res;
		}

		loginUser = tmpUser!=null? (LoginUser)tmpUser: loginUser;

		switch (type) {
		case 1:
			// 发送短信
			res = userInfoService.sendCodeToPhone(loginUser);
			break;
		case 2:
			// 发送邮件
			res = userInfoService.sendCodeToEmail(loginUser);
			break;
		default:
			res.setCode(FAIL);
			res.setMsg("not support type:"+type);
			break;
		}

		return res;
	}


	@PostMapping("/login/pwd/forget")
	public SsoResult loginForgetPassword(@RequestParam(required=true) String data) {
		SsoResult res = null;
		RsaJsEncryptor encryptor = keyPair.getRsaJsEncryptor();
		try {
			// 解密
			String text = encryptor.decrypt(data);
			PasswordForgetForm form = JSON.parseObject(text, PasswordForgetForm.class);
			// 空值校验
			if (StringUtils.isBlank(form.getK())) {
				res = new SsoResult();
				res.setCode(FAIL);
				res.setModel(new I18nModel(MSGC000003, "k"));
				return res;
			}

			String key = AesUtil.decryptHexStr(form.getK(), AesUtil.AES_SEED);
			res = userInfoService.checkRequestKey(key, getRemoteAddr());
			if (res.getCode()!=SUCESS) {
				return res;
			}

			// 忘记密码查询用户
			res = userInfoService.forgetPassword(form);
			if (res.getCode()!=SUCESS) {
				return res;
			}

			// 存储session
			getRequest().getSession().setAttribute("tmpUser", res.getModel());
		} catch (Exception e) {
			LOGGER.error("", e);
			res = res==null?new SsoResult():res;
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGE000012));
		}

		return res;
	}

	@PostMapping("/login/pwd/edit")
	public SsoResult loginEditPwd(@RequestParam(required=true) String data) {
		SsoResult res = new SsoResult();
		Object tmpUser = getRequest().getSession().getAttribute("tmpUser");
		LoginUser loginUser = getLoginUser();
		if (tmpUser==null && loginUser==null) {
			res.setCode(ResultCode.NOT_LOGIN);
			res.setModel(new I18nModel(MSGE000013));
			return res;
		}

		loginUser = tmpUser!=null? (LoginUser)tmpUser: loginUser;

		RsaJsEncryptor encryptor = keyPair.getRsaJsEncryptor();
		try {
			// 解密
			String text = encryptor.decrypt(data);
			PasswordEditForm form = JSON.parseObject(text, PasswordEditForm.class);
			res = userInfoService.updatePassword(loginUser, form.getCode(), form.getPassword());
			// 退出登录
			signOut();
		} catch (Exception e) {
			LOGGER.error("", e);
			res.setCode(I18nCodes.getCode(MSGE000012));
			res.setModel(new I18nModel(MSGE000012));
			return res;
		}

		return res;
	}

	@PostMapping("/sign/in")
	public SsoResult signIn(@RequestParam(required=true) String id) {
		SsoResult res = null;
		RsaJsEncryptor encryptor = keyPair.getRsaJsEncryptor();
		try {
			// 解密
			String text = encryptor.decrypt(id);
			IdInfo idInfo = JSON.parseObject(text, IdInfo.class);

			String key = AesUtil.decryptHexStr(idInfo.getK(), AesUtil.AES_SEED);
			res = userInfoService.checkRequestKey(key, getRemoteAddr());
			if (res.getCode()!=SUCESS) {
				return res;
			}

			// 登录
			res = userInfoService.signIn(idInfo);
			if (res.getCode()!=SUCESS) {
				if (res.getCode()==ResultCode.TMP_LOGIN) {
					res.setCode(SUCESS);
					getRequest().getSession().setAttribute("tmpUser", res.getModel());
				}
				return res;
			}

			LoginUser loginUser = (LoginUser) res.getModel();
			String uuid = String.valueOf(System.nanoTime());
			LoginLog log = new LoginLog(uuid, loginUser.getUserId(), loginUser.getUsername(), 
					getRemoteAddr(), getRemoteHost(), idInfo.getLang(), 
					getBasePath()+"/sign/in", System.currentTimeMillis());
			// TGC
			TicketGrantingCookie tgc = new TicketGrantingCookie(loginUser.getUserId(), 
					loginUser.getUsername(), getRequest().getSession().getId());
			tgc.setLoginTimestamp(System.currentTimeMillis());
			tgc.setRandom(RandomStringUtils.random(6));
			tgc.setLoginId(log.getUuid());

			// 存储TGC
			String tgcStr = AesUtil.encryptHexStr(tgc.toString(), AesUtil.AES_SEED);
			SsoUtil.saveTGC(getRequest(), getResponse(), ssoProperties, tgcStr);
			SsoUtil.saveLanguage(getRequest(), getResponse(), ssoProperties, log.getLoginLang());

			// 存储登录用户信息
			saveLoginUser(loginUser);

			// 记录登录日志

			logManager.sendLog(log);
			LOGGER.info("sign in successful. session={}, t={}", 
					getRequest().getSession().getId(), SsoUtil.hiddenToken(tgcStr));

		} catch (Exception e) {
			LOGGER.error("id="+id, e);
			res = res==null?new SsoResult():res;
			res.setCode(I18nCodes.getCode(MSGE000008));
			res.setModel(new I18nModel(MSGE000008));
		}
		return res;
	}

	protected void saveLoginUser(LoginUser loginUser) {
		if (loginUser==null) {
			return;
		}
		getRequest().getSession().setAttribute(Const.SESSION_LOGIN_USER, loginUser.toString());
	}

	/**
	 * 退出过滤器已处理
	 * @return
	 */
	@PostMapping("/sign/out")
	public SsoResult signOut() {
		SsoResult res = new SsoResult();
		SsoUtil.invalidateSession(getRequest().getSession());
		return res;
	}

	/**
	 * 校验token
	 * @return
	 */
	@PostMapping("/login/validate")
	public SsoResult loginValidate(@RequestParam(required=true) String data) {

		RsaJsEncryptor encryptor = keyPair.getRsaJsEncryptor();
		try {
			if (ssoProperties.isEnabledRsa()) {
				data = encryptor.decrypt(data);
			}
		} catch (Exception e) {
			LOGGER.error("", e);
			SsoResult res = new SsoResult();
			res.setCode(I18nCodes.getCode(MSGE000012));
			res.setModel(new I18nModel(MSGE000012));
			return res;
		}

		try {
			ServiceTicket st = JSON.parseObject(data, ServiceTicket.class);
			return userInfoService.validate(st);
		} catch (Exception e) {
			LOGGER.error("", e);
			SsoResult res = new SsoResult();
			res.setCode(I18nCodes.getCode(MSGC000007));
			res.setModel(new I18nModel(MSGC000007, "data", data));
			return res;
		}
	}

	@GetMapping("/user/current")
	public SsoResult getCurrentLoginUser() {
		return new SsoResult(getLoginUser());
	}

}

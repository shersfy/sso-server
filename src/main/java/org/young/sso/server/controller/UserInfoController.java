package org.young.sso.server.controller;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.sdk.resource.SsoResult;
import org.young.sso.sdk.resource.SsoResult.ResultCode;
import org.young.sso.sdk.utils.SsoAESUtil;
import org.young.sso.sdk.utils.SsoUtil;
import org.young.sso.server.beans.Const;
import org.young.sso.server.beans.IdInfo;
import org.young.sso.server.beans.RequestKey;
import org.young.sso.server.config.I18nCodes;
import org.young.sso.server.config.RsaKeyPair;
import org.young.sso.server.config.RsaKeyPair.RsaJsEncryptor;
import org.young.sso.server.config.i18n.I18nModel;
import org.young.sso.server.controller.form.PasswordEditForm;
import org.young.sso.server.controller.form.PasswordForgetForm;
import org.young.sso.server.controller.form.UserInfoEditForm;
import org.young.sso.server.controller.form.ValidateForm;
import org.young.sso.server.model.UserInfo;
import org.young.sso.server.service.UserInfoService;
import org.young.sso.server.service.logs.LogManager;
import org.young.sso.server.service.logs.LoginLog;

import com.alibaba.fastjson.JSON;

@RestController
public class UserInfoController extends BaseController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Lazy
	@Autowired
	private RsaKeyPair keyPair;

	@Autowired
	private SsoProperties ssoProperties;

	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private LogManager logManager;
	
	@PostMapping("/sign/in")
	public SsoResult signIn(@RequestParam(required=true) String id) {
		SsoResult res = null;
		RsaJsEncryptor encryptor = keyPair.getRsaJsEncryptor();
		try {
			// 解密
			String text = encryptor.decrypt(id);
			IdInfo idInfo = JSON.parseObject(text, IdInfo.class);

			String key = SsoAESUtil.decryptHexStr(idInfo.getK(), SsoAESUtil.AES_SEED);
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
			
			Long userId = loginUser.getUserId();
			String username = loginUser.getUsername();
			String remoteAddr = getRemoteAddr();
			String remoteHost = getRemoteHost();
			String loginLang  = idInfo.getLang();
			String loginUrl   = getBasePath()+"/sign/in";
			
			// 记录登录日志
			LoginLog log = new LoginLog(userId, username, remoteAddr, remoteHost, loginLang, loginUrl);
			logManager.sendLog(log);
			loginUser.setLoginId(log.getLoginId());
			
			// 生成TGT
			getRequest().getSession().invalidate();
			String tgc = getRequest().getSession().getId();
			String tgt = userInfoService.generateTGT(loginUser, tgc);
			
			// 存储登录用户信息
			saveLoginUser(loginUser, tgt, log.getLoginLang());
			LOGGER.info("sign in successful. session={}, TGT={}", tgc, SsoUtil.hiddenTicket(tgt));

		} catch (Exception e) {
			LOGGER.error("id="+id, e);
			res = res==null?new SsoResult():res;
			res.setCode(I18nCodes.getCode(MSGE000008));
			res.setModel(new I18nModel(MSGE000008));
		}
		return res;
	}

	/**
	 * 退出过滤器已处理
	 * @return
	 */
	@GetMapping("/sign/out")
	public SsoResult signOut() {
		SsoResult res = new SsoResult();
		SsoUtil.invalidateSession(getRequest().getSession());
		return res;
	}

	@PostMapping("/login/k")
	public SsoResult loginKey() {

		SsoResult res = new SsoResult();
		String prefix = String.format("%s_%s", Const.LOGIN_CACHE_KEY_PREFIX, getRemoteAddr());
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
			ValidateForm form = JSON.parseObject(data, ValidateForm.class);
			return userInfoService.validate(form);
		} catch (Exception e) {
			LOGGER.error("", e);
			SsoResult res = new SsoResult();
			res.setCode(I18nCodes.getCode(MSGC000007));
			res.setModel(new I18nModel(MSGC000007, "data", data));
			return res;
		}
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

			String key = SsoAESUtil.decryptHexStr(form.getK(), SsoAESUtil.AES_SEED);
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


	@GetMapping("/user/current")
	public SsoResult getCurrentLoginUser() {
		return new SsoResult(getLoginUser());
	}
	
	@PostMapping("/user/current/edit")
	public SsoResult editCurrentLoginUser(@Valid UserInfoEditForm form, BindingResult binding) {
		// 表单校验
		SsoResult res = new SsoResult();
		if (binding.hasErrors()) {
			res.setCode(FAIL);
			res.setMsg(errors(binding.getAllErrors()));
			return res;
		}
		
		LoginUser loginUser = getLoginUser();
		loginUser.setRealName(form.getRealName());
		loginUser.setPhone(form.getPhone());
		loginUser.setEmail(form.getEmail());
		loginUser.setNote(form.getNote());
		
		UserInfo udp = new UserInfo();
		udp.setId(loginUser.getUserId());
		try {
			BeanUtils.copyProperties(loginUser, udp);
		} catch (Exception e) {
			LOGGER.error("", e);
		}

		// check phone
		userInfoService.checkExist(res, loginUser.getUserId(), null, udp.getPhone(), null);
		if (res.getCode()!=SUCESS) {
			return res;
		}
		// check email
		userInfoService.checkExist(res, loginUser.getUserId(), null, null, udp.getEmail());
		if (res.getCode()!=SUCESS) {
			return res;
		}
		
		if (userInfoService.updateById(udp)==1) {
			saveLoginUser(loginUser);
			res.setModel(true);
		} else {
			res.setModel(true);
		}

		return res;
	}

}

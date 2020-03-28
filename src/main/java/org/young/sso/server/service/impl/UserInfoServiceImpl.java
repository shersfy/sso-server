package org.young.sso.server.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.sdk.resource.LoginWebapp;
import org.young.sso.sdk.resource.SsoResult;
import org.young.sso.sdk.resource.SsoResult.ResultCode;
import org.young.sso.server.beans.Const;
import org.young.sso.server.beans.IdInfo;
import org.young.sso.server.config.I18nCodes;
import org.young.sso.server.config.email.EmailSenderProperties;
import org.young.sso.server.config.i18n.I18nModel;
import org.young.sso.server.config.sms.SmsSender;
import org.young.sso.server.controller.form.PasswordForgetForm;
import org.young.sso.server.controller.form.ValidateForm;
import org.young.sso.server.mapper.BaseMapper;
import org.young.sso.server.mapper.UserInfoMapper;
import org.young.sso.server.model.UserInfo;
import org.young.sso.server.service.UserInfoService;
import org.young.sso.server.service.cache.CacheManager;
import org.young.sso.server.service.kdc.KeyDistributionCenter;
import org.young.sso.server.service.kdc.TicketGrantingTicket;
import org.young.sso.server.utils.MD5Util;

@Service
@Transactional
public class UserInfoServiceImpl extends BaseServiceImpl<UserInfo, Long>
	implements UserInfoService {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserInfoMapper mapper;

	@Autowired
	private CacheManager cache;

	@Autowired
	private KeyDistributionCenter kdc;
	
	@Autowired
	private SignOutService signOutService;

	@Autowired
	private SmsSender smsSender;

	@Autowired
	@Lazy
	private JavaMailSender javaMailSender;
	

	@Autowired
	private EmailSenderProperties emailProperties;
	
	@Override
	public BaseMapper<UserInfo, Long> getMapper() {
		return mapper;
	}

	@Override
	public LoginUser poToLoginUser(UserInfo po) {
		if (po==null) {
			return null;
		}

		// 登录用户
		LoginUser user = new LoginUser(po.getId(), po.getUsername());
		try {
			BeanUtils.copyProperties(po, user);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return user;
	}

	@Override
	public SsoResult signIn(IdInfo id) {
		SsoResult res = new SsoResult();
		if (StringUtils.isBlank(id.getUsername())) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGE000005));
			return res;
		}

		if (StringUtils.isBlank(id.getPassword())) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGC000003, "password"));
			return res;
		}

		UserInfo where = new UserInfo();
		// 邮箱登录
		if (id.getUsername().contains("@")) {
			where.setEmail(id.getUsername());
		}
		// 手机号登录
		else if (id.getUsername().matches("[0-9]+")) {
			where.setPhone(id.getUsername());
		}
		// 用户名登录
		else {
			where.setUsername(id.getUsername());
		}

		UserInfo user = mapper.findByUser(where);
		if (user==null) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGE000001, id.getUsername()));
			return res;
		}

		// 校验密码
		String md5 = MD5Util.encode(id.getPassword());
		if (!md5.equals(user.getPassword())) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGE000002));
			return res;
		}
		user.setPassword(Const.HIDDEN_CODE);
		// 未初始化用户
		if (TMP == user.getStatus()) {
			res.setCode(ResultCode.TMP_LOGIN);
		}
		res.setModel(poToLoginUser(user));
		return res;
	}

	@Override
	public SsoResult validate(ValidateForm form) {
		SsoResult res = new SsoResult();
		if (StringUtils.isBlank(form.getRk())) {
			LOGGER.error("validate error: rk cannot be blank");
			res.setCode(I18nCodes.getCode(MSGC000003));
			res.setModel(new I18nModel(MSGC000003, "rk"));
			return res;
		}
		
		if (StringUtils.isBlank(form.getSt())) {
			LOGGER.error("validate error: st cannot be blank");
			res.setCode(I18nCodes.getCode(MSGC000003));
			res.setModel(new I18nModel(MSGC000003, "st"));
			return res;
		}
		
		if (StringUtils.isBlank(form.getWebappSession())) {
			LOGGER.error("validate error: webappSession cannot be blank");
			res.setCode(I18nCodes.getCode(MSGC000003));
			res.setModel(new I18nModel(MSGC000003, "webappSession"));
			return res;
		}
		
		if (StringUtils.isBlank(form.getWebappServer())) {
			LOGGER.error("validate error: webappServer cannot be blank");
			res.setCode(I18nCodes.getCode(MSGC000003));
			res.setModel(new I18nModel(MSGC000003, "webappServer"));
			return res;
		}
		
		if (StringUtils.isBlank(form.getWebappLogout())) {
			LOGGER.error("validate error: webappLogout cannot be blank");
			res.setCode(I18nCodes.getCode(MSGC000003));
			res.setModel(new I18nModel(MSGC000003, "webappLogout"));
			return res;
		}
		
		// 验证ST有效性
		TicketGrantingTicket tgt = kdc.findTGTByST(form.getSt());
		if (tgt==null) {
			LOGGER.error("validate error: '{}' st already expired", form.getSt());
			res.setCode(I18nCodes.getCode(MSGE000003));
			res.setModel(new I18nModel(MSGE000003));
			return res;
		}
		
		// 验证RK与sso服务器缓存是否相同
		if (!tgt.getRandom().equals(form.getRk())) {
			LOGGER.error("validate error: sso server cached random '{}' not equals webapp rk '{}'", 
					tgt.getRandom(), form.getRk());
			res.setCode(I18nCodes.getCode(MSGE000004));
			res.setModel(new I18nModel(MSGE000004));
			return res;
		}
		
		LOGGER.info("webapp validate successful. user={}", tgt.getUser().getUsername());
		
		// 注册客户端应用
		LoginWebapp webapp = new LoginWebapp(form.getWebappServer(), form.getWebappLogout());
		webapp.setSession(form.getWebappSession());
		signOutService.addLoginWebapp(tgt.getId(), webapp);
		
		// 返回登录用户
		res.setModel(tgt.getUser());
		return res;
	}

	@Override
	public SsoResult sendCodeToPhone(LoginUser user) {
		SsoResult res = new SsoResult();
		if (StringUtils.isBlank(user.getPhone())) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGC000003, "phone"));
			return res;
		}

		// 生成message
		generateSendCodeMsg(user, res);
		if (res.getCode()!=SUCESS) {
			return res;
		}

		// 发送
		String[] params = {res.getModel().toString(), user.getUsername(), String.valueOf(properties.getSendVerifyCodeMaxAgeMinutes())};
		int templateId  = Integer.parseInt(properties.getSendVerifyCodeTemplate());
		res = smsSender.send(user.getPhone(), templateId, params);
		if (res.getCode()!=SUCESS) {
			LOGGER.error(res.getMsg());
			res.setModel(new I18nModel(MSGE000031));
			try {
				cache.delete(getSendCodeLockKey(user.getUserId()));
			} catch (Exception e) {
				LOGGER.error("", e);
			}
			return res;
		}

		LOGGER.info("send to '{}' msg={}", user.getPhone(), res.getModel());
		res.setModel(null);

		return res;
	}

	@Override
	public SsoResult sendCodeToEmail(LoginUser user) {
		SsoResult res = new SsoResult();
		if (StringUtils.isBlank(user.getEmail())) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGC000003, "email"));
			return res;
		}

		// 生成message
		generateSendCodeMsg(user, res);
		if (res.getCode()!=SUCESS) {
			return res;
		}

		// 发送
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setSubject(emailProperties.getDefaultSubject());
			helper.setFrom(emailProperties.getUsername());
			helper.setTo(user.getEmail());

			List<String> lines = IOUtils.readLines(emailProperties.getTemplate().getInputStream(), "UTF8");
			String text = StringUtils.join(lines, "\n");
			text = text.replace("${username}", user.getUsername());
			text = text.replace("${toUser}", user.getUsername());
			text = text.replace("${code}", res.getModel().toString());
			text = text.replace("${minutes}", String.valueOf(properties.getSendVerifyCodeMaxAgeMinutes()));

			helper.setText(text, true);

			javaMailSender.send(message);
			LOGGER.info("send to '{}' msg={}", user.getEmail(), res.getModel());
		} catch (Exception e) {
			LOGGER.error("", e);
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGE000031));
			try {
				cache.delete(getSendCodeLockKey(user.getUserId()));
			} catch (Exception e2) {
				LOGGER.error("", e2);
			}
			return res;
		}

		res.setModel(null);
		return res;
	}

	private String getSendCodeKey(Long userId, String code) {
		String key = String.format("send.code.to.user%s.%s", userId, code);
		return key.toLowerCase();
	}

	private String getSendCodeLockKey(Long userId) {
		String key = String.format("send.code.locked.by.user%s", userId);
		return key;
	}

	/**生成message**/
	private void generateSendCodeMsg(LoginUser user, SsoResult res) {

		String locked = getSendCodeLockKey(user.getUserId());
		if (cache.hasKey(locked)) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGE000010));
			return ;
		}
		cache.set(locked, "locked by "+user.getUsername(), 
				properties.getSendVerifyCodeIntervalMinutes(), TimeUnit.MINUTES);

		String code = RandomStringUtils.randomNumeric(Const.RANDOM_LEN);
		String key  = getSendCodeKey(user.getUserId(), code);
		int maxAge  = properties.getSendVerifyCodeMaxAgeMinutes();
		cache.set(key, code, maxAge, TimeUnit.MINUTES);

		res.setModel(code);
	}

	@Override
	public SsoResult forgetPassword(PasswordForgetForm form) {
		SsoResult res = new SsoResult();

		String username = form.getUsername();
		String phoneOrEmail = form.getPhoneOrEmail();

		// check user name
		if (StringUtils.isBlank(username)) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGC000003, "username"));
			return res;
		}

		// check phone or email
		if (StringUtils.isBlank(phoneOrEmail)) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGC000003, "phoneOrEmail"));
			return res;
		}
		
		if (username.equals(phoneOrEmail)) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGC000023));
			return res;
		}
		
		if (!phoneOrEmail.contains("@") && !phoneOrEmail.matches("[0-9]{11}")) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGC000007, "phone", phoneOrEmail));
			return res;
		}
		
		UserInfo where = new UserInfo();
		// 邮箱登录
		if (form.getUsername().contains("@")) {
			where.setEmail(form.getUsername());
		}
		// 手机号登录
		else if (form.getUsername().matches("[0-9]+")) {
			where.setPhone(form.getUsername());
		}
		// 用户名登录
		else {
			where.setUsername(form.getUsername());
		}

		UserInfo user = mapper.findByUser(where);
		if (user==null) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGE000001, username));
			return res;
		}

		if (!user.getPhone().equals(phoneOrEmail) 
				&& !user.getEmail().equals(phoneOrEmail)) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGE000032, phoneOrEmail));
			return res;
		}

		user.setPassword(Const.HIDDEN_CODE);
		res.setModel(poToLoginUser(user));

		return res;
	}

	@Override
	public SsoResult updatePassword(LoginUser loginUser, String code, String password) {
		SsoResult res = new SsoResult();

		// 空值校验
		if (StringUtils.isBlank(code)) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGC000003, "code"));
			return res;
		}
		if (StringUtils.isBlank(password)) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGC000003, "password"));
			return res;
		}
		if (password.length()<6 || password.length()>18) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGC000006, "password", 6, 18));
			return res;
		}

		// 验证码校验
		String key = getSendCodeKey(loginUser.getUserId(), code);
		if (!cache.hasKey(key)) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGE000030));
			return res;
		}

		password = MD5Util.encode(password);

		UserInfo udp = new UserInfo();
		udp.setId(loginUser.getUserId());
		udp.setPassword(password);
		udp.setStatus(NOR);

		this.updateById(udp);
		cache.delete(key);
		cache.delete(getSendCodeLockKey(loginUser.getUserId()));

		LOGGER.info("update password successful by user '{}'", loginUser.getUsername());
		return res;
	}

	@Override
	public void checkExist(SsoResult res, Long userId, String username, String phone, String email) {
		res = res==null? new SsoResult():res;
		UserInfo where = new UserInfo();

		if (StringUtils.isNotBlank(username)) {
			where.setUsername(username);
			List<UserInfo> list = findList(where);
			if (!list.isEmpty()) {
				if (userId==null) {
					res.setCode(FAIL);
					res.setModel(new I18nModel(MSGE000033, username));
					return;
				}
				for (UserInfo u :list) {
					if (username.equals(u.getUsername()) 
							&& u.getId().longValue()!=userId) {
						res.setCode(FAIL);
						res.setModel(new I18nModel(MSGE000033, username));
						return;
					}
				}
			}
		}

		where.setUsername(null);
		if (StringUtils.isNotBlank(phone)) {
			where.setPhone(phone);
			List<UserInfo> list = findList(where);
			if (!list.isEmpty()) {
				if (userId==null) {
					res.setCode(FAIL);
					res.setModel(new I18nModel(MSGE000033, phone));
					return;
				}
				for (UserInfo u :list) {
					if (phone.equals(u.getPhone()) 
							&& u.getId().longValue()!=userId) {
						res.setCode(FAIL);
						res.setModel(new I18nModel(MSGE000033, phone));
						return;
					}
				}
			}
		}

		where.setPhone(null);
		// check email
		if (StringUtils.isNotBlank(email)) {
			where.setEmail(email);
			List<UserInfo> list = findList(where);
			if (!list.isEmpty()) {
				if (userId==null) {
					res.setCode(FAIL);
					res.setModel(new I18nModel(MSGE000033, email));
					return;
				}
				for (UserInfo u :list) {
					if (email.equals(u.getEmail()) 
							&& u.getId().longValue()!=userId) {
						res.setCode(FAIL);
						res.setModel(new I18nModel(MSGE000033, email));
						return;
					}
				}
			}
		}
	}
	
	@Override
	public String generateTGT(LoginUser loginUser, String tgc) {
		return kdc.generateTGT(loginUser, tgc);
	}

	@Override
	public String generateRequestKey(String remoteAddr) {
		return kdc.generateRequestKey(remoteAddr);
	}

	@Override
	public long countRequestKey(String prefix) {
		return kdc.countRequestKey(prefix);
	}

	@Override
	public SsoResult checkRequestKey(String key, String remoteAddr) {
		return kdc.checkRequestKey(key, remoteAddr);
	}

}

package org.young.sso.server.service;

import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.sdk.resource.ServiceTicket;
import org.young.sso.sdk.resource.SsoResult;
import org.young.sso.server.beans.IdInfo;
import org.young.sso.server.controller.form.PasswordForgetForm;
import org.young.sso.server.model.UserInfo;

public interface UserInfoService extends BaseService<UserInfo, Long> {
	
	LoginUser poToLoginUser(UserInfo po);
	
	/**
	 * 登录
	 * @param id
	 * @return
	 */
	SsoResult signIn(IdInfo id);

	/**
	 * 登录校验
	 * @param st
	 * @return
	 */
	SsoResult validate(ServiceTicket st);
	
	
	String generateRequestKey(String remoteAddr);
	
	SsoResult checkRequestKey(String key, String remoteAddr);
	
	long countRequestKey(String prefix);

	String getCacheKeyPrefix(String prefix);


	SsoResult sendCodeToPhone(LoginUser loginUser);

	SsoResult sendCodeToEmail(LoginUser loginUser);

	SsoResult forgetPassword(PasswordForgetForm form);

	SsoResult updatePassword(LoginUser loginUser, String code, String password);


}

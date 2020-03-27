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
	
	SsoResult sendCodeToPhone(LoginUser loginUser);

	SsoResult sendCodeToEmail(LoginUser loginUser);

	SsoResult forgetPassword(PasswordForgetForm form);

	SsoResult updatePassword(LoginUser loginUser, String code, String password);

	/**
	 * check唯一性
	 * @param res
	 * @param id
	 * @param username
	 * @param phone
	 * @param email
	 */
	void checkExist(SsoResult res, Long userId, String username, String phone, String email);


	String generateRequestKey(String remoteAddr);

	long countRequestKey(String prefix);

	SsoResult checkRequestKey(String key, String remoteAddr);
}

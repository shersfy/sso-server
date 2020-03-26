package org.young.sso.server.controller.form;

import org.young.sso.sdk.resource.BaseBean;

public class PasswordEditForm extends BaseBean{
	
	private static final long serialVersionUID = 1L;
	/**
	 * 验证码
	 */
	private String code;
	/**
	 * 新密码
	 */
	private String password;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}

package org.young.sso.server.controller.form;

import org.young.sso.sdk.resource.BaseBean;

public class PasswordForgetForm extends BaseBean{

	private static final long serialVersionUID = 1L;
	/** 请求key **/
	private String k;
	
	/** 用户名 **/
	private String username;
	
    /** 电话或邮箱**/
    private String phoneOrEmail;

	public String getK() {
		return k;
	}

	public void setK(String k) {
		this.k = k;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhoneOrEmail() {
		return phoneOrEmail;
	}

	public void setPhoneOrEmail(String phoneOrEmail) {
		this.phoneOrEmail = phoneOrEmail;
	}
	
}

package org.young.sso.server.beans;

import java.util.Locale;

import org.young.sso.sdk.resource.BaseBean;

public class IdInfo extends BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String k;
	
	private String username;

	private String password;
	
	private String lang = Locale.CHINA.toString();

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getK() {
		return k;
	}

	public void setK(String k) {
		this.k = k;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}

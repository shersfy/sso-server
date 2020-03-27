package org.young.sso.server.controller.form;

import org.young.sso.sdk.resource.BaseBean;
import org.young.sso.server.config.I18nCodes;

public class ValidateForm extends BaseBean implements I18nCodes{

	private static final long serialVersionUID = 1L;
	
	/**
	 * request key
	 */
	private String rk;
	
	/**
	 * service ticket
	 */
	private String st;
	
	/**
	 * 客户端应用session ID
	 */
	private String webappSession;
	
	/**
	 * 客户端应用服务地址
	 */
	private String webappServer;
	
	/**
	 * 客户端应用退出地址
	 */
	private String webappLogout;

	public String getRk() {
		return rk;
	}

	public void setRk(String rk) {
		this.rk = rk;
	}

	public String getSt() {
		return st;
	}

	public void setSt(String st) {
		this.st = st;
	}

	public String getWebappSession() {
		return webappSession;
	}

	public void setWebappSession(String webappSession) {
		this.webappSession = webappSession;
	}

	public String getWebappServer() {
		return webappServer;
	}

	public void setWebappServer(String webappServer) {
		this.webappServer = webappServer;
	}

	public String getWebappLogout() {
		return webappLogout;
	}

	public void setWebappLogout(String webappLogout) {
		this.webappLogout = webappLogout;
	}
	
}

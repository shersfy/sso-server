package org.young.sso.server.beans;

import org.young.sso.sdk.resource.BaseBean;

public class RequestKey extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 请求KEY
	 */
	private String k; 
	/**
	 * 公钥
	 */
	private String p;
	
	public RequestKey(String k, String p) {
		super();
		this.k = k;
		this.p = p;
	}

	public String getK() {
		return k;
	}
	
	public void setK(String k) {
		this.k = k;
	}
	
	public String getP() {
		return p;
	}
	
	public void setP(String p) {
		this.p = p;
	} 

	
}

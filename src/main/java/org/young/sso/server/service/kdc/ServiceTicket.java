package org.young.sso.server.service.kdc;

import org.young.sso.server.beans.Const;

/**
 * TGT生成分发给客户端应用，仅使用一次后失效
 * @author Young
 * @date 2020-03-28
 */
public class ServiceTicket extends Ticket {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 客户端应用host
	 */
	private String apphost;
	

	public ServiceTicket() {
		super();
	}

	public ServiceTicket(String apphost) {
		super();
		this.apphost = apphost;
	}

	@Override
	public String getPrefix() {
		return Const.TICKET_PREFIX_ST;
	}

	public String getApphost() {
		return apphost;
	}


	public void setApphost(String apphost) {
		this.apphost = apphost;
	}


}

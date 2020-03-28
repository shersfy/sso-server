package org.young.sso.server.service.kdc;

import org.young.sso.server.beans.Const;

public class RequestKey extends Ticket{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 远程地址
	 */
	private String remoteAddr;

	public RequestKey() {
		super();
	}

	public RequestKey(String remoteAddr) {
		super();
		this.remoteAddr = remoteAddr;
	}
	
	@Override
	public String getPrefix() {
		return Const.TICKET_PREFIX_RK;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

}

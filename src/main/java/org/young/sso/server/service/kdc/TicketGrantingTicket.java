package org.young.sso.server.service.kdc;

import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.server.beans.Const;

/**
 * 服务器端票据
 * @author Young
 * @date 2020-03-28
 */
public class TicketGrantingTicket extends Ticket {

	private static final long serialVersionUID = 1L;
	
	private LoginUser user;
	
	public TicketGrantingTicket() {
		super();
	}

	public TicketGrantingTicket(LoginUser user, String tgc) {
		super();
		this.user = user;
		setId(tgc);
	}

	@Override
	public String getPrefix() {
		return Const.TICKET_PREFIX_TGT;
	}
	
	public LoginUser getUser() {
		return user;
	}

	public void setUser(LoginUser user) {
		this.user = user;
	}
	
}

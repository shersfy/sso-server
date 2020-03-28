package org.young.sso.server.service.kdc;

import java.util.ArrayList;
import java.util.List;

import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.sdk.resource.LoginWebapp;
import org.young.sso.server.beans.Const;

/**
 * 服务器端票据
 * @author Young
 * @date 2020-03-28
 */
public class TicketGrantingTicket extends Ticket {

	private static final long serialVersionUID = 1L;
	
	private LoginUser user;
	
	private List<LoginWebapp> webapps;
	
	public TicketGrantingTicket() {
		super();
		this.webapps = new ArrayList<>();
	}

	public TicketGrantingTicket(LoginUser user, String tgc) {
		this();
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

	public List<LoginWebapp> getWebapps() {
		return webapps;
	}

	public void setWebapps(List<LoginWebapp> webapps) {
		this.webapps = webapps;
	}
	
}

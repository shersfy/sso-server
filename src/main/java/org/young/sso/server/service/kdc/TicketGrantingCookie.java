package org.young.sso.server.service.kdc;

import org.young.sso.server.beans.Const;

public class TicketGrantingCookie extends Ticket {

	private static final long serialVersionUID = 1L;

	@Override
	public String getPrefix() {
		return Const.TICKET_PREFIX_TGC;
	}

}

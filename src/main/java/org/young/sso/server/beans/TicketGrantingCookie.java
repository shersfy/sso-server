package org.young.sso.server.beans;

import org.apache.commons.lang.RandomStringUtils;
import org.young.sso.sdk.resource.BaseBean;

public class TicketGrantingCookie extends BaseBean {

	private static final long serialVersionUID = 1L;
	
	private Long userId;
	
	private String username;
	
	private String loginId;
	
	private long loginTimestamp;
	
	private String session;
	
	private String random;

	public TicketGrantingCookie() {
		super();
		this.random = RandomStringUtils.randomAlphanumeric(Const.RANDOM_LEN);
	}
	
	public TicketGrantingCookie(Long userId, String username, String session) {
		super();
		this.userId = userId;
		this.username = username;
		this.session = session;
		this.random = RandomStringUtils.randomAlphanumeric(Const.RANDOM_LEN);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public long getLoginTimestamp() {
		return loginTimestamp;
	}

	public void setLoginTimestamp(long loginTimestamp) {
		this.loginTimestamp = loginTimestamp;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}

	

}

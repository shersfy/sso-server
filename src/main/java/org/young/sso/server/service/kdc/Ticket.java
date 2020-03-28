package org.young.sso.server.service.kdc;

import org.apache.commons.lang.RandomStringUtils;
import org.young.sso.sdk.resource.BaseBean;
import org.young.sso.server.beans.Const;

public abstract class Ticket extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private String id;
	
	/**
	 * 随机数
	 */
	private String random;
	
	/**
	 * 时间戳
	 */
	private long timestamp;

	public Ticket() {
		super();
		this.random = RandomStringUtils.randomAlphanumeric(Const.RANDOM_LEN);
		this.timestamp = System.currentTimeMillis();
	}
	
	public abstract String getPrefix();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}

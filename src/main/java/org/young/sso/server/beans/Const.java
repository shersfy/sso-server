package org.young.sso.server.beans;

import org.young.sso.sdk.autoconfig.ConstSso;

public interface Const extends ConstSso{
	
	/** 票据前缀-TGT **/
	String TICKET_PREFIX_TGT = "TGT";
	/** 票据前缀-TGC **/
	String TICKET_PREFIX_TGC = "TGC";
	/** 票据前缀-ST **/
	String TICKET_PREFIX_ST  = "ST";
	/** 票据前缀-RK **/
	String TICKET_PREFIX_RK  = "RK";
	
	/** 登录请求key缓存key前缀 **/
	String LOGIN_CACHE_KEY_PREFIX   = "login_k";
	/** 登录日志ID缓存key前缀 **/
	String LOGIN_ID_KEY_PREFIX   = "login_id_increment";
	
	/** 随机数长度 **/
	int RANDOM_LEN = 6;

}

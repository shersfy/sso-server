package org.young.sso.server.beans;

public interface Const {
	
	/** 登录请求key缓存key前缀 **/
	String LOGIN_CACHE_KEY_PREFIX   = "login_k";
	/** 登录日志ID缓存key前缀 **/
	String LOGIN_ID_KEY_PREFIX   = "login_id_increment";
	
	/** Session属性已登录用户 **/
	String SESSION_LOGIN_USER    = "loginUser";
	/** Session属性已登录应用 **/
	String SESSION_LOGIN_WEBAPPS = "loginWebapps";
	
	/** EDP 内置数据ID**/
	long EDP_AUTH_ID = 1;
	
	/** 隐藏密码 **/
	String HIDDEN_CODE = "******";
	/** 随机数长度 **/
	int RANDOM_LEN = 6;

}

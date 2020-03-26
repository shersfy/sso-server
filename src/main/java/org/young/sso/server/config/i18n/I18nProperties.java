package org.young.sso.server.config.i18n;

import java.util.Properties;

/**
 * @author pengy
 * 2018年10月22日
 */
public class I18nProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getProperty(String key, String arg) {
		String value = getProperty(key);
		if(value!=null){
			value = String.format(value, arg);
		}
		return value;
	}
	
	/**
	 * 带参数
	 * 
	 * @author PengYang
	 * @date 2017-06-16
	 * 
	 * @param key
	 * @param args
	 * @return String
	 */
	public String getProperty(String key, Object ... args) {
		String value = getProperty(key);
		if(value!=null){
			value = String.format(value, args);
		}
		return value;
	}
	

}

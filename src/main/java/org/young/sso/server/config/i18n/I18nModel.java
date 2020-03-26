package org.young.sso.server.config.i18n;

import com.alibaba.fastjson.JSON;

/**
 * 返回信息
 * @author pengy
 * 2018年10月22日
 */
public class I18nModel {
	
	/**标题**/
	private String title;
	/**详细信息**/
	private String detail;
	/**I18n属性KEY**/
	private String key;
	/**I18n属性值参数**/
	private Object[] args;

	public I18nModel() {
	}
	
	public I18nModel(String key) {
		this.key = key;
	}

	public I18nModel(String key, Object...args) {
		this.key = key;
		this.args = args;
	}
	

	public String getKey() {
		return key;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}

package org.young.sso.server.model;

import org.young.sso.server.beans.BaseEntity;

public class UserInfo extends BaseEntity {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 主键 **/
    private Long id;
    
    /** 用户名称 **/
    private String username;
    
    /** 真实姓名 **/
    private String realName;

    /** 密码 **/
    private String password;

    /** 电话 **/
    private String phone;

    /** 邮箱 **/
    private String email;
    
    /** 备注 **/
    private String note;

    /** 状态(-1：未激活，0:正常(默认)，1：失效) **/
    private Integer status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
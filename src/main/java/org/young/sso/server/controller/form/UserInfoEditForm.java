package org.young.sso.server.controller.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.young.sso.sdk.resource.BaseBean;
import org.young.sso.server.config.I18nCodes;

import io.swagger.annotations.ApiModelProperty;

public class UserInfoEditForm extends BaseBean implements I18nCodes {
	
	private static final long serialVersionUID = 1L;

    /** 真实姓名 **/
	@ApiModelProperty(required=true)
	@Length(min=1, max=255, message=MSGC000006)
    private String realName;

    /** 电话 **/
	@ApiModelProperty(required=true)
	@Pattern(regexp="[0-9]{11}", message=MSGC000007)
    private String phone;

    /** 邮箱 **/
	@ApiModelProperty(required=true)
	@Email(message=MSGC000007)
    private String email;
	
    /** 备注 **/
	@Length(min=0, max=255, message=MSGC000006)
    private String note;

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
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

}
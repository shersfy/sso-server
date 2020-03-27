package org.young.sso.server.config.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.sso.sdk.exception.SsoException;
import org.young.sso.sdk.resource.SsoResult;
import org.young.sso.sdk.resource.SsoResult.ResultCode;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;

public class SmsSender {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(SmsSender.class);
	
	private SmsSingleSender sender;
	
	private SmsSenderProperties properties;

	public SmsSender(SmsSenderProperties properties) {
		super();
		this.properties = properties;
		this.sender = new SmsSingleSender(properties.getAppid(), properties.getAppkey());
	}
	
	public SsoResult send(String phone, int templateId, String[] params) {
		// 签名参数未提供或者为空时，会使用默认签名发送短信
		SsoResult res = new SsoResult();
		try {
			SmsSingleSenderResult result = sender.sendWithParam("86", phone, templateId, 
					params, properties.getSmsSign(), "", "");
			res.setCode(result.result);
			res.setMsg(result.errMsg);
			res.setModel(result.sid);
		} catch (Throwable ex) {
			LOGGER.warn("", ex);
			res.setCode(ResultCode.FAIL);
			res.setMsg(SsoException.getRootCauseMsg(ex));
		}
		return res;
	}
	
}

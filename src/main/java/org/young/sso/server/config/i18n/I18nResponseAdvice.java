package org.young.sso.server.config.i18n;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.young.sso.sdk.resource.SsoResult;

/**
 * @author pengy
 * 2018年10月22日
 */
public abstract class I18nResponseAdvice implements ResponseBodyAdvice<Object>{

	@Autowired
	private I18nMessages i18n;
	
	@Autowired
	private I18nConfigProperties config;

	@Override
	public boolean supports(MethodParameter returnType, 
			Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
					ServerHttpResponse response) {
		if (!(body instanceof SsoResult)) {
			return body;
		}
		
		SsoResult res = (SsoResult) body;
		if (res.getModel() instanceof I18nModel) {
			I18nModel model = (I18nModel) res.getModel();
			String lang = request.getHeaders().getFirst(config.getHeaderKey());
			lang = StringUtils.isBlank(lang) ?Locale.CHINA.toString():lang;
					
			if (i18n.getI18n(lang) != null) {
				String msg = i18n.getI18n(lang).getProperty(model.getKey(), model.getArgs());
				res.setMsg(msg);
			}
		}
		
		return res;
	}
	
}

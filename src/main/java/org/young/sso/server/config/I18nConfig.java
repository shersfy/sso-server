package org.young.sso.server.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.young.sso.sdk.resource.SsoResult;
import org.young.sso.server.config.i18n.EnableI18n;
import org.young.sso.server.config.i18n.I18nModel;
import org.young.sso.server.config.i18n.I18nResponseAdvice;

@EnableI18n
@ControllerAdvice("org.young.sso.server.controller")
public class I18nConfig extends I18nResponseAdvice {

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		
		if(body instanceof SsoResult) {
			SsoResult res = (SsoResult)body;
			if(res.getModel() instanceof I18nModel) {
				I18nModel model = (I18nModel) res.getModel();
				String key = model.getKey();
				if(I18nCodes.MSGC000001.equals(key)) {
					Object[] args = model.getArgs();
					long time     = System.currentTimeMillis()-Long.parseLong(response.getHeaders().getFirst("request_start"));
					List<Object> list = new ArrayList<>();
					list.addAll(Arrays.asList(args));
					list.add(time/1000.0f);
					
					model.setArgs(list.toArray());
					res.setModel(model);
				}
			}
		}
		return super.beforeBodyWrite(body, returnType, selectedContentType, selectedConverterType, request, response);
	}

}

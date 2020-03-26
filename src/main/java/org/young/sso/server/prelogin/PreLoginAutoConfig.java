package org.young.sso.server.prelogin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.server.service.UserInfoService;

@Configuration
@EnableConfigurationProperties(PreLoginProperties.class)
@ConditionalOnProperty(prefix=PreLoginProperties.PREFIX, value="enabled", havingValue="true")
public class PreLoginAutoConfig {
	
	@Bean
	public FilterRegistrationBean<PreLoginFilter> preLoginFilter(@Autowired UserInfoService userInfoService, 
			@Autowired SsoProperties ssoProperties, 
			@Autowired PreLoginProperties preLoginProperties){
		
		PreLoginFilter filter = new PreLoginFilter(userInfoService, ssoProperties, preLoginProperties);
		
		FilterRegistrationBean<PreLoginFilter> bean = new FilterRegistrationBean<>();
		bean.setOrder(0);
		bean.setFilter(filter);
		bean.setName("preLoginFilter");
		bean.addUrlPatterns("/*");
		return bean;
	}

}

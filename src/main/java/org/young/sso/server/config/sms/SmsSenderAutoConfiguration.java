package org.young.sso.server.config.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SmsSenderProperties.class)
public class SmsSenderAutoConfiguration {
	
	@Autowired
	private SmsSenderProperties properties;
	
	@Bean
	public SmsSender smsSender() {
		return new SmsSender(properties);
	}
	
}

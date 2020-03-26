package org.young.sso.server.config.email;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EmailSenderProperties.class)
public class EmailSenderAutoConfiguration {
	
	
}

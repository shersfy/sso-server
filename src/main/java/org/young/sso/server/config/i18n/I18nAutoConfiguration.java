package org.young.sso.server.config.i18n;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * @author pengy
 * 2018年10月22日
 */
@Configuration
@ConditionalOnProperty(prefix=I18nConfigProperties.PREFIX, value="enabled", havingValue="true")
@EnableConfigurationProperties(I18nConfigProperties.class)
public class I18nAutoConfiguration {
	
	@Autowired
	private I18nConfigProperties config;
	
	@Bean
	@ConditionalOnProperty(prefix=I18nConfigProperties.PREFIX, value="enabled", havingValue="true")
	public I18nMessages i18n() throws IOException {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] arr = resolver.getResources(config.getLocation());
		Map<String, I18nProperties> map = new HashMap<>();
		for (Resource res :arr) {
			String[] names = FilenameUtils.getBaseName(res.getFilename()).split("_");
			if (names.length!=3) {
				continue;
			}
			I18nProperties prop = new I18nProperties();
			prop.load(new InputStreamReader(res.getInputStream(), "UTF8"));
			res.getInputStream().close();
			map.put(new Locale(names[1], names[2]).toString(), prop);
		}
		return new I18nMessages(map);
	}
}

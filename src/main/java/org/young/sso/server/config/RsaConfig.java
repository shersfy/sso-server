package org.young.sso.server.config;

import java.io.InputStream;
import java.security.KeyPair;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties.KeyStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.security.rsa.crypto.RsaSecretEncryptor;
import org.young.sso.server.config.RsaKeyPair.RsaJsEncryptor;

@Lazy
@Configuration
@EnableConfigurationProperties(RsaPropertiesExt.class)
public class RsaConfig {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(RsaConfig.class);
	
	@Autowired
	private KeyProperties key;
	
	@Autowired
	private RsaPropertiesExt properties;
	
	@Bean
	public RsaKeyPair rsaKeyPair() {
		KeyStore ks = key.getKeyStore();
		KeyStoreKeyFactory keyFactory = new KeyStoreKeyFactory(ks.getLocation(), ks.getPassword().toCharArray());
		KeyPair kp  = keyFactory.getKeyPair(ks.getAlias());
		
		RsaSecretEncryptor sec = new RsaSecretEncryptor(kp);
		RsaJsEncryptor js = new RsaJsEncryptor(kp.getPublic(), kp.getPrivate());
		js.setRsaProperties(properties);
		
		InputStream input = null;
		try {
			if (properties.getPemPubLocation()!=null) {
				input = properties.getPemPubLocation().getInputStream();
				List<String> lines = IOUtils.readLines(input);
				StringBuilder pem  = new StringBuilder();
				lines.forEach(line->{
					if (!line.startsWith("----")) {
						pem.append(line);
					}
				});
				js.setPemPubKey(pem.toString());
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			IOUtils.closeQuietly(input);
		}
		
		RsaKeyPair rsa = new RsaKeyPair(ks, kp);
		rsa.setRsaSecretEncryptor(sec);
		rsa.setRsaJsEncryptor(js);
		return rsa;
	}

}

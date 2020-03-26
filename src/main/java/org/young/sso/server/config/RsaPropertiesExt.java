package org.young.sso.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.bootstrap.encrypt.RsaProperties;
import org.springframework.core.io.Resource;
import org.springframework.security.rsa.crypto.RsaAlgorithm;

@ConditionalOnClass(RsaAlgorithm.class)
@ConfigurationProperties("encrypt.rsa")
public class RsaPropertiesExt extends RsaProperties {
	
	private int maxBytesLength = 1024;
	
	private int maxDecryptBlockLength = 128;
	
	private int maxEncryptBlockLength = 117;
	
	private Resource pemPubLocation;

	public int getMaxBytesLength() {
		return maxBytesLength;
	}

	public void setMaxBytesLength(int maxBytesLength) {
		this.maxBytesLength = maxBytesLength;
	}

	public int getMaxDecryptBlockLength() {
		return maxDecryptBlockLength;
	}

	public void setMaxDecryptBlockLength(int maxDecryptBlockLength) {
		this.maxDecryptBlockLength = maxDecryptBlockLength;
	}

	public int getMaxEncryptBlockLength() {
		return maxEncryptBlockLength;
	}

	public void setMaxEncryptBlockLength(int maxEncryptBlockLength) {
		this.maxEncryptBlockLength = maxEncryptBlockLength;
	}

	public Resource getPemPubLocation() {
		return pemPubLocation;
	}

	public void setPemPubLocation(Resource pemPubLocation) {
		this.pemPubLocation = pemPubLocation;
	}
	
}

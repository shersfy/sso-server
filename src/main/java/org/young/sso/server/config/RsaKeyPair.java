package org.young.sso.server.config;

import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.Cipher;

import org.apache.commons.io.IOUtils;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties.KeyStore;
import org.springframework.security.rsa.crypto.RsaSecretEncryptor;
import org.springframework.util.Assert;

public class RsaKeyPair {
	
	private KeyStore keyStore;
	
	private KeyPair keyPair;
	
	private RsaJsEncryptor rsaJsEncryptor;
	
	private RsaSecretEncryptor rsaSecretEncryptor;
	
	public RsaKeyPair(KeyStore keyStore, KeyPair keyPair) {
		super();
		this.keyStore = keyStore;
		this.keyPair = keyPair;
	}

	public KeyStore getKeyStore() {
		return keyStore;
	}

	public void setKeyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
	}

	public KeyPair getKeyPair() {
		return keyPair;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	public RsaSecretEncryptor getRsaSecretEncryptor() {
		return rsaSecretEncryptor;
	}

	public void setRsaSecretEncryptor(RsaSecretEncryptor rsaSecretEncryptor) {
		this.rsaSecretEncryptor = rsaSecretEncryptor;
	}
	
	/**
	 * RSA前端JS使用公钥加密，后端服务器使用私钥解密
	 * @author pengy
	 * @date 2018年11月7日
	 */
	public static class RsaJsEncryptor {
		
		private PublicKey publicKey;
		
		private PrivateKey privateKey;
		
		private RsaPropertiesExt rsaProperties;
		
		private String pemPubKey;
		
		public RsaJsEncryptor(PublicKey publicKey, PrivateKey privateKey) {
			super();
			this.publicKey = publicKey;
			this.privateKey = privateKey;
		}

		/**
		 * RSA使用公钥加密
		 * @param data 待加密数据
		 * @return 已加密数据
		 * @throws Exception
		 */
		public byte[] encrypt(byte[] data) throws Exception {
			Assert.notNull(publicKey, "The publicKey must not be null");
			Assert.notNull(data, "The data must not be null");
			if (data.length > rsaProperties.getMaxBytesLength()) {
				throw new IllegalArgumentException(String.format("Data must not be longer than %s bytes", rsaProperties.getMaxBytesLength()));
			}
			
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			byte[] cache = null;
			
			int maxBlock = rsaProperties.getMaxEncryptBlockLength();
			int length   = data.length;
			
	        int offSet = 0;
	        int count  = 0;
			try {
		        // 对数据分段解密
		        while (length-offSet > 0) {
		            if (length-offSet > maxBlock) {
		                cache = cipher.doFinal(data, offSet, maxBlock);
		            } else {
		                cache = cipher.doFinal(data, offSet, length-offSet);
		            }
		            bytes.write(cache, 0, cache.length);
		            
		            count++;
		            offSet = count * maxBlock;
		        }
		        
				Encoder encoder = Base64.getEncoder();
				return encoder.encode(bytes.toByteArray());
				
			} finally {
				IOUtils.closeQuietly(bytes);
			}
		}
		
		public String decrypt(String data) throws Exception {
			byte[] arr  = decrypt(data.getBytes("UTF-8"));
			return new String(arr, "UTF-8" );
		}
		
		/**
		 * RSA使用私钥解密
		 * @param data 待解密数据
		 * @return 已解密数据
		 * @throws Exception
		 */
		public byte[] decrypt(byte[] data) throws Exception {
			Assert.notNull(privateKey, "The privateKey must not be null");
			Assert.notNull(data, "The data must not be null");
			
			Decoder decoder = Base64.getDecoder();
			byte[] buffer   = decoder.decode(data);
	        
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			byte[] cache = null;
			
			int maxBlock = rsaProperties.getMaxDecryptBlockLength();
			int length   = buffer.length;
			
	        int offSet = 0;
	        int count  = 0;
			try {
		        // 对数据分段解密
		        while (length-offSet > 0) {
		            if (length-offSet > maxBlock) {
		                cache = cipher.doFinal(buffer, offSet, maxBlock);
		            } else {
		                cache = cipher.doFinal(buffer, offSet, length-offSet);
		            }
		            bytes.write(cache, 0, cache.length);
		            
		            count++;
		            offSet = count * maxBlock;
		        }
		        
				return bytes.toByteArray();
				
			} finally {
				IOUtils.closeQuietly(bytes);
			}
		}
		
		public PublicKey getPublicKey() {
			return publicKey;
		}

		public void setPublicKey(PublicKey publicKey) {
			this.publicKey = publicKey;
		}

		public PrivateKey getPrivateKey() {
			return privateKey;
		}

		public void setPrivateKey(PrivateKey privateKey) {
			this.privateKey = privateKey;
		}

		public RsaPropertiesExt getRsaProperties() {
			return rsaProperties;
		}

		public void setRsaProperties(RsaPropertiesExt rsaProperties) {
			this.rsaProperties = rsaProperties;
		}

		public String getPemPubKey() {
			return pemPubKey;
		}

		public void setPemPubKey(String pemPubKey) {
			this.pemPubKey = pemPubKey;
		}

		
	}

	public RsaJsEncryptor getRsaJsEncryptor() {
		return rsaJsEncryptor;
	}

	public void setRsaJsEncryptor(RsaJsEncryptor rsaJsEncryptor) {
		this.rsaJsEncryptor = rsaJsEncryptor;
	}

}

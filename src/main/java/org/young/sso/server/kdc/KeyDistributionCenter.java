package org.young.sso.server.kdc;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.sdk.resource.ServiceTicket;
import org.young.sso.sdk.resource.SsoResult;
import org.young.sso.server.beans.BaseEntity;
import org.young.sso.server.beans.Const;
import org.young.sso.server.config.i18n.I18nModel;
import org.young.sso.server.mapper.BaseMapper;
import org.young.sso.server.service.impl.BaseServiceImpl;
import org.young.sso.server.utils.AesUtil;
import org.young.sso.server.utils.DateUtil;
import org.young.sso.server.utils.MD5Util;

@Component
public class KeyDistributionCenter extends BaseServiceImpl<BaseEntity, Long>{
	
	@Autowired
	private StringRedisTemplate redis;
	
	@Autowired
	private SsoProperties ssoProperties;
	
	@Override
	public BaseMapper<BaseEntity, Long> getMapper() {
		return null;
	}
	
	/**
	 * TGC生成ST
	 * @param rk 明文RK
	 * @param tgc 密文TGC
	 * @param apphost 应用host
	 * @return
	 */
	public String generateST(String rk, String tgc, String apphost) {
		
		ServiceTicket st = new ServiceTicket(rk, tgc, apphost);
		String value = st.toString();
		
		st.setTgc(null);
		String enst = st.toString();
		
		try {
			enst = "st-"+MD5Util.encode(enst);
			redis.opsForValue().set(getCacheKey(enst), value, ssoProperties.getStMaxAgeSeconds(), TimeUnit.SECONDS);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		
		return enst;
	}
	
	public String generateRequestKey() {
		StringBuilder rk = new StringBuilder();
		rk.append(RandomStringUtils.randomAlphanumeric(Const.RANDOM_LEN));
		rk.append(System.currentTimeMillis());
		return rk.toString();
	}

	public String generateRequestKey(String remoteAddr) {
		
		String timestamp = DateUtil.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS")
				+RandomStringUtils.randomNumeric(Const.RANDOM_LEN);
		
		String key = String.format("%s_%s_%s", Const.LOGIN_CACHE_KEY_PREFIX, remoteAddr, timestamp);
		String ency = AesUtil.encryptHexStr(key, AesUtil.AES_SEED);
		
		redis.opsForValue().set(key, ency, properties.getRequestKeyMaxAgeSeconds(), TimeUnit.SECONDS);
		return ency;
	}

	public SsoResult checkRequestKey(String key, String remoteAddr) {

		SsoResult res = new SsoResult();
		if (StringUtils.isBlank(key)) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGC000003, "k"));
			return res;
		}
		// 防伪造请求
		if (!key.contains(remoteAddr)) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGE000004));
			return res;
		}

		// key是否有效
		String value = redis.opsForValue().get(key);
		if (StringUtils.isBlank(value) || "deleted".equals(value)) {
			res.setCode(FAIL);
			res.setModel(new I18nModel(MSGE000003));
			return res;
		}

		invalidRequestKey(key);
		return res;

	}

	public void invalidRequestKey(String key) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		redis.opsForValue().set(key, "deleted", redis.getExpire(key), TimeUnit.SECONDS);
	}

	public long countRequestKey(String prefix) {
		int cnt = 0;
		try {
			cnt = redis.keys(String.format("*%s*", prefix)).size();
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return cnt;
	}


}
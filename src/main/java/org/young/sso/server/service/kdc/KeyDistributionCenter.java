package org.young.sso.server.service.kdc;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.young.sso.sdk.autoconfig.SsoProperties;
import org.young.sso.sdk.resource.LoginUser;
import org.young.sso.sdk.resource.SsoResult;
import org.young.sso.sdk.utils.SsoAESUtil;
import org.young.sso.server.beans.BaseEntity;
import org.young.sso.server.beans.Const;
import org.young.sso.server.config.i18n.I18nModel;
import org.young.sso.server.mapper.BaseMapper;
import org.young.sso.server.service.cache.CacheManager;
import org.young.sso.server.service.impl.BaseServiceImpl;
import org.young.sso.server.utils.DateUtil;
import org.young.sso.server.utils.MD5Util;

import com.alibaba.fastjson.JSON;

@Component
public class KeyDistributionCenter extends BaseServiceImpl<BaseEntity, Long>{
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CacheManager cache;
	
	@Autowired
	private SsoProperties ssoProperties;
	
	@Override
	public BaseMapper<BaseEntity, Long> getMapper() {
		return null;
	}
	
	public TicketGrantingTicket getTGT(String session) {
		if (StringUtils.isBlank(session)) {
			return null;
		}
		TicketGrantingTicket obj = null;
		try {
			String text = cache.get(getTGTKey(session));
			obj = JSON.parseObject(text, TicketGrantingTicket.class);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return obj;
	}
	
	public TicketGrantingTicket getTGTByST(String st) {
		if (StringUtils.isBlank(st)) {
			return null;
		}
		
		TicketGrantingTicket obj = null;
		try {
			String text = cache.get(st);
			obj = JSON.parseObject(text, TicketGrantingTicket.class);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return obj;
	}
	
	public boolean removeTGT(String session) {
		if (StringUtils.isBlank(session)) {
			return false;
		}
		try {
			cache.delete(getTGTKey(session));
			return true;
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		
		return false;
	}
	
	/**
	 * 生成TGT，TGC作为key，TGT作为value
	 * @param loginUser 登录用户
	 * @param tgc SSO服务sessionId
	 * @return
	 */
	public String generateTGT(LoginUser loginUser, String tgc) {
		if (loginUser==null) {
			return null;
		}
		
		TicketGrantingTicket tgt = new TicketGrantingTicket(loginUser, tgc);
		String key = getTGTKey(tgc);
		try {
			if (cache.hasKey(key)) {
				return generateTGT(loginUser, tgc);
			}
			cache.set(key, tgt.toString(), ssoProperties.getTgtMaxAgeSeconds(), TimeUnit.SECONDS);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		
		return SsoAESUtil.encryptHexStr(tgt.toString(), tgt.getRandom());
	}
	
	/**
	 *  生成ST作为key，缓存TGT作为value
	 * @param tgt 对象
	 * @param apphost 应用host
	 * @return
	 */
	public String generateST(TicketGrantingTicket tgt, String apphost) {
		
		if (tgt==null) {
			return null;
		}
		
		ServiceTicket st = new ServiceTicket(apphost);
		
		String key = String.format("%s-%s", st.getPrefix(), MD5Util.encode(st.toString()));
		try {
			if (cache.hasKey(key)) {
				return generateST(tgt, apphost);
			}
			cache.set(key, tgt.toString(), ssoProperties.getStMaxAgeSeconds(), TimeUnit.SECONDS);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		
		return key;
	}
	
	public String generateRequestKey(String remoteAddr) {
		
		RequestKey rk = new RequestKey(remoteAddr);
		
		String timestamp = DateUtil.format(rk.getTimestamp(), "yyyyMMddHHmmssSSS");
		timestamp += rk.getRandom();
		
		String key = String.format("%s_%s_%s", 
				Const.LOGIN_CACHE_KEY_PREFIX, rk.getRemoteAddr(), timestamp);
		cache.set(key, rk.toString(), properties.getRequestKeyMaxAgeSeconds(), TimeUnit.SECONDS);
		
		key = SsoAESUtil.encryptHexStr(key, SsoAESUtil.AES_SEED);
		return key;
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
		String value = cache.get(key);
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
		cache.set(key, "deleted", cache.getExpire(key), TimeUnit.SECONDS);
	}

	public long countRequestKey(String prefix) {
		int cnt = 0;
		try {
			cnt = cache.countContainsKeys(prefix);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return cnt;
	}
	
	public String getTGTKey(String session) {
		String key = String.format("%s-%s", Const.TICKET_PREFIX_TGC, session);
		return key;
	}

}

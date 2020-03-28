package org.young.sso.server.service.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.young.sso.server.beans.BaseEntity;
import org.young.sso.server.mapper.BaseMapper;
import org.young.sso.server.service.impl.BaseServiceImpl;

@Component
public class CacheManager extends BaseServiceImpl<BaseEntity, Long>{

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private StringRedisTemplate redis;

	@Override
	public BaseMapper<BaseEntity, Long> getMapper() {
		return null;
	}

	/**
	 * 添加缓存，第一缓存和第二缓存同时添加
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		key = getCacheKey(key);
		// 第一缓存
		ValueOperations<String, String> vo = redis.opsForValue();
		vo.set(key, value);
	}
	/**
	 * 添加缓存，第一缓存和第二缓存同时添加
	 * @param key
	 * @param value
	 * @param timeout
	 * @param timeUnit
	 */
	public void set(String key, String value, long timeout, TimeUnit timeUnit) {

		key = getCacheKey(key);
		// 第一缓存
		ValueOperations<String, String> vo = redis.opsForValue();
		vo.set(key, value, timeout, timeUnit);

	}

	/**
	 * 更新缓存寿命，第一缓存和第二缓存同时更新
	 * @param key
	 * @param timeout
	 * @param timeUnit
	 */
	public void setExpire(String key, long timeout, TimeUnit timeUnit) {
		key = getCacheKey(key);
		// 第一缓存
		redis.expire(key, timeout, timeUnit);

	}

	/**
	 * 查询缓存，第一缓存异常时使用第二缓存
	 * @param key
	 * @return
	 */
	public String get(String key) {
		key = getCacheKey(key);
		ValueOperations<String, String> vo = redis.opsForValue();
		return vo.get(key);
	}

	public Map<Object, Object> getMap(String hashKey){
		hashKey = getCacheKey(hashKey);

		RedisSerializer<?> temp = redis.getHashValueSerializer();

		redis.setHashValueSerializer(new JdkSerializationRedisSerializer());
		Map<Object, Object> data = redis.opsForHash().entries(hashKey);

		redis.setHashValueSerializer(temp);
		return data;
	}

	public void putHash(String key, String hashKey, Object value) {
		key = getCacheKey(key);
		RedisSerializer<?> temp = redis.getHashValueSerializer();

		redis.setHashValueSerializer(new JdkSerializationRedisSerializer());
		redis.opsForHash().put(key, hashKey, value);

		redis.setHashValueSerializer(temp);
	}

	/**
	 * 删除缓存, 第一缓和第二缓存同时删除
	 * @param key
	 * @return
	 */
	public boolean delete(String key) {
		key = getCacheKey(key);
		return redis.delete(key);
	}

	/***
	 * key是否存在，第一缓存异常时使用第二缓存
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key) {
		key = getCacheKey(key);
		// 第一缓存
		return redis.hasKey(key);
	}

	public int countContainsKeys(String subkey) {
		subkey = getCacheKey(subkey);
		return redis.keys(String.format("*%s*", subkey)).size();
	}

	public long increment(String key, int step) {
		key = getCacheKey(key);
		return redis.opsForValue().increment(key, step);
	}

	public long getExpire(String key) {
		key = getCacheKey(key);
		return redis.getExpire(key);
	}

	public Set<String> keys(String pattern) {
		pattern = getCacheKey(pattern);
		return redis.keys(pattern);
	}

}

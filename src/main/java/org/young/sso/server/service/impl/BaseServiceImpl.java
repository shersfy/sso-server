package org.young.sso.server.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.young.sso.server.beans.BaseEntity;
import org.young.sso.server.config.AppConfig;
import org.young.sso.server.config.AppProperties;
import org.young.sso.server.mapper.BaseMapper;
import org.young.sso.server.service.BaseService;

import com.alibaba.fastjson.JSON;

public abstract class BaseServiceImpl<T extends BaseEntity, Id extends Serializable> 
	implements BaseService<T, Id> {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public abstract BaseMapper<T, Id> getMapper();
	
	@Autowired
	protected AppConfig config;
	
	@Autowired
	protected AppProperties properties;
	
	@Override
	public int deleteById(Id id) {
		return getMapper().deleteById(id);
	}
	
	@Override
	public int insert(T entity) {
		if(entity.getCreateTime()==null) {
			entity.setCreateTime(new Date());
		}
		if(entity.getUpdateTime()==null) {
			entity.setUpdateTime(entity.getCreateTime());
		}
		return getMapper().insert(entity);
	}

	@Override
	public T findById(Id id) {
		return getMapper().findById(id);
	}
	
	@Override
	public int updateById(T entity) {
		if(entity.getUpdateTime()==null) {
			entity.setUpdateTime(new Date());
		}
		return getMapper().updateById(entity);
	}

	@Override
	public long findListCount(T where) {
		return getMapper().findListCount(parseMap(where));
	}
	
	@Override
	public List<T> findList(T where) {
		return getMapper().findList(parseMap(where));
	}

	protected Map<String, Object> parseMap(T obj){
		Map<String, Object> map = new HashMap<>();
		map.putAll(JSON.parseObject(JSON.toJSONString(obj)));
		map.put("createTime", obj.getCreateTime());
		map.put("updateTime", obj.getUpdateTime());
		map.put("solrStartTime", obj.getSolrStartTime());
		map.put("solrEndTime", obj.getSolrEndTime());
		map.put("sort", obj.getSort());
		map.put("order", obj.getOrder());
		
		return map;
	}
	
	public <Vo extends T> Vo poToVo(T po, Class<Vo> vclass) {
		if (po==null || vclass==null) {
			return null;
		}
		
		Vo vo = null;
		try {
			vo = vclass.newInstance();
			BeanUtils.copyProperties(vo, po);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		
		return vo;
	}
	
	@Override
	public String getCacheKey(String prefix) {
		prefix = StringUtils.isBlank(prefix)?this.getClass().getName():prefix.trim();
		prefix = String.format("%s:%s", config.getApplication(), prefix);
		return prefix;
	}
	
	@Override
	public String getCacheKey(String methodName, String suffix) {
		methodName = StringUtils.isBlank(methodName)?"":methodName.trim();
		suffix = StringUtils.isBlank(suffix)?"":suffix.trim();
		String key = String.format("%s:%s.%s%s", config.getApplication(), this.getClass().getName(), methodName, suffix);
		return key;
	}
	
	
}

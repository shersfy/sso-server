package org.young.sso.server.mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.young.sso.server.beans.BaseEntity;

public interface BaseMapper<T extends BaseEntity, Id extends Serializable> {
	
	int insert(T entity);

	int deleteById(Id id);
	
	int updateById(T entity);
	
	T findById(Id id);
	
	long findListCount(Map<String, Object> map);
	
	List<T> findList(Map<String, Object> map);
	
}

package org.young.sso.server.service;

import java.io.Serializable;
import java.util.List;

import org.young.sso.sdk.resource.SsoResult.ResultCode;
import org.young.sso.server.beans.BaseEntity;
import org.young.sso.server.config.I18nCodes;

public interface BaseService<T extends BaseEntity, Id extends Serializable> extends I18nCodes{
	
	int SUCESS = ResultCode.SUCESS;
	int FAIL   = ResultCode.FAIL;
	
	int NOR    = 0;
	int DEL    = 1;
	int TMP    = -1;
	
	int deleteById(Id id);
	
	int insert(T entity);

	T findById(Id id);
	
	int updateById(T entity);
	
	long findListCount(T where);
	
	List<T> findList(T where);

}

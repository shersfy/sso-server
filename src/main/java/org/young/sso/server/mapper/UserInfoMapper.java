package org.young.sso.server.mapper;

import org.young.sso.server.model.UserInfo;

public interface UserInfoMapper extends BaseMapper<UserInfo, Long> {
	
	UserInfo findByUser(UserInfo where);

}
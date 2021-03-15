package com.SecureLogin.login.dao;

import org.apache.ibatis.annotations.Select;

import com.SecureLogin.login.domain.UserInfo;

/* 본인의 DB에 알맞게 수정 */
public interface LoginDao {
	@Select("SELECT * FROM USERINFO WHERE USER_ID=#{USER_ID}")
	public UserInfo getLoginInfo(String USER_ID);
}



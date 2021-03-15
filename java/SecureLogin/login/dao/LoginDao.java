package com.SecureLogin.login.dao;

import org.apache.ibatis.annotations.Select;

import com.SecureLogin.login.domain.UserInfo;

/* ������ DB�� �˸°� ���� */
public interface LoginDao {
	@Select("SELECT * FROM USERINFO WHERE USER_ID=#{USER_ID}")
	public UserInfo getLoginInfo(String USER_ID);
}



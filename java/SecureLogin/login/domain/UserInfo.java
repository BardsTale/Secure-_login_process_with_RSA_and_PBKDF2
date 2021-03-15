package com.SecureLogin.login.domain;

/* 기본적인 Domain 설정.
 * 본인의 DB에 알맞게 수정.
 */
public class UserInfo{
	private String USER_ID;
	private String USER_NM;
	private String PASSWD;
	
	public UserInfo(String USER_ID, String USER_NM, String PASSWD) {
		super();
		this.USER_ID = USER_ID;
		this.USER_NM = USER_NM;
		this.PASSWD = PASSWD;
	}

	public String getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(String USER_ID) {
		this.USER_ID = USER_ID;
	}
	
	public String getUSER_NM() {
		return USER_NM;
	}

	public void setUSER_NM(String USER_NM) {
		this.USER_NM = USER_NM;
	}
	
	public String getPASSWD() {
		return PASSWD;
	}

	public void setPASSWD(String PASSWD) {
		this.PASSWD = PASSWD;
	}
}

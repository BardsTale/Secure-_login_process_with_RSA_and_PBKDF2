package com.SecureLogin.login.service;

import java.security.PrivateKey;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.SecureLogin.common.SecureModule;

import com.SecureLogin.login.dao.LoginDao;
import com.SecureLogin.login.domain.UserInfo;


@Service("LoginService")
public class LoginServiceImpl implements LoginService {
	@Autowired
	private LoginDao LoginDao;
	
	@Override
	public ResponseEntity<HashMap<String, Object>> doDecrypt(PrivateKey privateKey, String securedUSERID, String securedPASSWD) {
		String userid = "", password = "";
		HashMap<String, Object> result_map = new HashMap<String, Object>();
		String msg = "로그인에 성공하였습니다.";
		HttpStatus status = HttpStatus.OK;
    	
    	if (privateKey == null) {
        	System.out.println("암호화 비밀키 정보를 찾을 수 없습니다.");
        	msg = "로그인 인증이 만료되었습니다.\n다시 로그인 바랍니다.";
        	status = HttpStatus.GONE;
        }else {
        	try {
        		//복호화 프로세스
        		userid = SecureModule.decryptRsa(privateKey, securedUSERID);
        		password = SecureModule.decryptRsa(privateKey, securedPASSWD);
        		//복호화 한 ID 를 통해 계정 정보를 가져온다.
        		UserInfo userInfo = LoginDao.getLoginInfo(userid);
        		//ID가 체크되면 비밀번호 검사
        		if(userInfo == null) {
        			msg = "존재하지 않는 사용자 계정입니다.";
        			status = HttpStatus.BAD_REQUEST;
        		}else {
        			//PBKDF2 암호화 알고리즘을 돌린 password와 db값이 같으면 통과
        			if(!userInfo.getPASSWD().equals(SecureModule.encryptPBKDF(userid, password))) {
        				//암호 불일치
        				msg = "비밀번호가 틀렸습니다.";
        				status = HttpStatus.BAD_REQUEST;
        			}else {
        				//암호 일치, 로그인 성공
        				result_map.put("user_bean",userInfo);
        			}
        		}
        	} catch (Exception e) {
        		System.out.println(e.getMessage());
        		System.out.println(e);
        		msg = "사용자 인증 중\n오류가 발생하였습니다.";
        		status = HttpStatus.INTERNAL_SERVER_ERROR;
        	}
        }
    	
    	result_map.put("msg",msg);
    	return ResponseEntity.status(status).body(result_map);
	}
}

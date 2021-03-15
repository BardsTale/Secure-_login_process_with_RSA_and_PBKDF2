package com.SecureLogin.login.service;

import java.security.PrivateKey;
import java.util.HashMap;

import org.springframework.http.ResponseEntity;

public interface LoginService {
	public ResponseEntity<HashMap<String, Object>> doDecrypt(PrivateKey privateKey, String securedUSERID, String securedPASSWD);
}

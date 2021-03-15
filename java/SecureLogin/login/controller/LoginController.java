package com.SecureLogin.login.controller;

import java.security.PrivateKey;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.KDTR.login.service.LoginService;



@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private LoginService loginService;
	
	//get 요청 시
	@GetMapping
	public String getLogin(){
		return "login";
	}
		
	//post 요청 시
	@PostMapping
	public @ResponseBody ResponseEntity<HashMap<String, Object>> postLogin(@ModelAttribute @RequestParam(value = "securedUSERID", required = false, defaultValue = "")String securedUSERID 
    		, @RequestParam(value = "securedPASSWD", required = false, defaultValue = "")String securedPASSWD, HttpSession session){
    	//세션에 기존  유저 정보 삭제
    	session.removeAttribute("user_bean");
    	PrivateKey privateKey = (PrivateKey) session.getAttribute("__rsaPrivateKey__");
    	
    	//복호화를 위한 서비스 호출, return 이후 AOP에 기술된 코드에 따라  privateKey는 초기화되고 재발급된다.
    	ResponseEntity<HashMap<String, Object>> result = loginService.doDecrypt(privateKey, securedUSERID, securedPASSWD);
    	
    	//HttpStatus 코드가 OK(200)으로 반환되었을 경우 성공하였으므로 유저 정보를 세션에 담는다.
    	if((boolean) HttpStatus.OK.equals(result.getStatusCode())) {
    		session.setAttribute("user_bean",result.getBody().get("user_bean"));
    	}
    	return result; 
	}
	
}
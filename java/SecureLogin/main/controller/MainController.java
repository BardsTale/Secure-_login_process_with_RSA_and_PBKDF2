package com.SecureLogin.main.controller;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.SecureLogin.login.domain.UserInfo;


@Controller
public class MainController {
	
	@RequestMapping(value = { "/", "index" })
	public ModelAndView mainSetting(HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView("index");//기본 index페이지로 이동
		HashMap<String, Object> result_map = new HashMap<String, Object>();
		
		/* 해당 메소드 진입 시 암호화를 위해 공개키를 생성해서 세션을 통해  사용자에게 전달한다.
		* 세션에 자장된 공개키의 문자열을 키로하여 개인키를 저장한다.
		* 로직은 AOP 방면에서 처리하므로 생략된다.
		*/
		
		//유저 접속 정보 체크, 없을 경우 Login 페이지로 이동한다.
		UserInfo userInfo = (UserInfo)session.getAttribute("user_bean");
		if(userInfo == null) {
			mv.setViewName("login");
		}
		return mv;
	}
}

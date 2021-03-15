package com.SecureLogin.aop;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/* RSA Keygen AOP 
 * ※자신의 스프링 프로젝트 Configuration에 프록시 기반 AOP를 사용하도록 @EnableAspectJAutoProxy를 기술해야 한다.
 */
@Aspect
@Component
public class LoginKeygenAspect {
	//해당 조인 포인트들에 들어올 경우 KeyGen 메소드를 실행시킨다.
	@Pointcut("execution(* com.SecureLogin.login.controller.LoginController.postLogin(..)) "
			+ "|| execution(* com.SecureLogin.main.controller.MainController.mainSetting(..))")
	public void keyGen_Pointcut() {}
	
	/* keyGen_Pointcut() 실행 전 발동. 위에서 설정한 조인포인트들은 아래의 메소드를 타게된다.
	 * 여기서 ModelAndView를 리턴해줄 컨트롤러에서 호출된 메소드와 그외의 메소드를 구분시킨다. 
	 */
	@Before("keyGen_Pointcut()")
	public void keyGen_void(JoinPoint joinPoint) throws Exception {
		String method_name = joinPoint.getSignature().toShortString();
		
		//ModelAndView를 Return 값으로 사용하는 컨트롤러에서 호출되는 메소드는 키젠만 시키므로 아래에 기술.
		String[] method_array = {"mainSetting"};
		if(Arrays.stream(method_array).anyMatch(method_name::contains)) {
			try {
				keygen_proc();
			} catch (Exception e) {
			  //본인에게 알맞는 예외처리 프로세스 기술
				System.out.println(e);
			}
		}
	}
	
	//ResponseEntity<>를 Return 값으로 사용하는 컨트롤러에서 호출되는 메소드들은 아래의 메소드를 사용.
	@AfterReturning(value = "keyGen_Pointcut()", returning = "result")
	public void keyGen_HashMap(JoinPoint joinPoint, ResponseEntity<HashMap<String, Object>> result) throws Exception {
		//AOP에서 세션 접근
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		
		try {
			keygen_proc();
		} catch (Exception e) {
		  //본인에게 알맞는 예외처리 프로세스 기술, ResponseEntity를 쓴다면 HttpStatus를 수정해주는 것도 좋음.
			System.out.println(e);
			result.getBody().put("msg",e);
		}
		result.getBody().put("publicKeyModulus", session.getAttribute("publicKeyModulus"));
		result.getBody().put("publicKeyExponent", session.getAttribute("publicKeyExponent"));
	}
	
	
	//공통으로 쓸 키젠 메소드
	public void keygen_proc() throws Exception{
		//AOP에서 세션 접근
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		
		//세션에 기존의 RSA키 있을 경우 일단 삭제하고 진행.
		session.removeAttribute("__rsaPrivateKey__");
		
		//RSA 키 생성
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(1024);
		
		//key pair 생성
		KeyPair keyPair = generator.genKeyPair();
		
		//key pair의 공개키 비공개키를 각각 놔눔.
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		
		//Key <-> KeySpec 객체를 상호 변환하는 키팩토리 생성
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		
		
		// 세션에 공개키의 문자열을 키로하여 개인키를 저장한다.
		session.setAttribute("__rsaPrivateKey__", privateKey);
		
		// 공개키를 문자열로 변환하여 키팩토리를 통해 JavaScript RSA 라이브러리가 사용토록  넘겨준다.
		RSAPublicKeySpec publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
		
		String publicKeyModulus = publicSpec.getModulus().toString(16);
		String publicKeyExponent = publicSpec.getPublicExponent().toString(16);
		session.setAttribute("publicKeyModulus", publicKeyModulus);
		session.setAttribute("publicKeyExponent", publicKeyExponent);
	}
}

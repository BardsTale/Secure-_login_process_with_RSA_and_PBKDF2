# Secure login process with RSA and PBKDF2
[![SecureLogin-RSA](https://img.shields.io/badge/SecureLogin-RSA-green)](https://github.com/BardsTale/Secure_login_process_with_RSA_and_PBKDF2)
[![SecureLogin-PBKDF2](https://img.shields.io/badge/SecureLogin-PBKDF2-green)](https://github.com/BardsTale/Secure_login_process_with_RSA_and_PBKDF2)

RSA 비대칭 암호화와 PBKDF2 암호 함수를 사용한 보안된 로그인 처리입니다.<br>
<br>
백엔드 방면에서 RSA키 페어를 발급하고 이중 공개키를 세션 혹은 모델에 담아 프론트엔드로 전달합니다.<br>
프론트엔드 방면에선 제공받은 공개키를 활용하는 모듈이 따로 없기 때문에 외부 JavaScript 라이브러리를 통해<br>
발급받은 RSA 공개키를 사용한 암호화를 진행하고 이를 전달하여 SSL을 사용하지 않을 경우 평문이 전달되는 문제점을 최소화합니다.<br>
<br>
강력한 단방향 암호화 함수인 PBKDF2를 통해 암호화 후 DB에 저장을 하고 로그인 처리 시 전달받은 비밀번호를<br>
RSA 복호화와 PBKDF2 통해 암호화하여 DB에 저장되어있는 암호화된 비밀번호와 비교를 합니다.<br>
<br>
비대칭형이지만 보안성 강화를 위해 키는 항상 단발성으로 사용되며 AOP를 통해 주요 구간마다 키를 재발급합니다.<br>
단발성 발급을 원하지 않는다면 AOP 방면의 코드는 제외시켜도 됩니다.<br>

## 목차

- [필요설치](#필요설치)
    - [프론트 엔드](#프론트엔드)
    - [백엔드](#백엔드)
- [사용법](#사용법)
    - [1. 키발급](#키발급)
    - [2. RSA암호화 및 전달](#암호화_및_전달)
    - [3. RSA복호화_및_키재발급](#복호화_및_키재발급)
    - [4. PBKDF2암호화](#암호화)
    - [5. 로그인 처리](#로그인_처리)
- [관련기술](#관련프로젝트)
- [관리자](#관리자)


## 필요설치

사용을 위해 필요한 설치는 Front-End 방면과 Back-End 방면으로 나뉘어집니다.<br>
※ GitHub에 업로드 된 코드들에 대한 설치는 제외합니다.

### 프론트엔드

기본적으로 프론트엔드 방면에서 발급된 RSA 공개키를 사용한 암호화를 위해 외부 라이브러리가 필요합니다.<br>
사용한 라이브러리는 [Stanford University Computer Science](http://www-cs-students.stanford.edu/)에서 제공된 [BSD 라이센스](https://ko.wikipedia.org/wiki/BSD_%ED%97%88%EA%B0%80%EC%84%9C) 라이브러리입니다.<br>

설치는 CDN을 통하여 가능합니다.
```sh
<!-- RSA 암호화 구현, jsbn을 사용.-->
<script type="text/javascript" src="http://www-cs-students.stanford.edu/~tjw/jsbn/rsa.js"></script>
<!-- 기본 BigInteger 구현, RSA 암호화에만 쓰이는 용도 -->
<script type="text/javascript" src="http://www-cs-students.stanford.edu/~tjw/jsbn/jsbn.js"></script>
<!-- rng를 위한 ARC4 기반 백엔드 -->
<script type="text/javascript" src="http://www-cs-students.stanford.edu/~tjw/jsbn/prng4.js"></script>
<!-- 기본적인 엔트로피 수집기 -->
<script type="text/javascript" src="http://www-cs-students.stanford.edu/~tjw/jsbn/rng.js"></script>
```


이 프로젝트의 예제 코드는 JQuery Ajax를 사용한 Login 방식을 사용하고 있기 때문에 기본적으로 [JQuery](https://www.jqwidgets.com/download/)의 설치가 필요합니다.<br>
JQuery는 다음의 CDN과 NPM을 통한 설치를 제공합니다.<br>
>본인만의 프론트엔드 로그인 구성을 고려한다면 이 부분의 설치는 제외하시면 됩니다.

1. CDN을 통한 설치
```sh
#JQuery CDN (JQuery Cookie 미포함)
<script src="https://code.jquery.com/jquery-1.12.4.min.js" integrity="sha256-ZosEbRLbNQzLpnKIkEdrPv7lOy9C27hHQ+Xp8a4MxAQ=" crossorigin="anonymous"></script>
```

2. NPM을 통한 설치
```sh
#JQuery NPM 설치(JQuery Cookie 미포함)
$ npm install jquery
```




### 백엔드
백엔드 방면은 업로드된 코드 외엔 별도로 설치할 것이 없지만, 예제 코드가 템플릿 엔진으로 [Thymeleaf](https://www.thymeleaf.org/)를 쓰고 있기 때문에<br>
Thymeleaf 설치가 필요합니다. 또한 SpringBoot 기반의 코드이기 해당 환경 설정이 필요합니다.<br>
>별도의 템플릿 엔진이나 프레임워크를 사용중이라면 이 부분의 설치는 제외하시면 됩니다.

1. Gradle을 통한 설치
```sh
dependencies {
...
implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
...
}
```

2. Maven을 통한 설치
```sh
...
<dependency>
  <groupId>org.thymeleaf</groupId>
  <artifactId>thymeleaf</artifactId>
  <version>3.0.12.RELEASE</version>
</dependency>
...
```

## 사용법

사용법은 키 발급 -> RSA 암호화 및 전달 -> RSA 복호화 및 키 재발급 -> PBKDF2 암호화 -> 로그인 처리 순으로 처리되며<br>
해당 순서로 기술하겠습니다.

### 키발급
키 발급은 AOP를 통해 특정 조인포인트에서 발급됩니다.
그 대상은 index를 통해 접속했을 시 실행되는 mainSetting() 메소드와 로그인 시도 시 실행되는 postLogin() 메소드가 대상입니다.
위의 두 메소드는 각각 실행 전, 실행 후 키를 발급하게 됩니다.

```sh
#LoginKeygenAspect.java

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
        ...
        keygen_proc(); //키젠 메소드 실랭
        ...
    }
    
    //ResponseEntity<>를 Return 값으로 사용하는 컨트롤러에서 호출되는 메소드들은 아래의 메소드를 사용.
    @AfterReturning(value = "keyGen_Pointcut()", returning = "result")
	public void keyGen_HashMap(JoinPoint joinPoint, ResponseEntity<HashMap<String, Object>> result) throws Exception {
        ...
        keygen_proc(); //키젠 메소드 실랭
        ...
        result.getBody().put("publicKeyModulus", session.getAttribute("publicKeyModulus"));
        result.getBody().put("publicKeyExponent", session.getAttribute("publicKeyExponent"));
    }
}
```

AOP를 통해 발급된 RSA키 페어는 기본적으로 Session에 저장되며 이중 공개키는 아래의 방식으로 전달됩니다.

1. 페이지 접속 시 session을 통해 취득.
```sh
#login.html
...
<!-- RSA키 -->
<input type="hidden" id="rsaPublicKeyModulus" th:value="${session.publicKeyModulus}" />
<input type="hidden" id="rsaPublicKeyExponent" th:value="${session.publicKeyExponent}" />
...
```

2. 로그인 시도 후 response 값에서 갱신된 키 취득.
```sh
#login.js
...
$.ajax({
    ...
    complete: function(response) {
       ...
        let data = response.responseJSON;
        //재발급된 공개키 적용
        $("#rsaPublicKeyModulus").val(data.publicKeyModulus);  
        $("#rsaPublicKeyExponent").val(data.publicKeyExponent);
        ...
   }
});
...
```

### 암호화_및_전달
암호화 및 전달은 login.js의 doLogin() 함수를 내에서 이뤄집니다.

1. 발급받은 공개키를 이용하여 입력한 계정과 비밀번호에 대해 rsa암호화를 진행하고 암호화된 문자를 폼에 기입합니다.
```sh
#login.js

function doLogin(){
    try{
        ...
        //패스워드 암호화
        var rsaPublicKeyModulus =  $("#rsaPublicKeyModulus").val();
        var rsaPublicKeyExponent = $("#rsaPublicKeyExponent").val();
        encryptedForm(USERID, PASSWD, rsaPublicKeyModulus, rsaPublicKeyExponent);
        ...
    } catch(err){
        ...
    }
}
...
function encryptedForm(USERID, PASSWD, rsaPublicKeyModulus, rsaPpublicKeyExponent){
    //RSAKey 객체 발급
    ...

    // 사용자ID와 비밀번호를 RSA로 암호화한다.
    ...

    // POST 로그인 폼에 값을 설정한다.
    var securedLoginForm = document.getElementById("login_form");
    securedLoginForm.securedUSERID.value = securedUSERID;
    securedLoginForm.securedPASSWD.value = securedPASSWD;
}
```

2. Ajax를 통해 암호화된 문자가 입력된 Form을 직렬화하여 POST 방식으로 제출합니다. 

```sh
#login.js

...
$.ajax({
    type: "POST", timeout: 600000, 
    url: $("#login_form").attr('action'),
    data: $("#login_form").serialize(),
    dataType:"json",
    async: false,
    complete: function(response) {
        ...
    }
});
...
```

### 복호화_및_키재발급
프론트엔드에서 암호화하여 전달받은 값들을 백엔드 방면에서 개인키를 사용하여 복호화를 하며<br>
이때 보안성 강화를 위해 키를 재발급합니다.<br>
1. Controller에서 Post 요청을 받고 복호화 처리를 진행합니다. 수행 후 return 시 AOP에서 키를 재발급하고 result 객체에 담아서 보냅니다.
```sh
#LoginController.java

@Controller
@RequestMapping("/login")
public class LoginController {
    ...
    @PostMapping
    public @ResponseBody ResponseEntity<HashMap<String, Object>> postLogin(..){
        //세션에 기존  유저 정보 삭제
        session.removeAttribute("user_bean");
        PrivateKey privateKey = (PrivateKey) session.getAttribute("__rsaPrivateKey__");

        //복호화를 위한 서비스 호출, return 이후 AOP에 기술된 코드에 따라  privateKey는 초기화되고 재발급된다.
        ResponseEntity<HashMap<String, Object>> result = loginService.doDecrypt(privateKey, securedUSERID, securedPASSWD);
        ...
        return result; 
    }
}
```

2. doDecrypt() 메소드에서 복호화 진행.
```sh
#LoginServiceImpl.java

@Service("LoginService")
public class LoginServiceImpl implements LoginService {
	@Autowired
	private LoginDao LoginDao;
	
	@Override
	public ResponseEntity<HashMap<String, Object>> doDecrypt(..) {
        try{
            ...
            
            //복호화 프로세스
            userid = SecureModule.decryptRsa(privateKey, securedUSERID);
            password = SecureModule.decryptRsa(privateKey, securedPASSWD);

            //복호화 한 ID를 통해 계정 정보를 가져온다.
        	UserInfo userInfo = LoginDao.getLoginInfo(userid);
            
            ...
            
        } catch (Exception e) {
            ...
        }
    }
}
```

### 암호화
PBKDF2는 NIST(National Institute of Standards and Technology, 미국표준기술연구소)에 의해서 승인된 알고리즘이며<br>
구현하기 쉬우면서도 검증된 해쉬함수와 솔트, 스트레칭 등을 사용하여 보안성이 강화되어 있습니다.<br>
<br>
이 프로젝트에선 doDecrypt() 메소드에서 복호화 한 비밀번호를 단방향 암호화인 PBKDF2 함수와 MD5를 통해 암호화하고 이를 비교합니다.<br>
>※ 최초 비밀번호 저장 시 위의 방식대로 암호화하여 저장시켜놓아야 합니다.
```sh
#SecureModule.java

public static String encryptPBKDF(String userid, String passwd) throws Exception{
	/* PBKDF 암호화 알고리즘 */
	/**
	* @param   alg     HMAC algorithm to use.
	* @param   P       Password.
	* @param   S       Salt.
	* @param   c       Iteration count.
	* @param   dkLen   Intended length, in octets, of the derived key.
	*/
	String alg ="HmacSHA256"; //사용 해쉬함수 알고리즘
	byte[] byte_Pass = passwd.getBytes("UTF-8");
	byte[] byte_Id = userid.getBytes("UTF-8");

	/* PBKDF2.v1 */
	int c = 10000;
	int dkLen = 60;
	byte[] r1 = PBKDF.pbkdf2(alg, byte_Pass, byte_Id, c, dkLen);

	//DB에 저장하기 용이한 형태인 MD5로 다시 암호화
	return makeMD5(new String(r1, "UTF-8"));
} 
```

### 로그인_처리
doDecrypt() 메소드에서 HttpStatus 코드로 OK(200)를 전달받았다면 로그인에 성공하였으니 로그인을 처리합니다.

1. 백엔드 로그인 성공 확인.
```sh
#LoginController.java

@Controller
@RequestMapping("/login")
public class LoginController {
    ...
    @PostMapping
    public @ResponseBody ResponseEntity<HashMap<String, Object>> postLogin(..){
        ...
        
        //복호화를 위한 서비스 호출, return 이후 AOP에 기술된 코드에 따라  privateKey는 초기화되고 재발급된다.
    	ResponseEntity<HashMap<String, Object>> result = loginService.doDecrypt(privateKey, securedUSERID, securedPASSWD);
    	
    	//HttpStatus 코드가 OK(200)으로 반환되었을 경우 성공하였으므로 유저 정보를 세션에 담는다.
    	if((boolean) HttpStatus.OK.equals(result.getStatusCode())) {
    		session.setAttribute("user_bean",result.getBody().get("user_bean"));
    	}
    	return result;
    }
}
```

2. 프론트엔드 로그인 처리.
```sh
#login.js

...
$.ajax({
    ...
    complete: function(response) {
        //응답값이 없을 경우 0을 반환한다.
        if(response.status == 0){
            alert("서버와 연결이 끊어졌습니다.");
            return;
        }
        let data = response.responseJSON;
        let msg = data.msg;
        
        //재발급된 공개키 적용
        $("#rsaPublicKeyModulus").val(data.publicKeyModulus);  
        $("#rsaPublicKeyExponent").val(data.publicKeyExponent);
        let data = response.responseJSON;
        let msg = data.msg;
        
        //400이상의 HttpStatus 코드는 오류 목록이므로 오류로 처리.
        if(response.status >= 400){
            alert("로그인 인증에 실패하였습니다.\n"+msg);
            return; 
        }

        //로그인에 성공하였으므로 index 페이지로 이동.
        location.replace("index");
   }
});
...
```


## 관련프로젝트

RSA and ECC in JavaScript [Link](http://www-cs-students.stanford.edu/~tjw/jsbn/)<br>
PBKDF2 [Link](https://www.javadoc.io/doc/com.lambdaworks/scrypt/1.2.1/com/lambdaworks/crypto/PBKDF.html)<br>



## 관리자

[@BardsTale](https://github.com/BardsTale).

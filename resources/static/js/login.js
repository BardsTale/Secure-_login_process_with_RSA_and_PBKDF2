$(document).ready(function(){
  /* 로그인을 위한 이벤트 메소드를 할당한다. */
  //로그인 버튼 클릭
	$("#loginBtn").click(function()  { 
	    doLogin();
	});
	
	//패스워드 창에서 엔터 키 입력
	$('#PASSWD').keydown(function(event){
		if(event.keyCode==13){ doLogin();}
	});
});

function doLogin(){
  var USERID = $("#USERID").val();
  var PASSWD = $("#PASSWD").val();
    
  if (!USERID || !PASSWD) {
    alert("아이디와 비밀번호를 입력하세요.");
    return; 
  }
  try {
    //패스워드 암호화
    var rsaPublicKeyModulus =  $("#rsaPublicKeyModulus").val();
  	var rsaPublicKeyExponent = $("#rsaPublicKeyExponent").val();
  	encryptedForm(USERID, PASSWD, rsaPublicKeyModulus, rsaPublicKeyExponent);
    
    try {
      $.ajax({
    		type: "POST", timeout: 600000, 
    		url: $("#login_form").attr('action'),
    		data: $("#login_form").serialize(),
    		dataType:"json",
    		async: false,
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
  	} catch(err) {
  	  alert("로그인 도중 에러가 발생하였습니다.");
  		return;
  	}
  }catch(err){
    alert("암호화 도중 에러가 발생하였습니다.");
		return;
  }
}

function encryptedForm(USERID, PASSWD, rsaPublicKeyModulus, rsaPpublicKeyExponent) {
	//RSAKey 객체 발급
    var rsa = new RSAKey();
    rsa.setPublic(rsaPublicKeyModulus, rsaPpublicKeyExponent);
    
    // 사용자ID와 비밀번호를 RSA로 암호화한다.
    var securedUSERID = rsa.encrypt(USERID);
    var securedPASSWD = rsa.encrypt(PASSWD);

    // POST 로그인 폼에 값을 설정한다.
    var securedLoginForm = document.getElementById("login_form");
    securedLoginForm.securedUSERID.value = securedUSERID;
    securedLoginForm.securedPASSWD.value = securedPASSWD;
}
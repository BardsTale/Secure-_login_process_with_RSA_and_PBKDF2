# Secure login process with RSA and PBKDF2
[![SecureLogin-RSA](https://img.shields.io/badge/SecureLogin-RSA-green)](https://github.com/BardsTale/Secure_login_process_with_RSA_and_PBKDF2)
[![SecureLogin-PBKDF2](https://img.shields.io/badge/SecureLogin-PBKDF2-green)](https://github.com/BardsTale/Secure_login_process_with_RSA_and_PBKDF2)

RSA 비대칭 암호화와 PBKDF2 암호 함수를 사용한 보안된 로그인 처리입니다.<br>
<br>
백엔드 방면에서 RSA키 페어를 발급하고 이중 공개키를 세션 혹은 모델에 담아 프론트엔드로 전달합니다.<br>
프론트엔드 방면에선 제공받은 공개키를 활용하는 모듈이 따로 없기 때문에 외부 JavaScript 라이브러리를 통해<br>
발급받은 RSA 공개키를 사용한 암호화를 진행하고 이를 전달하여 SSL을 사용하지 않을 경우 평문이 전달되는 문제점을 최소화합니다.<br>
<br>
그리고 백엔드 방면에선 개인키를 통해 복호화를 진행한 후<br>
강력한 단방향 암호화 함수인 PBKDF2를 통해 암호화 후 DB에 저장하기 좋은 MD5로 또 다시 암호화 하여 DB에 저장합니다.<br>
<br>
비대칭형이지만 보안성 강화를 위해 키는 항상 단발성으로 사용되며 AOP를 통해 주요 구간마다 키를 재발급합니다.<br>
단발성 발급을 원하지 않는다면 AOP 방면의 코드는 제외시켜도 됩니다.<br>

## 목차

- [필요설치](#필요설치)
    - [프론트엔드](#프론트엔드)
    - [백엔드](#백엔드)
- [사용법](#사용법)
    - [프론트엔드](#프론트엔드_사용법)
    - [백엔드](#백엔드_사용법)
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


이 프로젝트의 예제 코드는 JQuery Ajax를 사용한 Login 방식을 사용하고 있기 때문에 기본적으로 [JQuery](https://www.jqwidgets.com/download/)의 설치가 필요하며<br>
부가적인 UI/UX 구성을 위해 [SweetAlert2](https://sweetalert2.github.io/#download)의 설치가 필요합니다.<br>
JQuery와 SweetAlert2는 다음의 CDN과 NPM을 통한 설치를 제공합니다.<br>
>본인만의 프론트엔드 로그인 구성을 고려한다면 이 부분의 설치는 제외하시면 됩니다.

1. CDN을 통한 설치
```sh
#JQuery CDN (JQuery Cookie 미포함)
<script src="https://code.jquery.com/jquery-1.12.4.min.js" integrity="sha256-ZosEbRLbNQzLpnKIkEdrPv7lOy9C27hHQ+Xp8a4MxAQ=" crossorigin="anonymous"></script>

#SweetAlert2 CDN
<script src="//cdn.jsdelivr.net/npm/sweetalert2@10"></script>
```

2. NPM을 통한 설치
```sh
#JQuery NPM 설치(JQuery Cookie 미포함)
$ npm install jquery

#SweetAlert2 NPM 설치
$ npm install sweetalert2
```


### 백엔드


## 사용법


### 프론트엔드_사용법


### 백엔드_사용법


## 관련프로젝트

RSA and ECC in JavaScript [Link](http://www-cs-students.stanford.edu/~tjw/jsbn/)<br>


## 관리자

[@BardsTale](https://github.com/BardsTale).

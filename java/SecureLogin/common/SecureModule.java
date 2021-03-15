package com.SecureLogin.common;

import java.security.PrivateKey;

import javax.crypto.Cipher;

public class SecureModule {
	/* 로그인용 모듈 */
    //복호화 메소드
	public static String decryptRsa(PrivateKey privateKey, String securedValue) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        byte[] encryptedBytes = hexToByteArray(securedValue);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        String decryptedValue = new String(decryptedBytes, "utf-8"); // 문자 인코딩 주의.
        return decryptedValue;
    }

    /**
     * 16진 문자열을 byte 배열로 변환한다.
     */
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            return new byte[]{};
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            byte value = (byte)Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[(int) Math.floor(i / 2)] = value;
        }
        return bytes;
    }
    
    // md5 생성
    public static String makeMD5(String param) {
		StringBuffer md5 = new StringBuffer();
		try {
			byte[] digest = java.security.MessageDigest.getInstance("MD5")
					.digest(param.getBytes());
			for (int i = 0; i < digest.length; i++) {
				md5.append(Integer.toString((digest[i] & 0xf0) >> 4, 16));
				md5.append(Integer.toString(digest[i] & 0x0f, 16));
			}
		} catch (java.security.NoSuchAlgorithmException ne) {
			ne.printStackTrace();
		}
		return md5.toString().toUpperCase();
	}
    
    // PBKDF 암호화
    public static String encryptPBKDF(String userid, String passwd) throws Exception{
    	/* PBKDF 암호화 알고리즘 */
		/**
		* @param   alg     HMAC algorithm to use.
		* @param   P       Password.
		* @param   S       Salt.
		* @param   c       Iteration count.
		* @param   dkLen   Intended length, in octets, of the derived key.
		*/
		String alg ="HmacSHA256";
		byte[] byte_Pass = passwd.getBytes("UTF-8");
		byte[] byte_Id = userid.getBytes("UTF-8");
		
		/* PBKDF2.v1 */
		int c = 10000;
		int dkLen = 60;
		byte[] r1 = PBKDF.pbkdf2(alg, byte_Pass, byte_Id, c, dkLen);
		
		//DB에 저장하기 용이한 형태인 MD5로 다시 암호화
		return makeMD5(new String(r1, "UTF-8"));
	} 
}

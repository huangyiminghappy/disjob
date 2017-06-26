package com.huangyiming.disjob.console.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class EncryptUtils {
	private static final String ENCODING = "UTF-8";
	private static final String MD5 = "MD5";
	private static final String AES = "AES";
//    private static SecretKey secretKey = null;
	
	/**
	 * 计算摘要，返回32位的MD5值，加密串为SAFE_CODE
	 * 
	 * @param str
	 * @param encoding
	 * @return 
	 */
	public static byte[] getMessageDigest(String str, String encoding) {
		try {
			MessageDigest digest = MessageDigest.getInstance(MD5);
			digest.update(str.getBytes(encoding));
			return digest.digest();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getMessageDigest(String str) {
		return getMessageDigest(str, ENCODING);
	}

	private static String byte2HexString(byte[] b) {
		int i;
		StringBuffer buf = new StringBuffer("");
		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if (i < 0)
				i += 256;
			if (i < 16)
				buf.append("0");
			buf.append(Integer.toHexString(i));
		}
		return buf.toString();

	}
	
	/**
	 * md5加密
	 * @param str 需要加密的明文
	 * @return md5 密码串
	 */
	public static String encryptByMD5(String str) {
		return byte2HexString(getMessageDigest(str));
	}

	private static byte[] hex2Byte(String src) {
		if (src.length() < 1)
			return null;
		byte[] encrypted = new byte[src.length() / 2];
		for (int i = 0; i < src.length() / 2; i++) {
			int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);

			encrypted[i] = (byte) (high * 16 + low);
		}
		return encrypted;
	}
    
    // 根据密钥串生成密钥对象
    public static SecretKey getSecretKey(String keyStr) {    	
    	try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
			SecureRandom secureRandom = new SecureRandom(keyStr.getBytes(ENCODING));
			keyGenerator.init(128, secureRandom);
			return keyGenerator.generateKey();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    /**
     * 使用AES加密
     * @param str 要加密的字符串明文
     * @param keyStr 密钥
     * @return 加密后的字符串密文
     */
	public static String encryptByAES(String str, String keyStr) {
		SecretKey secretKey = getSecretKey(keyStr);
		return encryptByAES(str, secretKey);
	}

	/**
	 * 使用AES解密
	 * @param str 要解密的字符串密文
	 * @param keyStr 密钥
	 * @return 解密后的明文
	 */
	public static String decryptByAES(String str, String keyStr) {
		SecretKey secretKey = getSecretKey(keyStr);
		return decryptByAES(str, secretKey);
	}
	
	/**
     * 使用AES加密
     * @param str 要加密的字符串明文
     * @param keyStr 密钥对象
     * @return 加密后的字符串密文
     */
	public static String encryptByAES(String str, SecretKey secretKey) {
		try {			
			Cipher cipher = Cipher.getInstance(AES);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] result =  cipher.doFinal(str.getBytes(ENCODING));
			return byte2HexString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 使用AES解密
	 * @param str 要解密的字符串密文
	 * @param secretKey 密钥对象
	 * @return 解密后的明文
	 */
	public static String decryptByAES(String str, SecretKey secretKey) {
		try {
			Cipher cipher = Cipher.getInstance(AES);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] result = cipher.doFinal(hex2Byte(str));
			return new String(result,ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void main(String args[]) {
		String str = "aaabbbccc";
		String key = "zoshow";

		long start = System.currentTimeMillis();
		String result = encryptByMD5(str + key);
		System.out.println("md5 result:" + result);
		System.out.println(System.currentTimeMillis() - start);
		
		start = System.currentTimeMillis();
		SecretKey secretKey = getSecretKey(key);
		System.out.println("生成密钥: " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		result = encryptByAES(str, secretKey);
		System.out.println("aes decrypt result:" + result);
		System.out.println("加密: " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		result = decryptByAES(result, secretKey);
		System.out.println("aes decrypt result:" + result);
		System.out.println("解密: " + (System.currentTimeMillis() - start));
		
//		String str = "aaabbbccc";
//		String key = "zoshow";
//		
//		System.out.println(EncryptUtils.encryptByAES(str, key));
	}
}

package com.light.hexo.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Author MoonlightL
 * @Title: AesUtil
 * @ProjectName: hexo-boot
 * @Description: 加密工具
 * @DateTime: 2022/5/19 11:48
 */
public class AesUtil {

	public static final String CHARSET = "UTF-8";

	public static final String AES_SECRET_KEY = "bWFsbHB3ZA==WNST";

	private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

	private AesUtil() {}

	/**
	 * AES 加密
	 * @param content 明文
	 * @return
	 */
	public static String encrypt(String content) {
		try {
			return base64Encode(aesEncryptToBytes(content, AES_SECRET_KEY));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * AES 解密
	 * @param encrypt 密文
	 * @return
	 */
	public static String decrypt(String encrypt) {
		try {
			return StringUtils.isBlank(encrypt) ? null : aesDecryptByBytes(base64Decode(encrypt), AES_SECRET_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * base 64 encode
	 * @param bytes
	 * @return
	 */
	public static String base64Encode(byte[] bytes){
		return Base64.encodeBase64String(bytes);
	}

	/**
	 * base 64 decode
	 * @param base64Code
	 * @return
	 * @throws Exception
	 */
	public static byte[] base64Decode(String base64Code) throws Exception{
		return StringUtils.isBlank(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);
	}


	/**
	 * AES 加密
	 * @param content
	 * @param encryptKey
	 * @return
	 * @throws Exception
	 */
	private static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);
		Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));

		return cipher.doFinal(content.getBytes(CHARSET));
	}

	/**
	 * AES 解密
	 * @param encryptBytes
	 * @param decryptKey
	 * @return
	 * @throws Exception
	 */
	private static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);

		Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
		byte[] decryptBytes = cipher.doFinal(encryptBytes);
		return new String(decryptBytes);
	}

}

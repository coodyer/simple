package com.app.server.comm.util;

import java.net.URLEncoder;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;


public final class EncryptUtil {

	public final static String CHARSET = "UTF-8";

	private EncryptUtil() {
		throw new Error("Utility classes should not instantiated!");
	}

	public static String decryptMessage(String msg, String key)
			throws Exception {
		byte[] data = TripleDESUtil.decrypt(Base64.decodeBase64(Base64.decodeBase64(msg)),
				Base64.decodeBase64(key));
		return new String(data, CHARSET);
	}

	/**
	 * 解密
	 * 
	 * @param msg
	 *            待解密字符串
	 * @param charset
	 *            编码
	 * @param key
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static String decryptMessage(String msg, String charset, String key) throws Exception {
		byte[] data = TripleDESUtil.decrypt(Base64.decodeBase64(Base64.decodeBase64(msg)),
				Base64.decodeBase64(key));
		return new String(data, charset);
	}

	/**
	 * 加密
	 * 
	 * @param msg
	 * @param key
	 *            密钥
	 * @return
	 * @throws Exception
	 * @author hanweizhao
	 */
	public static String encryptMessage(String msg, String key)
			throws Exception {
		byte[] data = TripleDESUtil.encrypt(msg.getBytes(CHARSET),
				Base64.decodeBase64(key));
		String str= Base64.encodeBase64String(data);
		return  Base64.encodeBase64String(str.getBytes(CHARSET));
	}
	
	
	
	public static String md5Code(String pwd) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(pwd.getBytes(CHARSET));
			byte b[] = md.digest();

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

		} catch (Exception e) {

		}
		return "";
	}

	private static String textCode(String s, String key) {
		String str = "";
		int ch;
		if (key.length() == 0) {
			return s;
		} else if (!s.equals(null)) {
			for (int i = 0, j = 0; i < s.length(); i++, j++) {
				if (j > key.length() - 1) {
					j = j % key.length();
				}
				ch = s.codePointAt(i) + key.codePointAt(j);
				if (ch > 65535) {
					ch = ch % 65535;// ch - 33 = (ch - 33) % 95 ;
				}
				str += (char) ch;
			}
		}
		return str;

	}

	@SuppressWarnings("unused")
	private static String textDeCode(String s, String key) {
		String str = "";
		int ch;
		if (key.length() == 0) {
			return s;
		} else if (!s.equals(key)) {
			for (int i = 0, j = 0; i < s.length(); i++, j++) {
				if (j > key.length() - 1) {
					j = j % key.length();
				}
				ch = (s.codePointAt(i) + 65535 - key.codePointAt(j));
				if (ch > 65535) {
					ch = ch % 65535;// ch - 33 = (ch - 33) % 95 ;
				}
				str += (char) ch;
			}
		}
		return str;
	}

	public static String customEnCode(String str) {
		try {
			str = md5Code(str);
			str = textCode(str, str);
			str = str.substring(1, str.length() - 1);
			str = URLEncoder.encode(str, "UTF-8").replace("%", "")
					.toLowerCase();
			str = md5Code(str);
			return str;
		} catch (Exception e) {
			return "";
		}

	}
	
	
	public static void main(String[] args) throws Exception {
		System.out.println(customEnCode("123456"));
	}

}

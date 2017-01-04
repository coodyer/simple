package com.app.server.comm.entity;

import java.util.HashMap;
import java.util.Map;

public class HttpEntity {
	@SuppressWarnings("unused")
	private String html;
	private byte[] bye;
	private String cookie;
	private Integer code=-1;
	private Map<String,String> headMap;
	public Map<String, String> getHeadMap() {
		return headMap;
	}

	public void setHeadMap(Map<String, String> headMap) {
		this.headMap = headMap;
	}

	private String encode="UTF-8";
	public String getHtml() {
		try {
		if(bye==null){
			return null;
		}
			String str= new String(bye, encode);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public byte[] getBye() {
		return bye;
	}

	public void setBye(byte[] bye) {
		this.bye = bye;
	}

	public Map<String, String> getCookieMap() {
		if (cookie == null) {
			return null;
		}
		Map<String, String> cookieMap = new HashMap<String, String>();
		String[] cookies = cookie.split(";");
		for (String cook : cookies) {
			String[] tmps = cook.split("=");
			if (tmps.length >= 2) {
				String cookieValue = "";
				for (int i = 1; i < tmps.length; i++) {
					cookieValue += tmps[i];
					if (i < tmps.length) {
						cookieValue += "=";
					}
				}
				cookieMap.put(tmps[0].trim(), cookieValue.trim());
			}
		}
		return cookieMap;
	}
}

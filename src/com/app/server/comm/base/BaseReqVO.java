package com.app.server.comm.base;

import com.app.server.comm.annotation.ParamCheck;
import com.app.server.comm.avafinal.FormatFinal;

@SuppressWarnings("serial")
public  class BaseReqVO extends BaseModel {
	private String action;
	//0代表安卓，其他代表IOS
	@ParamCheck(allowNull=true,format=FormatFinal.NUMBER)
	private Integer osType;
	//系统类型
	private String osVersion;
	//手机唯一标识
	private String imei;
	//版本号
	@ParamCheck(allowNull=true,format=FormatFinal.NUMBER)
	private Integer version;
	//用户ID
	@ParamCheck(allowNull=true,format=FormatFinal.NUMBER)
	private Long userId;
	//登录授权key
	private String token;

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getOsType() {
		return osType;
	}

	public void setOsType(Integer osType) {
		this.osType = osType;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}

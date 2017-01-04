package com.app.server.vo;

import com.app.server.comm.annotation.ParamCheck;
import com.app.server.comm.base.BaseReqVO;

@SuppressWarnings("serial")
public class UserReqVO extends BaseReqVO{

	@ParamCheck
	private String userName;
	@ParamCheck
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}

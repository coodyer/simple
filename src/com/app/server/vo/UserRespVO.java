package com.app.server.vo;

import com.app.server.comm.base.BaseRespVO;
import com.app.server.model.SimpleUser;

@SuppressWarnings("serial")
public class UserRespVO extends BaseRespVO{

	private SimpleUser userInfo;

	public SimpleUser getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(SimpleUser userInfo) {
		this.userInfo = userInfo;
	}
	
}

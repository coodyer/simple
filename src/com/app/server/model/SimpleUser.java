package com.app.server.model;

import com.app.server.comm.base.BaseModel;

@SuppressWarnings("serial")
public class SimpleUser extends BaseModel{

	private Integer id;
	private String user;
	private String pwd;
	private Integer iden;
	public Integer getIden() {
		return iden;
	}
	public void setIden(Integer iden) {
		this.iden = iden;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}

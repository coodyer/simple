package com.app.server.year.domain;

import com.app.server.comm.base.BaseModel;

public class BankYearTop extends BaseModel{

	private String uid;
	private String vote;
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getVote() {
		return vote;
	}
	public void setVote(String vote) {
		this.vote = vote;
	}
	
}

package com.app.server.year.domain;

import java.util.Date;

import com.app.server.comm.base.BaseModel;

@SuppressWarnings("serial")
public class YearVoteRecord extends BaseModel{

	private Integer id;
	private Long currUid;
	private Long targeUid;
	private Integer vote;
	private Date createDate;
	private Integer type;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getCurrUid() {
		return currUid;
	}
	public void setCurrUid(Long currUid) {
		this.currUid = currUid;
	}
	public Long getTargeUid() {
		return targeUid;
	}
	public void setTargeUid(Long targeUid) {
		this.targeUid = targeUid;
	}
	public Integer getVote() {
		return vote;
	}
	public void setVote(Integer vote) {
		this.vote = vote;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}

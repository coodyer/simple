package com.app.server.year.domain;


import java.util.Date;

import com.app.server.comm.base.BaseModel;

/**
 * YearUser entity. @author MyEclipse Persistence Tools
 */

@SuppressWarnings("serial")
public class YearUser extends BaseModel{

	// Fields

	private Integer uid;
	private Integer remainVote;
	private Integer inVote;
	private Integer totalInVote;
	private Integer outVote;
	private Integer score;
	private Date updateTime;
	private Integer userLevel;

	// Constructors

	/** default constructor */
	public YearUser() {
	}

	public Integer getTotalInVote() {
		return totalInVote;
	}

	public void setTotalInVote(Integer totalInVote) {
		this.totalInVote = totalInVote;
	}

	/** full constructor */
	public YearUser(Integer uid, Integer remainVote, Integer inVote,
			Integer outVote, Integer score, Date updateTime) {
		this.uid = uid;
		this.remainVote = remainVote;
		this.inVote = inVote;
		this.outVote = outVote;
		this.score = score;
		this.updateTime = updateTime;
	}

	// Property accessors

	public Integer getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(Integer userLevel) {
		this.userLevel = userLevel;
	}

	public Integer getUid() {
		return this.uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getRemainVote() {
		return this.remainVote;
	}

	public void setRemainVote(Integer remainVote) {
		this.remainVote = remainVote;
	}

	public Integer getInVote() {
		return this.inVote;
	}

	public void setInVote(Integer inVote) {
		this.inVote = inVote;
	}

	public Integer getOutVote() {
		return this.outVote;
	}

	public void setOutVote(Integer outVote) {
		this.outVote = outVote;
	}

	public Integer getScore() {
		return this.score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
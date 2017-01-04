package com.app.server.year.domain;

import com.app.server.comm.base.BaseModel;

/**
 * YearTop entity. @author MyEclipse Persistence Tools
 */

@SuppressWarnings("serial")
public class YearTop extends BaseModel{

	// Fields

	private Integer id;
	private Integer uid;
	private Integer inVote;
	private Integer score;
	private String date;
	private Integer type;
	private Integer matchId;
	private Integer hasReset;
	private Integer userLevel;
	private Integer totalInVote;
	private Integer isExpire;


	public Integer getTotalInVote() {
		return totalInVote;
	}

	public Integer getIsExpire() {
		return isExpire;
	}

	public void setIsExpire(Integer isExpire) {
		this.isExpire = isExpire;
	}

	public void setTotalInVote(Integer totalInVote) {
		this.totalInVote = totalInVote;
	}

	public Integer getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(Integer userLevel) {
		this.userLevel = userLevel;
	}

	/** default constructor */
	public YearTop() {
	}

	public Integer getHasReset() {
		return hasReset;
	}

	public void setHasReset(Integer hasReset) {
		this.hasReset = hasReset;
	}

	/** minimal constructor */
	public YearTop(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public YearTop(Integer id, Integer uid, Integer inVote, Integer score,
			String date, Integer type, Integer matchId) {
		this.id = id;
		this.uid = uid;
		this.inVote = inVote;
		this.score = score;
		this.date = date;
		this.type = type;
		this.matchId = matchId;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUid() {
		return this.uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getInVote() {
		return this.inVote;
	}

	public void setInVote(Integer inVote) {
		this.inVote = inVote;
	}

	public Integer getScore() {
		return this.score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getMatchId() {
		return this.matchId;
	}

	public void setMatchId(Integer matchId) {
		this.matchId = matchId;
	}

}